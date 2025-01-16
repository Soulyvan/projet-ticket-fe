package com.example.ticketeventandroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.ticketeventandroid.models.QRCode;
import com.example.ticketeventandroid.network.ApiService;
import com.example.ticketeventandroid.network.RetrofitClient;
import com.example.ticketeventandroid.network.UserManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
// import retrofit2.Call;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateEventActivity extends BaseActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText eventName, eventDescription, eventType, eventDate, eventLocation;
    private ImageView eventPhotoPreview;
    private Button selectPhotoButton, submitEventButton, addCategoryButton;
    private LinearLayout categoriesContainer;
    private Uri selectedImageUri;

    private UserManager userManager;

    private ProgressDialog progressDialog;

    private static final String API_URL = getBASE_URL() + "/api/evenement/evenements/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityView(R.layout.activity_create_event);

        // Définir dynamiquement le titre de la Toolbar
        getSupportActionBar().setTitle("Création d'évènement");

        userManager = new UserManager(this);

        // Initialisation des vues
        eventName = findViewById(R.id.event_name);
        eventDescription = findViewById(R.id.event_description);
        eventType = findViewById(R.id.event_type);
        eventDate = findViewById(R.id.event_date);
        eventLocation = findViewById(R.id.event_location);
        eventPhotoPreview = findViewById(R.id.event_photo_preview);
        selectPhotoButton = findViewById(R.id.select_photo_button);
        submitEventButton = findViewById(R.id.submit_button);
        addCategoryButton = findViewById(R.id.add_category_button);
        categoriesContainer = findViewById(R.id.categories_container);

        // Action pour sélectionner une photo
        // selectPhotoButton.setOnClickListener(v -> openImagePicker());
        selectPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ouvrir un sélecteur d'images
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        // Action pour ajouter une catégorie
        addCategoryButton.setOnClickListener(v -> addCategory());

        // Action pour soumettre l'événement
        submitEventButton.setOnClickListener(v -> submitForm());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Obtenir l'URI de l'image sélectionnée
            selectedImageUri = data.getData();

            // Utiliser Glide pour afficher l'image sélectionnée dans l'ImageView
            Glide.with(this)
                    .load(selectedImageUri)
                    .into(eventPhotoPreview);  // Mettre l'image dans l'ImageView
        }
    }

    private void addCategory() {
        // Créer un conteneur horizontal pour la catégorie
        LinearLayout categoryLayout = new LinearLayout(this);
        categoryLayout.setOrientation(LinearLayout.HORIZONTAL);
        categoryLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        categoryLayout.setPadding(0, 8, 0, 8);

        // Créer le champ pour le nom de la catégorie
        EditText categoryNameEditText = new EditText(this);
        categoryNameEditText.setHint("Nom de la catégorie");
        categoryNameEditText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        categoryLayout.addView(categoryNameEditText);

        // Créer le champ pour le nombre de billets restants
        EditText categoryTicketsEditText = new EditText(this);
        categoryTicketsEditText.setHint("Billets restants");
        categoryTicketsEditText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        categoryLayout.addView(categoryTicketsEditText);

        // Créer le champ pour le prix
        EditText categoryPriceEditText = new EditText(this);
        categoryPriceEditText.setHint("Prix");
        categoryPriceEditText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        categoryLayout.addView(categoryPriceEditText);

        // Ajouter le conteneur de la catégorie au conteneur principal
        categoriesContainer.addView(categoryLayout);
    }

    private String encodeImageToBase64(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            byte[] byteArray = outputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void submitForm() {
        if (!validateInputs()) {
            return; // Ne pas soumettre si la validation échoue
        }

        // Récupérer les autres champs
        String name = eventName.getText().toString();
        String description = eventDescription.getText().toString();
        String type = eventType.getText().toString();
        String date = eventDate.getText().toString();
        String location = eventLocation.getText().toString();

        // Créer l'objet JSON pour l'événement
        JSONObject eventJson = new JSONObject();
        try {
            eventJson.put("nom", name);
            eventJson.put("description", description);
            eventJson.put("type_evenement", type);
            eventJson.put("date_heure", date);
            eventJson.put("lieu", location);

            // Ajouter la photo si elle est sélectionnée
            if (selectedImageUri != null) {
                String encodedImage = encodeImageToBase64(selectedImageUri);
                eventJson.put("photo", encodedImage);
            }

            // Ajouter les catégories
            JSONArray categoriesArray = new JSONArray();
            for (int i = 0; i < categoriesContainer.getChildCount(); i++) {
                LinearLayout categoryLayout = (LinearLayout) categoriesContainer.getChildAt(i);

                EditText categoryNameEditText = (EditText) categoryLayout.getChildAt(0);
                EditText categoryTicketsEditText = (EditText) categoryLayout.getChildAt(1);
                EditText categoryPriceEditText = (EditText) categoryLayout.getChildAt(2);

                String categoryName = categoryNameEditText.getText().toString();
                String categoryTickets = categoryTicketsEditText.getText().toString();
                String categoryPrice = categoryPriceEditText.getText().toString();

                if (!categoryName.isEmpty() && !categoryTickets.isEmpty() && !categoryPrice.isEmpty()) {
                    JSONObject categoryJson = new JSONObject();
                    categoryJson.put("nom", categoryName);
                    categoryJson.put("billets_restant", categoryTickets);
                    categoryJson.put("prix", categoryPrice);
                    categoriesArray.put(categoryJson);
                }
            }

            eventJson.put("categories", categoriesArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("AFFICHE_JSON", "json : " + eventJson);
        // Envoyer la requête à l'API (fonction fictive)
        sendEventToApi(eventJson);
    }

    private void sendEventToApi(JSONObject eventJson) {
        // Construire les catégories en JSON
        String categories = "";
        try {
            categories = eventJson.getJSONArray("categories").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Construire la requête multipart
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        multipartBuilder.addFormDataPart("nom", eventJson.optString("nom"));
        multipartBuilder.addFormDataPart("description", eventJson.optString("description"));
        multipartBuilder.addFormDataPart("type_evenement", eventJson.optString("type_evenement"));
        multipartBuilder.addFormDataPart("date_heure", eventJson.optString("date_heure"));
        multipartBuilder.addFormDataPart("lieu", eventJson.optString("lieu"));
        multipartBuilder.addFormDataPart("categories", categories);

        // Ajouter l'image si elle est sélectionnée
        if (selectedImageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                byte[] byteArray = outputStream.toByteArray();

                RequestBody fileBody = RequestBody.create(byteArray, MediaType.parse("image/jpeg"));
                multipartBuilder.addFormDataPart("photo", "event_photo.jpg", fileBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        RequestBody requestBody = multipartBuilder.build();

        // Envoyer la requête
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + userManager.getToken())
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(CreateEventActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(CreateEventActivity.this, "Événement créé avec succès!", Toast.LENGTH_SHORT).show());

                    Intent accueilIntent = new Intent(CreateEventActivity.this, MainActivity.class);
                    // Ferme toutes les activités au-dessus d'elle et évite de recréer MainActivity
                    accueilIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(accueilIntent);
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Erreur inconnue";
                    Log.e("Erreur serveur", errorBody);
                    runOnUiThread(() -> Toast.makeText(CreateEventActivity.this, "Erreur : " + errorBody, Toast.LENGTH_LONG).show());
                }
            }
        });




        /*RequestQueue queue = Volley.newRequestQueue(this);

        String token = userManager.getToken();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API_URL, eventJson,
                response -> {
                    Log.d("Debug", "Réponse reçue : " + response.toString());
                    try {
                        Toast.makeText(this, "Evènement crée avec succès.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this, "Erreur de parsing JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Gestion des erreurs réseau
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorMsg = new String(error.networkResponse.data);
                        Log.e("ServerError", "Erreur serveur : " + errorMsg);
                        Toast.makeText(this, "Erreur : " + errorMsg, Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("ServerError", "Aucune réponse du serveur");
                        Toast.makeText(this, "Erreur de connexion au serveur", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);*/




        /*// Créer la requête HTTP
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(eventJson.toString(), JSON);
        Request request = new Request.Builder()
                .url(getBASE_URL() + "/api/evenement/evenements/")
                .addHeader("Authorization", "Bearer " + userManager.getToken())
                .post(body)
                .build();

        // Envoyer la requête de manière asynchrone
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Gérer les erreurs de connexion
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Succès
                    Toast.makeText(CreateEventActivity.this, "Événement créé avec succès!", Toast.LENGTH_SHORT).show();
                    // System.out.println("Réponse : " + response.body().string());
                    finish();
                } else {
                    // Afficher la réponse complète de l'API en cas d'échec
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Aucune réponse";
                        Log.d("API_Error", "Response error: " + errorBody);
                    } catch (IOException e) {
                        Log.e("API_Error", "Error reading response body", e);
                    }
                    Toast.makeText(CreateEventActivity.this, "Erreur lors de la création de l'événement.", Toast.LENGTH_SHORT).show();
                }
            }
        });



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getBASE_URL()) // Remplacez par l'URL de votre API
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService eventApi = RetrofitClient.getInstance().create(ApiService.class);

        // Afficher la barre de progression
        showProgressDialog();

        Call<ResponseBody> call = eventApi.createEvent(eventJson, "token " + userManager.getToken());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dismissProgressDialog();
                if (response.isSuccessful()) {
                    Toast.makeText(CreateEventActivity.this, "Événement créé avec succès!", Toast.LENGTH_SHORT).show();
                    finish(); // Fermer l'activité après soumission
                } else {
                    // Afficher la réponse complète de l'API en cas d'échec
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Aucune réponse";
                        Log.d("API_Error", "Response error: " + errorBody);
                    } catch (IOException e) {
                        Log.e("API_Error", "Error reading response body", e);
                    }
                    Toast.makeText(CreateEventActivity.this, "Erreur lors de la création de l'événement.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dismissProgressDialog();
                Log.e("API_Failure", "Connection failed", t); // Ajouter un log pour l'échec de la connexion
                Toast.makeText(CreateEventActivity.this, "Échec de la connexion à l'API.", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    private boolean validateInputs() {
        String name = eventName.getText().toString();
        String description = eventDescription.getText().toString();
        String type = eventType.getText().toString();
        String date = eventDate.getText().toString();
        String location = eventLocation.getText().toString();

        if (name.isEmpty()) {
            eventName.setError("Le nom de l'événement est requis.");
            return false;
        }

        if (description.isEmpty()) {
            eventDescription.setError("La description est requise.");
            return false;
        }

        if (type.isEmpty()) {
            eventType.setError("Le type d'événement est requis.");
            return false;
        }

        if (date.isEmpty()) {
            eventDate.setError("La date et l'heure sont requis.");
            return false;
        }

        if (location.isEmpty()) {
            eventLocation.setError("Le lieu est requis.");
            return false;
        }

        return true;
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Création de l'événement...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}