package com.example.ticketeventandroid.network;

import com.example.ticketeventandroid.models.ConnexionReponse;
import com.example.ticketeventandroid.models.ConnexionRequete;
import com.example.ticketeventandroid.models.Evenement;
import com.example.ticketeventandroid.models.InscriptionReponse;
import com.example.ticketeventandroid.models.InscriptionRequete;
import com.example.ticketeventandroid.models.QRCode;

import org.json.JSONObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    // Inscription
    @POST("authentification/inscription/") // Le point d'entrée de l'API
    Call<InscriptionReponse> registerUser(@Body InscriptionRequete inscriptionRequete);

    // Connexion
    @POST("authentification/connexion/")
    @Headers("Content-Type: application/json")
    Call<ConnexionReponse> login(@Body ConnexionRequete connexionRequete);

    // Déconnexion
    @POST("authentification/deconnexion/")
    Call<Void> logout();

    // Suppression
    @DELETE("authentification/suppression/")
    Call<Void> deleteUser(@Header("Authorization") String token);



    // Liste des évènements
    @GET("evenement/evenements/")
    Call<List<Evenement>> getEvenements();

    @Multipart
    @POST("evenement/evenements/")
    Call<ResponseBody> createEvent(
            @Part("nom") RequestBody nom,  // Exemple de champ texte (nom de l'événement)
            @Part("description") RequestBody description,  // Exemple de champ texte (description)
            @Part("type_evenement") RequestBody typeEvenement,  // Exemple de champ texte (type d'événement)
            @Part("date_heure") RequestBody dateHeure,  // Exemple de champ texte (date et heure)
            @Part("lieu") RequestBody lieu,  // Exemple de champ texte (lieu)
            @Part("categories") RequestBody categories,  // Exemple de champ JSON (catégories)
            @Part("photo") MultipartBody.Part photo,  // Envoi de l'image
            @Header("Authorization") String token  // En-tête pour l'authentification
    );

    // Envoi du token via un POST pour récupérer les QR codes associés
    // @POST("evenement/qrcodes/")
    // Call<List<QRCode>> getQRCodes(@Body TokenRequete tokenRequete);
}
