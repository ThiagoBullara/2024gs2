package br.com.gs2.model;

public class Financeiro extends Relatorio {
    private double orcamentoTotal;
    private double despesas;
    private double valorGerado;

    public Financeiro(){}

    public Financeiro(double orcamentoTotal, double despesas, double valorGerado){
        this.orcamentoTotal = orcamentoTotal;
        this.despesas = despesas;
        this.valorGerado = valorGerado;
    }

    public double getOrcamentoTotal(){
	    return orcamentoTotal;
    }

    public Financeiro setOrcamentoTotal(double orcamentoTotal){
        this.orcamentoTotal = orcamentoTotal;
        return this;
    }

    public double getDespesas(){
	    return despesas;
    }

    public Financeiro setDespesas(double despesas){
        this.despesas = despesas;
        return this;
    }

    public double getValorGerado() {
        return valorGerado;
    }

    public Financeiro setValorGerado(double valorGerado) {
        this.valorGerado = valorGerado;
        return this;
    }
}