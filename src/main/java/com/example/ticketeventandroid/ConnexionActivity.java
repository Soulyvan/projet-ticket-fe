package com.example.ticketeventandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ticketeventandroid.models.ConnexionReponse;
import com.example.ticketeventandroid.models.ConnexionRequete;
import com.example.ticketeventandroid.network.ApiService;
import com.example.ticketeventandroid.network.RetrofitClient;
import com.example.ticketeventandroid.network.UserManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnexionActivity extends BaseActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityView(R.layout.activity_connexion);

        // Définir dynamiquement le titre de la Toolbar
        getSupportActionBar().setTitle("Connexion");

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(ConnexionActivity.this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
                    return;
                }

                loginUser(email, password);
            }
        });
    }

    private void loginUser(String email, String password) {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<ConnexionReponse> call = apiService.login(new ConnexionRequete(email, password));

        call.enqueue(new Callback<ConnexionReponse>() {
            @Override
            public void onResponse(Call<ConnexionReponse> call, Response<ConnexionReponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    boolean isOrganizer = response.body().getUser().isOrganisateur();

                    // Sauvegarder le token et le rôle (organisateur)
                    UserManager userManager = new UserManager(ConnexionActivity.this);
                    userManager.saveToken(token);
                    userManager.saveIsOrganizer(isOrganizer);

                    // Naviguer vers l'écran principal
                    Intent intent = new Intent(ConnexionActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ConnexionActivity.this, "Identifiants incorrects", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ConnexionReponse> call, Throwable t) {
                Toast.makeText(ConnexionActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}