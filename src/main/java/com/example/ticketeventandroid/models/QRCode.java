package com.example.ticketeventandroid.models;

public class QRCode {
    private String token;
    private boolean valide;
    private String qrImage;
    private String dateCreation;
    private String category;  // Nouveau champ pour la catégorie
    private String evenementName;  // Nouveau champ pour le nom de l'événement

    // Constructeur
    public QRCode(String token, boolean valide, String qrImage, String dateCreation, String category, String evenementName) {
        this.token = token;
        this.valide = valide;
        this.qrImage = qrImage;
        this.dateCreation = dateCreation;
        this.category = category;
        this.evenementName = evenementName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isValide() {
        return valide;
    }

    public void setValide(boolean valide) {
        this.valide = valide;
    }

    public String getQrImage() {
        return qrImage;
    }

    public void setQrImage(String qrImage) {
        this.qrImage = qrImage;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEvenementName() {
        return evenementName;
    }

    public void setEvenementName(String evenementName) {
        this.evenementName = evenementName;
    }
}
