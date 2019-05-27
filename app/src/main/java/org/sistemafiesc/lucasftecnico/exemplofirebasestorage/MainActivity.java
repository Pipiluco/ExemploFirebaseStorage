package org.sistemafiesc.lucasftecnico.exemplofirebasestorage;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button btnEscolherImagem, btnUpload;
    private TextView tvNomeUpload;
    private EditText edtNomeArquivo;
    private ProgressBar pbUpload;
    private ImageView imgArquivo;

    private Uri uriImagem;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnEscolherImagem = (Button) findViewById(R.id.btnEscolherImagem);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        tvNomeUpload = (TextView) findViewById(R.id.tvNomeUpload);
        edtNomeArquivo = (EditText) findViewById(R.id.edtNomeArquivo);
        pbUpload = (ProgressBar) findViewById(R.id.pbUpload);
        imgArquivo = (ImageView) findViewById(R.id.imgArquivo);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        btnEscolherImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirArquivoEscolhido();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(getApplicationContext(), "Upload em progresso!", Toast.LENGTH_SHORT).show();
                } else {
                    uploadDoArquivo();
                }
            }
        });

        tvNomeUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirImagensActivity();
            }
        });
    }

    private void abrirArquivoEscolhido() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void uploadDoArquivo() {
        if (uriImagem != null) {
            final StorageReference arquivoReference = storageReference.child(System.currentTimeMillis() + "." + getExtensaoDoArquivo(uriImagem));

            uploadTask = arquivoReference.putFile(uriImagem).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                private static final String TAG = "uploadDoArquivo";

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pbUpload.setProgress(0);
                        }
                    }, 500);
                    Toast.makeText(getApplicationContext(), "Upload completo!", Toast.LENGTH_SHORT).show();

                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    Uri downloadUri = uriTask.getResult();

                    Log.d(TAG, ": Firebase URL: " + downloadUri.toString());
                    Upload upload = new Upload(edtNomeArquivo.getText().toString().trim(), downloadUri.toString());

                    String uploadId = databaseReference.push().getKey();
                    databaseReference.child(uploadId).setValue(upload);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progresso = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    pbUpload.setProgress((int) progresso);
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Nenhum arquivo selecionado!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getExtensaoDoArquivo(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void abrirImagensActivity() {
        Intent intent = new Intent(this, ImagensActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriImagem = data.getData();
            Picasso.with(this).load(uriImagem).into(imgArquivo);
        }
    }
}

/*
 private void uploadDoArquivo() {
        if (uriImagem != null) {
            StorageReference arquivoReference = storageReference.child(System.currentTimeMillis() + "." + getExtensaoDoArquivo(uriImagem));

            uploadTask = arquivoReference.putFile(uriImagem).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pbUpload.setProgress(0);
                        }
                    }, 500);
                    Toast.makeText(getApplicationContext(), "Upload completo!", Toast.LENGTH_SHORT).show();
                    Upload upload = new Upload(edtNomeArquivo.getText().toString().trim(), taskSnapshot.getStorage().getDownloadUrl().toString());
                    String uploadId = databaseReference.push().getKey();
                    databaseReference.child(uploadId).setValue(upload);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progresso = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    pbUpload.setProgress((int) progresso);
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Nenhum arquivo selecionado!", Toast.LENGTH_SHORT).show();
        }
    }
 */

//////////////////////////////////////////////

/*
  private void uploadDoArquivo() {
        if (uriImagem != null) {
            StorageReference arquivoReference = storageReference.child(System.currentTimeMillis() + "." + getExtensaoDoArquivo(uriImagem));
            final String fileContext = arquivoReference.toString();
            final StorageReference ref = storageReference.child("uploads");

            uploadTask = ref.putFile(uriImagem);
            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String miUrlOk = downloadUri.toString();
                        Upload upload = new Upload(edtNomeArquivo.getText().toString().trim(), miUrlOk);
                        String uploadId = databaseReference.push().getKey();
                        databaseReference.child(uploadId).setValue(upload);
                    } else {

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Nenhum arquivo selecionado!", Toast.LENGTH_SHORT).show();
        }
    }
 */