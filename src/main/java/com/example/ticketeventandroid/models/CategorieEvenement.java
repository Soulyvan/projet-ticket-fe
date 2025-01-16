package com.example.ticketeventandroid.models;

import android.os.Parcel;
import android.os.Parcelable;

public class CategorieEvenement implements Parcelable {
    private int id;
    private String nom;
    private int billets_restant;
    private int prix;

    public CategorieEvenement() {
    }

    public CategorieEvenement(int id, String nom, int billets_restant, int prix) {
        this.id = id;
        this.nom = nom;
        this.billets_restant = billets_restant;
        this.prix = prix;
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

    public int getBilletsRestant() {
        return billets_restant;
    }

    public void setBilletsRestant(int billets_restant) {
        this.billets_restant = billets_restant;
    }

    public int getPrix() {
        return prix;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nom);
        dest.writeInt(billets_restant);
        dest.writeInt(prix);
    }

    public static final Parcelable.Creator<CategorieEvenement> CREATOR = new Parcelable.Creator<CategorieEvenement>() {
        @Override
        public CategorieEvenement createFromParcel(Parcel in) {
            return new CategorieEvenement(in);
        }

        @Override
        public CategorieEvenement[] newArray(int size) {
            return new CategorieEvenement[size];
        }
    };

    // Constructor for Parcelable
    protected CategorieEvenement(Parcel in) {
        id = in.readInt();
        nom = in.readString();
        billets_restant = in.readInt();
        prix = in.readInt();
    }
}
