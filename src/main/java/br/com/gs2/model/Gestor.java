package br.com.gs2.model;

public class Gestor {

    private int idGestor;
    private String nome;
    private String email;
    private String telefone;
    private String descricao;

    public Gestor(){}

    public Gestor(int idGestor, String nome, String email, String telefone, String descricao){
        this.idGestor = idGestor;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.descricao = descricao;
    }

    public int getIdGestor(){
	    return idGestor;
    }

    public Gestor setIdGestor(int idGestor){
        this.idGestor = idGestor;
        return this;
    }

    public String getNome(){
	    return nome;
    }

    public Gestor setNome(String nome){
        this.nome = nome;
        return this;
    }

    public String getEmail(){
	    return email;
    }

    public Gestor setEmail(String email){
        this.email = email;
        return this;
    }

    public String getTelefone(){
	    return telefone;
    }

    public Gestor setTelefone(String telefone){
        this.telefone = telefone;
        return this;
    }

    public String getDescricao(){
	    return descricao;
    }

    public Gestor setDescricao(String descricao){
        this.descricao = descricao;
        return this;
    }
}