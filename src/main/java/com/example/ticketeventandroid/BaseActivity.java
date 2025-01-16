package com.example.ticketeventandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ticketeventandroid.network.ApiService;
import com.example.ticketeventandroid.network.RetrofitClient;
import com.example.ticketeventandroid.network.UserManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity extends AppCompatActivity {

    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base); // Charge le layout de base

        // Initialiser le UserManager
        userManager = new UserManager(this);

        // Configurer la Toolbar comme barre de navigation
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Ajouter un bouton de retour dans toutes les activités sauf MainActivity
        if (getSupportActionBar() != null && !(this instanceof MainActivity)) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // Remplacer la vue par défaut avec celle d'une activité enfant
    protected void setActivityView(@LayoutRes int layoutResID) {
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(layoutResID, contentFrame, true);
    }

    // Charger le menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);  // Charge le menu

        // Cacher l'option de déconnexion si l'utilisateur n'est pas connecté
        MenuItem deconnexionItem = menu.findItem(R.id.menu_option_deconnexion);
        MenuItem mesQRCodes = menu.findItem(R.id.menu_option_qrcodes);
        MenuItem supprimerUser = menu.findItem(R.id.menu_option_supprimer);
        MenuItem creerEvent = menu.findItem(R.id.menu_option_creer_event);

        if (userManager.isLoggedIn()) {
            deconnexionItem.setVisible(true);
            mesQRCodes.setVisible(true);
            supprimerUser.setVisible(true);
            if (userManager.isOrganizer()) {
                creerEvent.setVisible(true);
            }
        } else {
            deconnexionItem.setVisible(false);
            mesQRCodes.setVisible(false);
            supprimerUser.setVisible(false);
            creerEvent.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Vérifie si l'élément cliqué est la flèche de retour (home button)
        if (item.getItemId() == android.R.id.home) {
            // Vérifie si l'activité précédente est MainActivity
            if (!(this instanceof MainActivity)) {
                // Si ce n'est pas MainActivity, revenir à l'activité précédente
                onBackPressed();  // Retourne à l'activité précédente dans la pile
            } else {
                // Si on est déjà dans MainActivity, rien ne se passe ou on peut personnaliser
                finish();  // Ferme l'activité si nécessaire
            }
            return true;
        }

        // Gérer les autres options du menu
        /*if (item.getItemId() == R.id.menu_option_recherche) {
            Toast.makeText(this, "Option de recherche sélectionnée", Toast.LENGTH_SHORT).show();
            return true;
        }*/ if (item.getItemId() == R.id.menu_option_supprimer) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmation")
                    .setMessage("Voulez-vous vraiment supprimer votre compte ? Cette action est irréversible.")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        deleteAccount();
                    })
                    .setNegativeButton("Non", (dialog, which) -> {
                        // Fermer la boîte de dialogue
                        dialog.dismiss();
                    })
                    .show();
            return true;
        } else if (item.getItemId() == R.id.menu_option_accueil) {
            Intent accueilIntent = new Intent(this, MainActivity.class);
            // Ferme toutes les activités au-dessus d'elle et évite de recréer MainActivity
            accueilIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(accueilIntent);
            return true;
        } else if (item.getItemId() == R.id.menu_option_inscription) {
            Intent inscriptionIntent = new Intent(this, InscriptionActivity.class);
            startActivity(inscriptionIntent);
            return true;
        } else if (item.getItemId() == R.id.menu_option_connexion) {
            Intent connexionIntent = new Intent(this, ConnexionActivity.class);
            startActivity(connexionIntent);
            return true;
        } else if (item.getItemId() == R.id.menu_option_qrcodes) {
            if (userManager.isLoggedIn()) {
                Intent qrcodeIntent = new Intent(this, QRCodeActivity.class);
                startActivity(qrcodeIntent);
            }
            return true;
        } else if (item.getItemId() == R.id.menu_option_creer_event) {
            if (userManager.isLoggedIn() && userManager.isOrganizer()) {
                Intent creerEventIntent = new Intent(this, CreateEventActivity.class);
                startActivity(creerEventIntent);
            }
            return true;
        } else if (item.getItemId() == R.id.menu_option_deconnexion) {
            if (userManager.isLoggedIn()) {
                // Déconnecter l'utilisateur
                userManager.logout();
                // Rediriger vers la page de connexion
                Intent intent = new Intent(BaseActivity.this, ConnexionActivity.class);
                startActivity(intent);
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // Récupère l'élément de menu (recherche)
        MenuItem searchItem = menu.findItem(R.id.menu_option_recherche);

        // Si l'activité courante est MainActivity, afficher l'option de recherche
        if (this instanceof MainActivity) {
            searchItem.setVisible(true);
        } else {
            // Sinon, masquer l'option de recherche
            searchItem.setVisible(false);
        }

        return true;
    }

    private void deleteAccount() {
        // Créer une instance d'ApiService
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

        String token = "Token " + userManager.getToken();
        // Appeler la méthode deleteUser
        Call<Void> call = apiService.deleteUser(token);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Compte supprimé avec succès.", Toast.LENGTH_SHORT).show();
                    userManager.logout();

                    // Rediriger vers la page d'accueil après suppression
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class); // Remplace MainActivity par l'activité d'accueil
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Erreur lors de la suppression du compte : " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Échec de la suppression : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static String getBASE_URL() {
        return "http://192.168.1.7:8000";
    }
}