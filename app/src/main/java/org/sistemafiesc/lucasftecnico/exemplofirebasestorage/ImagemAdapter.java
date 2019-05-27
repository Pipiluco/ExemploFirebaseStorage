package org.sistemafiesc.lucasftecnico.exemplofirebasestorage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImagemAdapter extends RecyclerView.Adapter<ImagemAdapter.ImagemViewHolder> {
    private Context context;
    private List<Upload> uploads;
    private OnItemClickListener onItemClickListener;

    public ImagemAdapter(Context context, List<Upload> uploads) {
        this.context = context;
        this.uploads = uploads;
    }

    @NonNull
    @Override
    public ImagemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_imagem, viewGroup, false);
        return new ImagemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagemViewHolder imagemViewHolder, int i) {
        Upload upload = uploads.get(i);
        imagemViewHolder.tvNome.setText(upload.getNome());
        Picasso.with(context).load(upload.getUrlImagem()).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(imagemViewHolder.imvUpload);
    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    ///
    public class ImagemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView tvNome;
        public ImageView imvUpload;

        public ImagemViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNome = itemView.findViewById(R.id.tvNome);
            imvUpload = itemView.findViewById(R.id.imvUpload);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                int posicao = getAdapterPosition();
                if (posicao != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(posicao);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Escolha ação");
            MenuItem doWhatEver = menu.add(Menu.NONE, 1, 1, "Do whatever");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete");

            doWhatEver.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (onItemClickListener != null) {
                int posicao = getAdapterPosition();
                if (posicao != RecyclerView.NO_POSITION) {
                    switch (item.getItemId()){
                        case 1:
                            onItemClickListener.onWhatEverClick(posicao);
                            return true;
                        case 2:
                            onItemClickListener.onDeleteClick(posicao);
                            return true;
                    }
                }
            }

            return false;
        }
    }

    ///
    public interface OnItemClickListener {
        void onItemClick(int posicao);

        void onWhatEverClick(int posicao);

        void onDeleteClick(int posicao);
    }


    ///
    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }
}
