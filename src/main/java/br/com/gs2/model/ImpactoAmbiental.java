package br.com.gs2.model;

public class ImpactoAmbiental extends Relatorio {
    private double emissaoEvitada;
    private double recursosEconomizados;

    public ImpactoAmbiental(){}

    public ImpactoAmbiental(double emissaoEvitada, double recursosEconomizados){
        this.emissaoEvitada = emissaoEvitada;
        this.recursosEconomizados = recursosEconomizados;
    }

    public double getEmissaoEvitada(){
	    return emissaoEvitada;
    }

    public ImpactoAmbiental setEmissaoEvitada(double emissaoEvitada){
        this.emissaoEvitada = emissaoEvitada;
        return this;
    }

    public double getRecursosEconomizados(){
	    return recursosEconomizados;
    }

    public ImpactoAmbiental setRecursosEconomizados(double recursosEconomizados){
        this.recursosEconomizados = recursosEconomizados;
        return this;
    }
}