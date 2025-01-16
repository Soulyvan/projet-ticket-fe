package com.example.ticketeventandroid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketeventandroid.R;
import com.example.ticketeventandroid.models.CategorieEvenement;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    private List<CategorieEvenement> categories;

    public CategoriesAdapter(List<CategorieEvenement> categories) {
        this.categories = categories;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        CategorieEvenement category = categories.get(position);
        holder.categoryName.setText(category.getNom());
        holder.categoryPrice.setText("Prix: " + category.getPrix() + " XOF");
        holder.categoryTickets.setText("Nombre de places: " + category.getBilletsRestant());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView categoryName;
        TextView categoryPrice;
        TextView categoryTickets;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            categoryPrice = itemView.findViewById(R.id.categoryPrice);
            categoryTickets = itemView.findViewById(R.id.categoryTickets);
        }
    }
}