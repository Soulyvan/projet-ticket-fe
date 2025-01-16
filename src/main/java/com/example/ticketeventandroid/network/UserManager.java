package com.example.ticketeventandroid.network;

import android.content.Context;
import android.content.SharedPreferences;

public class UserManager {
    private static final String PREFS_NAME = "user_prefs";
    private static final String TOKEN_KEY = "token";
    private static final String IS_ORGANIZER_KEY = "is_organizer";  // Nouvelle clé pour le rôle (organisateur)
    private Context context;

    private SharedPreferences sharedPreferences;

    public UserManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply();
    }

    public String getToken() {
        return sharedPreferences.getString(TOKEN_KEY, null);
    }

    public void clearToken() {
        sharedPreferences.edit().remove(TOKEN_KEY).apply();
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public void logout() {
        // Utiliser le contexte fourni pour accéder aux SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(TOKEN_KEY); // Supprimer le token
        editor.apply(); // Appliquer les changements
    }

    // Sauvegarder si l'utilisateur est un organisateur
    public void saveIsOrganizer(boolean isOrganizer) {
        sharedPreferences.edit().putBoolean(IS_ORGANIZER_KEY, isOrganizer).apply();
    }

    // Récupérer si l'utilisateur est un organisateur
    public boolean isOrganizer() {
        return sharedPreferences.getBoolean(IS_ORGANIZER_KEY, false);  // Par défaut, l'utilisateur n'est pas un organisateur
    }

}
