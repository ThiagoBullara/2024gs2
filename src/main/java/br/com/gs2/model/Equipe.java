package br.com.gs2.model;

public class Equipe {

    private int idEquipe;
    private String nome;
    private String especialidade;
    private String email;
    private String descricao;
    private int qtdFuncionarios;

    public Equipe(){}

    public Equipe(int idEquipe, String nome, String email, String especialidade, String descricao, int qtdFuncionarios){
        this.idEquipe = idEquipe;
        this.nome = nome;
        this.email = email;
        this.especialidade = especialidade;
        this.descricao = descricao;
        this.qtdFuncionarios = qtdFuncionarios;
    }

    public int getIdEquipe(){
	    return idEquipe;
    }

    public Equipe setIdEquipe(int idEquipe){
        this.idEquipe = idEquipe;
        return this;
    }

    public String getNome(){
	    return nome;
    }

    public Equipe setNome(String nome){
        this.nome = nome;
        return this;
    }

    public String getEspecialidade(){
	    return especialidade;
    }

    public Equipe setEspecialidade(String especialidade){
        this.especialidade = especialidade;
        return this;
    }

    public String getEmail(){
	    return email;
    }

    public Equipe setEmail(String email){
        this.email = email;
        return this;
    }

    public String getDescricao(){
	    return descricao;
    }

    public Equipe setDescricao(String descricao){
        this.descricao = descricao;
        return this;
    }

    public int getQtdFuncionarios(){
	    return qtdFuncionarios;
    }

    public Equipe setQtdFuncionarios(int qtdFuncionarios){
        this.qtdFuncionarios = qtdFuncionarios;
        return this;
    }
}