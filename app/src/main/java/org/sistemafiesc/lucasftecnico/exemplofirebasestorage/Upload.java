package org.sistemafiesc.lucasftecnico.exemplofirebasestorage;

import com.google.firebase.database.Exclude;

public class Upload {
    private String nome;
    private String urlImagem;
    private String key;

    public Upload() {

    }

    public Upload(String nome, String urlImagem) {
        if (nome.trim().equals("")) {
            nome = "Sem nome";
        }
        this.nome = nome;
        this.urlImagem = urlImagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}
