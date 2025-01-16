package com.example.ticketeventandroid;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.ticketeventandroid.adapter.QRCodeAdapter;
import com.example.ticketeventandroid.models.QRCode;
import com.example.ticketeventandroid.network.UserManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QRCodeActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private QRCodeAdapter adapter;
    private List<QRCode> qrCodeList = new ArrayList<>();
    private static final String API_URL = getBASE_URL() + "/api/evenement/qrcodes/";

    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityView(R.layout.activity_qrcode);

        // Définir dynamiquement le titre de la Toolbar
        getSupportActionBar().setTitle("Mes QRCodes");

        // Initialisation des vues
        recyclerView = findViewById(R.id.recyclerView);

        // Configuration du RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QRCodeAdapter(this, qrCodeList);
        recyclerView.setAdapter(adapter);

        // Initialisation du UserManager
        userManager = new UserManager(this);

        // Chargement des données
        loadQRCodeData();
    }

    private void loadQRCodeData() {
        RequestQueue queue = Volley.newRequestQueue(this);

        String token = userManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Le token est manquant ou invalide.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API_URL, requestBody,
                response -> {
                    Log.d("Debug", "Réponse reçue : " + response.toString());
                    qrCodeList.clear();
                    try {
                        // Récupérer le tableau
                        JSONArray resultsArray = response.getJSONArray("result");

                        // Parcourir les résultats
                        for (int i = 0; i < resultsArray.length(); i++) {
                            JSONObject evenementObject = resultsArray.getJSONObject(i);
                            String evenementName = evenementObject.getString("evenement");  // Nom de l'événement
                            JSONArray categoriesArray = evenementObject.getJSONArray("categories");

                            // Parcourir chaque catégorie
                            for (int j = 0; j < categoriesArray.length(); j++) {
                                JSONObject categorieObject = categoriesArray.getJSONObject(j);
                                String category = categorieObject.getString("categorie");  // Nom de la catégorie
                                JSONArray qrcodesArray = categorieObject.getJSONArray("qrcodes");

                                // Parcourir les QR Codes de chaque catégorie
                                for (int k = 0; k < qrcodesArray.length(); k++) {
                                    JSONObject qrCodeObject = qrcodesArray.getJSONObject(k);
                                    QRCode qrCode = new QRCode(
                                            qrCodeObject.getString("token"),
                                            qrCodeObject.getBoolean("valide"),
                                            qrCodeObject.getString("qr_image"),
                                            qrCodeObject.getString("date_creation"),
                                            category,
                                            evenementName
                                    );
                                    qrCodeList.add(qrCode);
                                }
                            }
                        }

                        // Notifier l'adaptateur de la mise à jour des données
                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
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

        queue.add(jsonObjectRequest);
    }
}