package com.example.ticketeventandroid.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Evenement implements Parcelable {
    private int id;
    private String nom;
    private String photo; // URL de l'image
    private String description;
    private String type_evenement;
    private String date_heure; // Format ISO8601 : "YYYY-MM-DDTHH:MM:SS"
    private String lieu;
    private int organisateur;
    private List<CategorieEvenement> categories;

    public Evenement() {

    }

    public Evenement(int id, String nom, String photo, String description, String type_evenement, String date_heure, String lieu, int organisateur, List<CategorieEvenement> categories) {
        this.id = id;
        this.nom = nom;
        this.photo = photo;
        this.description = description;
        this.type_evenement = type_evenement;
        this.date_heure = date_heure;
        this.lieu = lieu;
        this.organisateur = organisateur;
        this.categories = categories;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTypeEvenement() {
        return type_evenement;
    }

    public void setTypeEvenement(String type_evenement) {
        this.type_evenement = type_evenement;
    }

    public String getDateHeure() {
        return date_heure;
    }

    public void setDateHeure(String date_heure) {
        this.date_heure = date_heure;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public int getOrganisateur() {
        return organisateur;
    }

    public void setOrganisateur(int organisateur) {
        this.organisateur = organisateur;
    }

    public List<CategorieEvenement> getCategories() {
        return categories;
    }

    public void setCategories(List<CategorieEvenement> categories) {
        this.categories = categories;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nom);
        dest.writeString(photo);
        dest.writeString(description);
        dest.writeString(type_evenement);
        dest.writeString(date_heure);
        dest.writeString(lieu);
        dest.writeInt(organisateur);
        dest.writeTypedList(categories); // List<CategorieEvenement> is a Parcelable list
    }

    public static final Parcelable.Creator<Evenement> CREATOR = new Parcelable.Creator<Evenement>() {
        @Override
        public Evenement createFromParcel(Parcel in) {
            return new Evenement(in);
        }

        @Override
        public Evenement[] newArray(int size) {
            return new Evenement[size];
        }
    };

    // Constructor for Parcelable
    protected Evenement(Parcel in) {
        id = in.readInt();
        nom = in.readString();
        photo = in.readString();
        description = in.readString();
        type_evenement = in.readString();
        date_heure = in.readString();
        lieu = in.readString();
        organisateur = in.readInt();
        categories = in.createTypedArrayList(CategorieEvenement.CREATOR); // Read the list of CategorieEvenement
    }
}
