package br.com.gs2.model;

public class Recurso {

    private int idRecurso;
    private String tipo;
    private int quantidade;
    private double custoUnitario;
    private String fornecedor;
    private Projeto projeto;

    public Recurso(){}

    public Recurso(int idRecurso, String tipo, double custoUnitario, int quantidade, String fornecedor, Projeto projeto){
        this.idRecurso = idRecurso;
        this.tipo = tipo;
        this.custoUnitario = custoUnitario;
        this.quantidade = quantidade;
        this.fornecedor = fornecedor;
        this.projeto = projeto;
    }

    public int getIdRecurso(){
	    return idRecurso;
    }

    public Recurso setIdRecurso(int idRecurso){
        this.idRecurso = idRecurso;
        return this;
    }

    public String getTipo(){
	    return tipo;
    }

    public Recurso setTipo(String tipo){
        this.tipo = tipo;
        return this;
    }

    public int getQuantidade(){
	    return quantidade;
    }

    public Recurso setQuantidade(int quantidade){
        this.quantidade = quantidade;
        return this;
    }

    public double getCustoUnitario(){
	    return custoUnitario;
    }

    public Recurso setCustoUnitario(double custoUnitario){
        this.custoUnitario = custoUnitario;
        return this;
    }

    public String getFornecedor(){
	    return fornecedor;
    }

    public Recurso setFornecedor(String fornecedor){
        this.fornecedor = fornecedor;
        return this;
    }

    public Projeto getProjeto(){
	    if(projeto == null)
		projeto = new Projeto();
	    return projeto;
    }

    public Recurso setProjeto(Projeto projeto){
        this.projeto = projeto;
        return this;
    }
}