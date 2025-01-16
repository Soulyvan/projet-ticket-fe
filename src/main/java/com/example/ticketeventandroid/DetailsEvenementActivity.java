package com.example.ticketeventandroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.ticketeventandroid.adapter.CategoriesAdapter;
import com.example.ticketeventandroid.models.CategorieEvenement;
import com.example.ticketeventandroid.models.Evenement;
import com.example.ticketeventandroid.network.UserManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.paymentsheet.PaymentSheet;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DetailsEvenementActivity extends BaseActivity {

    private Evenement evenement;
    private ImageView eventImage;
    private TextView eventName, eventLocation, eventDescription, eventDate, eventType;
    private RecyclerView categoriesRecyclerView;
    private CategoriesAdapter categoriesAdapter;
    private Button payerTicketButton, supprimerEvent;

    private UserManager userManager;

    private final String clePublique = "pk_test_51NvXIPCBYqfJw3ZTXv92VgTx095EZPpguihA2GRYyS9EbiRlY3eMBMrlaAruDRvserUJLS43pDNCgjI9p077luLq008XRKoRxw";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_details_evenement);
        setActivityView(R.layout.activity_details_evenement);

        // Définir dynamiquement le titre de la Toolbar
        getSupportActionBar().setTitle("Détails");

        // Initialisation des vues
        eventImage = findViewById(R.id.eventImage);
        eventName = findViewById(R.id.eventName);
        eventLocation = findViewById(R.id.eventLocation);
        eventDescription = findViewById(R.id.eventDescription);
        eventDate = findViewById(R.id.eventDate);
        eventType = findViewById(R.id.eventType);
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        payerTicketButton = findViewById(R.id.btn_payer_ticket);
        supprimerEvent = findViewById(R.id.btn_supprimer_event);

        userManager = new UserManager(this);

        // Si l'utilisateur est connecté, on affiche le bouton de Paiement des tickets
        if (userManager.isLoggedIn()) { // Remplacez par votre méthode de vérification
            payerTicketButton.setVisibility(View.VISIBLE);
            if (userManager.isOrganizer()) {
                supprimerEvent.setVisibility(View.VISIBLE);
            }
        } else {
            payerTicketButton.setVisibility(View.GONE);
            supprimerEvent.setVisibility(View.GONE);
        }

        // Récupérer les données de l'événement depuis l'Intent
        evenement = getIntent().getParcelableExtra("evenement");

        // Afficher les données dans les vues
        eventName.setText(evenement.getNom());
        eventLocation.setText(evenement.getLieu());
        eventDate.setText(formatDate(evenement.getDateHeure()));
        eventType.setText(evenement.getTypeEvenement());
        if (evenement.getDescription() != null && !evenement.getDescription().trim().isEmpty()) {
            eventDescription.setText(evenement.getDescription()); // Afficher la description si elle n'est pas vide
        } else {
            eventDescription.setText("Aucune description disponible."); // Afficher un texte par défaut si la description est vide
        }

        // au clic du bouton supprimer l'évènement
        // au clic du bouton supprimer l'évènement
        supprimerEvent.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Voulez-vous vraiment supprimer cette évènement ? Cette action est irréversible.")
                .setPositiveButton("Oui", (dialog, which) -> {
                    supprimerEvenement(evenement.getId());
                })
                .setNegativeButton("Non", (dialog, which) -> {
                    // Fermer la boîte de dialogue
                    dialog.dismiss();
                })
                .show();
        });


        // Charger l'image avec Glide
        Glide.with(this)
                .load(getBASE_URL() + evenement.getPhoto())
                .into(eventImage);

        // Initialiser le RecyclerView pour afficher les catégories
        List<CategorieEvenement> categories = evenement.getCategories();
        categoriesAdapter = new CategoriesAdapter(categories);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoriesRecyclerView.setAdapter(categoriesAdapter);

        // Initialisation de Stripe
        PaymentConfiguration.init(
                this,
                clePublique
        );

        // Vérifiez si l'Activity a été ouverte via un deep link
        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            String host = data.getHost(); // Récupère "payment-success" ou "payment-cancel"
            String paymentIntent = data.getQueryParameter("payment_intent"); // Ex: "12345"

            if ("payment-success".equals(host)) {
                handlePaymentSuccess(paymentIntent);
            } else if ("payment-cancel".equals(host)) {
                handlePaymentCancel();
            }
        }

        payerTicketButton.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            View bottomSheetView = getLayoutInflater().inflate(R.layout.form_ticket_bottom_sheet, null);
            bottomSheetDialog.setContentView(bottomSheetView);

            Spinner spinnerCategories = bottomSheetView.findViewById(R.id.spinner_categories);
            EditText etNombreTickets = bottomSheetView.findViewById(R.id.et_nombre_tickets);
            Button btnEnvoyerPaiement = bottomSheetView.findViewById(R.id.btn_envoyer_paiement);

            // Extraire les noms des catégories
            List<CategorieEvenement> mesCategories = evenement.getCategories();
            List<String> categorieNoms = new ArrayList<>();
            AtomicInteger nbTicketsCategorieSelectionnee = new AtomicInteger();
            for (CategorieEvenement categorie : mesCategories) {
                categorieNoms.add(categorie.getNom());
            }

            // Remplir le spinner avec les catégories
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, categorieNoms); // catégorieNoms = ["standart", "vip"]
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategories.setAdapter(adapter);

            // Action pour le bouton de paiement
            btnEnvoyerPaiement.setOnClickListener(view -> {
                String categorieSelectionnee = spinnerCategories.getSelectedItem().toString();
                String nombreTickets = etNombreTickets.getText().toString();

                for (CategorieEvenement categorie : mesCategories) {
                    if (categorieSelectionnee == categorie.getNom()) {
                        nbTicketsCategorieSelectionnee.set(categorie.getBilletsRestant());
                        break;
                    }
                }

                if (!nombreTickets.isEmpty() && Integer.parseInt(nombreTickets) > 0 && Integer.parseInt(nombreTickets) <= Integer.parseInt(String.valueOf(nbTicketsCategorieSelectionnee))) {
                    effectuerPaiement(categorieSelectionnee, Integer.parseInt(nombreTickets));
                    bottomSheetDialog.dismiss();
                } else {
                    Toast.makeText(this, "Veuillez entrer un nombre de tickets valide", Toast.LENGTH_SHORT).show();
                }
            });

            bottomSheetDialog.show();
        });
    }

    private String formatDate(String dateString) {
        try {
            // Convertir la chaîne de date en objet Date
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date date = inputFormat.parse(dateString);

            // Définir le format de sortie
            SimpleDateFormat outputFormat = new SimpleDateFormat("d MMMM yyyy 'à' HH:mm");
            return outputFormat.format(date); // Retourne la date formatée
        } catch (Exception e) {
            e.printStackTrace();
            return "Date invalide"; // En cas d'erreur, on retourne une valeur par défaut
        }
    }

    private void effectuerPaiement(String categorie, int nombreTickets) {
        String url = getBASE_URL() + "/api/evenement/qrcode/creer/";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("evenement_id", evenement.getId()); // ID de l'événement
            requestBody.put("categorie_evenement_nom", categorie);
            requestBody.put("nombre_places", nombreTickets);
            requestBody.put("token_user", userManager.getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, requestBody,
                response -> {
                    // Succès
                    try {
                        // Récupérer l'ID de session Stripe et l'url
                        String checkoutSessionId = response.getString("id");
                        String checkoutUrl = response.getString("url");

                        // Rediriger l'utilisateur vers l'URL de paiement
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(checkoutUrl));
                        startActivity(browserIntent);
                        // Toast.makeText(this, "CS: " + checkoutSessionId, Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Erreur
                    Toast.makeText(this, "Erreur lors du paiement", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userManager.getToken());
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void handlePaymentSuccess(String paymentIntent) {
        // Logique pour succès du paiement
        Toast.makeText(this, "Paiement réussi pour : " + paymentIntent, Toast.LENGTH_LONG).show();

        // Mettre à jour l'interface utilisateur ou rediriger l'utilisateur
    }

    private void handlePaymentCancel() {
        // Logique pour annulation ou échec
        Toast.makeText(this, "Le paiement a échoué ou a été annulé", Toast.LENGTH_LONG).show();
    }

    private void supprimerEvenement(int evenementId) {
        String url = getBASE_URL() + "/api/evenement/evenements/" + evenementId + "/supprimer/"; // URL pour supprimer l'événement

        // Créer une requête DELETE pour supprimer l'événement
        JsonObjectRequest deleteRequest = new JsonObjectRequest(
                Request.Method.DELETE, url, null,
                response -> {
                    // Si la suppression est réussie
                    Toast.makeText(DetailsEvenementActivity.this, "Événement supprimé avec succès.", Toast.LENGTH_SHORT).show();

                    Intent accueilIntent = new Intent(DetailsEvenementActivity.this, MainActivity.class);
                    // Ferme toutes les activités au-dessus d'elle et évite de recréer MainActivity
                    accueilIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(accueilIntent);
                    finish(); // Fermer l'activité actuelle
                },
                error -> {
                    // Si erreur lors de la suppression
                    Toast.makeText(DetailsEvenementActivity.this, "Erreur lors de la suppression de l'événement.", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                // Ajouter le token d'authentification
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userManager.getToken());
                return headers;
            }
        };

        // Ajouter la requête à la queue de Volley
        Volley.newRequestQueue(DetailsEvenementActivity.this).add(deleteRequest);
    }

}