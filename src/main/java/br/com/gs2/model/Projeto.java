package br.com.gs2.model;

import java.time.LocalDateTime;

public class Projeto {

    private int idProjeto;
    private String nome;
    private String tipo;
    private String descricao;
    private String status;
    private String localizacao;
    private int duracao;
    private double orcamento;
    private LocalDateTime dataInicio;
    private LocalDateTime dataTermino;
    private Gestor gestor;
    private Equipe equipe;

    public Projeto(){}

    public Projeto(int idProjeto, String nome, String tipo, String descricao, String status, String localizacao, double orcamento, LocalDateTime dataInicio, LocalDateTime dataTermino, int duracao, Gestor gestor, Equipe equipe){
        this.idProjeto = idProjeto;
        this.nome = nome;
        this.tipo = tipo;
        this.descricao = descricao;
        this.status = status;
        this.localizacao = localizacao;
        this.orcamento = orcamento;
        this.duracao = duracao;
        this.dataInicio = dataInicio;
        this.dataTermino = dataTermino;
        this.gestor = gestor;
        this.equipe = equipe;
    }

    public int getIdProjeto(){
	    return idProjeto;
    }

    public Projeto setIdProjeto(int idProjeto){
        this.idProjeto = idProjeto;
        return this;
    }

    public String getNome(){
	    return nome;
    }

    public Projeto setNome(String nome){
        this.nome = nome;
        return this;
    }

    public String getTipo(){
	    return tipo;
    }

    public Projeto setTipo(String tipo){
        this.tipo = tipo;
        return this;
    }

    public String getDescricao(){
	    return descricao;
    }

    public Projeto setDescricao(String descricao){
        this.descricao = descricao;
        return this;
    }

    public String getStatus(){
	    return status;
    }

    public Projeto setStatus(String status){
        this.status = status;
        return this;
    }

    public String getLocalizacao(){
	    return localizacao;
    }

    public Projeto setLocalizacao(String localizacao){
        this.localizacao = localizacao;
        return this;
    }

    public double getOrcamento(){
	    return orcamento;
    }

    public Projeto setOrcamento(double orcamento){
        this.orcamento = orcamento;
        return this;
    }

    public int getDuracao(){
	    return duracao;
    }

    public Projeto setDuracao(int duracao){
        this.duracao = duracao;
        return this;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public Projeto setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
        return this;
    }

    public LocalDateTime getDataTermino() {
        return dataTermino;
    }

    public Projeto setDataTermino(LocalDateTime dataTermino) {
        this.dataTermino = dataTermino;
        return this;
    }

    public Gestor getGestor(){
	    return gestor;
    }

    public Projeto setGestor(Gestor gestor){
        this.gestor = gestor;
        return this;
    }

    public Equipe getEquipe(){
	    return equipe;
    }

    public Projeto setEquipe(Equipe equipe){
        this.equipe = equipe;
        return this;
    }
}