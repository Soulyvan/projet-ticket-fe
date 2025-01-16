package com.example.ticketeventandroid;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ticketeventandroid.models.InscriptionReponse;
import com.example.ticketeventandroid.models.InscriptionRequete;
import com.example.ticketeventandroid.network.ApiService;
import com.example.ticketeventandroid.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class InscriptionActivity extends BaseActivity {

    private EditText emailField, passwordField;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityView(R.layout.activity_inscription);

        // Définir dynamiquement le titre de la Toolbar
        getSupportActionBar().setTitle("Inscription");

        emailField = findViewById(R.id.email_input);
        passwordField = findViewById(R.id.password_input);
        progressBar = findViewById(R.id.progress_bar);
        Button registerButton = findViewById(R.id.btn_inscription);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(InscriptionActivity.this, "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show();
                } else {
                    performRegistration(email, password);
                }
            }
        });
    }

    private void performRegistration(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        InscriptionRequete registerRequest = new InscriptionRequete(email, password);

        apiService.registerUser(registerRequest).enqueue(new Callback<InscriptionReponse>() {
            @Override
            public void onResponse(Call<InscriptionReponse> call, Response<InscriptionReponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(InscriptionActivity.this, "Inscription réussie !", Toast.LENGTH_SHORT).show();

                    // Redirigez vers l'activité Connexion
                    Intent intent = new Intent(InscriptionActivity.this, ConnexionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(InscriptionActivity.this, "Erreur lors de l'inscription", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InscriptionReponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(InscriptionActivity.this, "Une erreur s'est produite : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}