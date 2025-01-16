package com.example.ticketeventandroid;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ticketeventandroid.adapter.EvenementAdapter;
import com.example.ticketeventandroid.models.CustomUser;
import com.example.ticketeventandroid.models.Evenement;
import com.example.ticketeventandroid.network.ApiService;
import com.example.ticketeventandroid.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends BaseActivity {

    private static final String BASE_URL = getBASE_URL() + "/api/";
    private RecyclerView recyclerView;
    private EvenementAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;  // SwipeRefreshLayout pour le rafraîchissement
    List<Evenement> evenements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        setActivityView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewEvenements);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        // Connexion Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // Méthode pour charger les événements
        loadEvenements(apiService);

        // On rafraîchit les événements lors du swipe
        swipeRefreshLayout.setOnRefreshListener(() -> loadEvenements(apiService));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadEvenements(apiService);  // Recharger les événements
            }
        });

    }

    // Méthode pour charger les événements
    private void loadEvenements(ApiService apiService) {
        apiService.getEvenements().enqueue(new Callback<List<Evenement>>() {
            @Override
            public void onResponse(Call<List<Evenement>> call, Response<List<Evenement>> response) {
                if (response.isSuccessful()) {
                    evenements = response.body();
                    adapter = new EvenementAdapter(MainActivity.this, evenements);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(MainActivity.this, "Erreur serveur", Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);  // Arrêter l'animation de rafraîchissement
            }

            @Override
            public void onFailure(Call<List<Evenement>> call, Throwable t) {
                Log.e("API_ERROR", "Erreur : " + t.getMessage());  // Affiche les détails
                Toast.makeText(MainActivity.this, "Erreur : " + t.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);  // Arrêter l'animation de rafraîchissement
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if (item.getItemId() == R.id.menu_option_recherche) {
            // Afficher ou cacher la SearchView
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setVisibility(View.VISIBLE);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // Effectuer la recherche ici avec le texte
                    filterEvenements(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // Effectuer la recherche en temps réel ici
                    filterEvenements(newText);
                    return false;
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void filterEvenements(String query) {
        if (evenements == null || evenements.isEmpty()) return;

        List<Evenement> filteredList = new ArrayList<>();
        for (Evenement evenement : evenements) {
            if (evenement.getNom().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(evenement);
            }
        }

        adapter.updateData(filteredList);
    }
}