package com.example.ticketeventandroid.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ticketeventandroid.BaseActivity;
import com.example.ticketeventandroid.QRCodeActivity;
import com.example.ticketeventandroid.R;
import com.example.ticketeventandroid.models.QRCode;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

public class QRCodeAdapter extends RecyclerView.Adapter<QRCodeAdapter.QRCodeViewHolder> {
    private List<QRCode> qrCodeList;
    private Context context;

    public QRCodeAdapter(Context context, List<QRCode> qrCodeList) {
        this.context = context;
        this.qrCodeList = qrCodeList;
    }

    @Override
    public QRCodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_qrcode, parent, false);
        return new QRCodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QRCodeViewHolder holder, int position) {
        QRCode qrCode = qrCodeList.get(position);
        holder.bind(qrCode);
    }

    @Override
    public int getItemCount() {
        return qrCodeList.size();
    }

    public class QRCodeViewHolder extends RecyclerView.ViewHolder {
        private ImageView qrCodeImage;
        private TextView qrCodeCategory;
        private TextView evenementName;
        private Button downloadButton;

        public QRCodeViewHolder(View itemView) {
            super(itemView);
            qrCodeImage = itemView.findViewById(R.id.qr_code_image);
            qrCodeCategory = itemView.findViewById(R.id.qr_code_category);
            evenementName = itemView.findViewById(R.id.evenement_name);
            downloadButton = itemView.findViewById(R.id.download_button);
        }

        public void bind(QRCode qrCode) {
            // Charger l'image avec Glide
            Glide.with(qrCodeImage.getContext())
                    .load(BaseActivity.getBASE_URL() + qrCode.getQrImage())
                    .into(qrCodeImage);

            // Afficher le nom de la catégorie et de l'événement
            qrCodeCategory.setText(qrCode.getCategory());
            evenementName.setText(qrCode.getEvenementName());

            // Associer l'URL de l'image au bouton de téléchargement
            String imageUrl = BaseActivity.getBASE_URL() + qrCode.getQrImage();
            downloadButton.setTag(imageUrl);

            // Gestion du téléchargement de l'image
            downloadButton.setOnClickListener(v -> {
                String url = (String) v.getTag();
                downloadImage(url);  // Appel à une méthode non statique
            });
        }

        private void downloadImage(String imageUrl) {
            // Lancer un téléchargement en arrière-plan (en passant le contexte dans le constructeur)
            new DownloadImageTask().execute(imageUrl);  // Ici pas besoin de contexte externe
        }
    }

    // AsyncTask pour télécharger l'image
    private class DownloadImageTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            String imageUrl = strings[0];
            try {
                // Télécharger l'image avec Picasso
                Bitmap bitmap = Picasso.get().load(imageUrl).get();

                // Créer un fichier dans le répertoire des images
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "Downloaded Image");
                values.put(MediaStore.Images.Media.DESCRIPTION, "Image téléchargée");
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

                // Ajouter l'image dans la galerie
                Uri imageUri = context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                // Enregistrer l'image dans le répertoire
                OutputStream outputStream = context.getContentResolver().openOutputStream(imageUri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();

                // Informer que l'image a bien été ajoutée à la galerie
                MediaScannerConnection.scanFile(context, new String[]{imageUri.getPath()}, null, null);

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                // Afficher un message de succès
                Toast.makeText(context, "Image téléchargée et ajoutée à la galerie", Toast.LENGTH_SHORT).show();
            } else {
                // Afficher un message d'erreur
                Toast.makeText(context, "Échec du téléchargement", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
