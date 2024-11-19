package br.com.gs2.model;

import java.time.LocalDateTime;

abstract class Relatorio {

    private int idRelatorio;
    private String descricao;
    private String autor;
    private LocalDateTime dataEmissao;
    private Projeto projeto;

    public Relatorio(){}

    public Relatorio(int idRelatorio, String descricao, String autor, LocalDateTime dataEmissao, Projeto projeto){
        this.idRelatorio = idRelatorio;
        this.descricao = descricao;
        this.autor = autor;
        this.dataEmissao = dataEmissao;
        this.projeto = projeto;
    }

    public int getIdRelatorio(){
	    return idRelatorio;
    }

    public Relatorio setIdRelatorio(int idRelatorio){
        this.idRelatorio = idRelatorio;
        return this;
    }

    public String getDescricao(){
	    return descricao;
    }

    public Relatorio setDescricao(String descricao){
        this.descricao = descricao;
        return this;
    }

    public String getAutor(){
	    return autor;
    }

    public Relatorio setAutor(String autor){
        this.autor = autor;
        return this;
    }

    public LocalDateTime getDataEmissao(){
	    return dataEmissao;
    }

    public Relatorio setDataEmissao(LocalDateTime dataEmissao){
        this.dataEmissao = dataEmissao;
        return this;
    }

    public Projeto getProjeto(){
	    return projeto;
    }

    public Relatorio setProjeto(Projeto projeto){
        this.projeto = projeto;
        return this;
    }
}