package com.example.ticketeventandroid.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ticketeventandroid.BaseActivity;
import com.example.ticketeventandroid.DetailsEvenementActivity;
import com.example.ticketeventandroid.R;
import com.example.ticketeventandroid.models.Evenement;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EvenementAdapter extends RecyclerView.Adapter<EvenementAdapter.EvenementViewHolder> {

    private Context context;
    private List<Evenement> evenementList;
    private final String BASE_URL = BaseActivity.getBASE_URL();

    public EvenementAdapter(Context context, List<Evenement> evenementList) {
        this.context = context;
        this.evenementList = evenementList;
    }

    @Override
    public EvenementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_evenement, parent, false);
        return new EvenementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EvenementViewHolder holder, int position) {
        Evenement evenement = evenementList.get(position);
        holder.nom.setText(evenement.getNom());
        holder.lieu.setText(evenement.getLieu());
        holder.type.setText(evenement.getTypeEvenement());

        // Chargement de l'image avec Glide
        Glide.with(context)
                .load(BASE_URL + evenement.getPhoto()) // Base URL + photo
                .into(holder.photo);

        // Clic sur l'élément
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailsEvenementActivity.class);
            // Passer l'objet Evenement complet
            intent.putExtra("evenement", evenement);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return evenementList.size();
    }

    public static class EvenementViewHolder extends RecyclerView.ViewHolder {
        TextView nom, lieu, type;
        ImageView photo;

        public EvenementViewHolder(View itemView) {
            super(itemView);
            nom = itemView.findViewById(R.id.textViewNomEvent);
            lieu = itemView.findViewById(R.id.textViewLieuEvent);
            photo = itemView.findViewById(R.id.imageViewPhotoEvent);
            type = itemView.findViewById(R.id.textViewTypeEvent);
        }
    }

    public void updateData(List<Evenement> newEvenements) {
        this.evenementList = newEvenements;
        notifyDataSetChanged(); // Notifie l'adaptateur pour mettre à jour l'affichage
    }
}