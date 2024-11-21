package br.com.gs2.model;

import java.time.LocalDateTime;

public abstract class Relatorio
{  
    private int idRelatorio;
    private String descricao;
    private String autor;
    private LocalDateTime dataEmissao;
    private Tipo tipoRelatorio;
    private Projeto projeto;

    public Relatorio()
    {
    }

    public Relatorio(int idRelatorio, String descricao, String autor, LocalDateTime dataEmissao, Tipo tipoRelatorio, Projeto projeto)
    {
	this.idRelatorio = idRelatorio;
	this.descricao = descricao;
	this.autor = autor;
	this.dataEmissao = dataEmissao;
	this.tipoRelatorio = tipoRelatorio;
	this.projeto = projeto;
    }

    public int getIdRelatorio()
    {
	return idRelatorio;
    }

    public Relatorio setIdRelatorio(int idRelatorio)
    {
	this.idRelatorio = idRelatorio;
	return this;
    }

    public String getDescricao()
    {
	return descricao;
    }

    public Relatorio setDescricao(String descricao)
    {
	this.descricao = descricao;
	return this;
    }

    public String getAutor()
    {
	return autor;
    }

    public Relatorio setAutor(String autor)
    {
	this.autor = autor;
	return this;
    }

    public LocalDateTime getDataEmissao()
    {
	return dataEmissao;
    }

    public Relatorio setDataEmissao(LocalDateTime dataEmissao)
    {
	this.dataEmissao = dataEmissao;
	return this;
    }

    public Projeto getProjeto()
    {
	if(projeto == null)
	    projeto = new Projeto();
	return projeto;
    }

    public Relatorio setProjeto(Projeto projeto)
    {
	this.projeto = projeto;
	return this;
    }

    public Tipo getTipoRelatorio()
    {
	return tipoRelatorio;
    }

    public Relatorio setTipoRelatorio(Tipo tipoRelatorio)
    {
	this.tipoRelatorio = tipoRelatorio;
	return this;
    }

    public enum Tipo
    {
	FINANCEIRO(1, "Financeiro"),
	IMPACTO_AMBIENTAL(2, "Impacto Ambiental"),
	TECNOLOGICO(3, "Tecnologico");

	private final String descricao;
	private final int codigo;

	Tipo(int codigo, String descricao)
	{
	    this.descricao = descricao;
	    this.codigo = codigo;
	}

	@Override
	public String toString()
	{
	    return descricao;
	}

	public int getCodigo()
	{
	    return codigo;
	}

	public static Tipo fromCodigo(int codigo)
	{
	    for (Tipo tipoRelatorio : Tipo.values())
	    {
		if (tipoRelatorio.getCodigo() == codigo)
		{
		    return tipoRelatorio;
		}
	    }
	    throw new IllegalArgumentException("Código inválido: " + codigo);
	}
    }
}
