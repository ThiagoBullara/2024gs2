package br.com.gs2.model;

import java.time.LocalDateTime;

public class Tecnologico extends Relatorio {
    private double qtdEnergiaGerada;
    private double eficiencia;
    private LocalDateTime validade;

    public Tecnologico(){}

    public Tecnologico(double qtdEnergiaGerada, double eficiencia, LocalDateTime validade){
        this.qtdEnergiaGerada = qtdEnergiaGerada;
        this.eficiencia = eficiencia;
        this.validade = validade;
    }

    public double getQtdEnergiaGerada(){
	    return qtdEnergiaGerada;
    }

    public Tecnologico setQtdEnergiaGerada(double qtdEnergiaGerada){
        this.qtdEnergiaGerada = qtdEnergiaGerada;
        return this;
    }

    public double getEficiencia(){
	    return eficiencia;
    }

    public Tecnologico setEficiencia(double eficiencia){
        this.eficiencia = eficiencia;
        return this;
    }

    public LocalDateTime getValidade() {
        return validade;
    }

    public Tecnologico setValidade(LocalDateTime validade) {
        this.validade = validade;
        return this;
    }
}