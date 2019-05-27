package org.sistemafiesc.lucasftecnico.exemplofirebasestorage;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ImagensActivity extends AppCompatActivity implements ImagemAdapter.OnItemClickListener {
    private RecyclerView rcvImagens;
    private ImagemAdapter imagemAdapter;
    private ProgressBar pbCirculoImagens;

    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private List<Upload> uploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagens);

        rcvImagens = findViewById(R.id.rcvImagens);
        rcvImagens.setHasFixedSize(true);
        rcvImagens.setLayoutManager(new LinearLayoutManager(this));

        pbCirculoImagens = findViewById(R.id.pbCirculoImagens);

        uploads = new ArrayList<>();
        imagemAdapter = new ImagemAdapter(ImagensActivity.this, uploads);
        rcvImagens.setAdapter(imagemAdapter);
        imagemAdapter.setOnItemClickListener(ImagensActivity.this);

        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uploads.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    upload.setKey(postSnapshot.getKey());
                    uploads.add(upload);
                }
                imagemAdapter.notifyDataSetChanged();
                pbCirculoImagens.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Erro: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                pbCirculoImagens.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onItemClick(int posicao) {
        Toast.makeText(getApplicationContext(), "Click normal na posição: " + posicao, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWhatEverClick(int posicao) {
        Toast.makeText(getApplicationContext(), "Click whatever na posição: " + posicao, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int posicao) {
        Upload itemSelecionado = uploads.get(posicao);
        final String keySelecionada = itemSelecionado.getKey();

        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(itemSelecionado.getUrlImagem());
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseReference.child(keySelecionada).removeValue();
                Toast.makeText(getApplicationContext(), "Item excluído!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(valueEventListener);
    }
}
