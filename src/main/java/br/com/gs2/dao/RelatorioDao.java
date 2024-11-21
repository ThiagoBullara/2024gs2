package br.com.gs2.dao;

import br.com.gs2.exception.NotFoundException;
import br.com.gs2.factory.ConnectionFactory;
import br.com.gs2.model.Equipe;
import br.com.gs2.model.Financeiro;
import br.com.gs2.model.Gestor;
import br.com.gs2.model.ImpactoAmbiental;
import br.com.gs2.model.Projeto;
import br.com.gs2.model.Relatorio;
import br.com.gs2.model.Relatorio.Tipo;
import static br.com.gs2.model.Relatorio.Tipo.FINANCEIRO;
import static br.com.gs2.model.Relatorio.Tipo.IMPACTO_AMBIENTAL;
import static br.com.gs2.model.Relatorio.Tipo.TECNOLOGICO;
import br.com.gs2.model.Tecnologico;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class RelatorioDao implements AutoCloseable
{

    private final Connection conexao;

    public RelatorioDao(Connection conexao)
    {
	this.conexao = conexao;
    }

    public RelatorioDao() throws SQLException
    {
	conexao = ConnectionFactory.getConnection();
    }

    public Connection getConnection()
    {
	return conexao;
    }

    public void fecharConexao() throws SQLException
    {
	conexao.close();
    }

    @Override
    public void close() throws Exception
    {
	try
	{
	    if (conexao != null && !conexao.isClosed())
	    {
		conexao.close();
	    }
	} catch (SQLException e)
	{
	    System.out.println("Erro ao fechar a conexão: " + e.getMessage());
	}
    }

    public int insert(Relatorio relatorio) throws SQLException
    {
	String sql = "INSERT INTO Relatorio (descricao, autor, dataEmissao, tipoRelatorio, Projeto_id, orcamentoTotal, despesas, valorGerado, emissaoEvitada, recursosEconomizados, qtdEnergiaGerada, eficiencia, validade) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	try (PreparedStatement stm = conexao.prepareStatement(sql, new String[]
	{
	    "idRelatorio"
	}))
	{
	    stm.setString(1, relatorio.getDescricao());
	    stm.setString(2, relatorio.getAutor());
	    stm.setTimestamp(3, Timestamp.valueOf(relatorio.getDataEmissao()));
	    stm.setString(4, relatorio.getTipoRelatorio().name());
	    stm.setInt(5, relatorio.getProjeto().getIdProjeto());

	    for (int i = 6; i <= 13; i++)
	    {
		stm.setObject(i, null);
	    }

	    if (relatorio instanceof Financeiro financeiro)
	    {
		stm.setDouble(6, financeiro.getOrcamentoTotal());
		stm.setDouble(7, financeiro.getDespesas());
		stm.setDouble(8, financeiro.getValorGerado());
	    } else if (relatorio instanceof ImpactoAmbiental impacto)
	    {
		stm.setDouble(9, impacto.getEmissaoEvitada());
		stm.setDouble(10, impacto.getRecursosEconomizados());
	    } else if (relatorio instanceof Tecnologico tecnologico)
	    {
		stm.setDouble(11, tecnologico.getQtdEnergiaGerada());
		stm.setDouble(12, tecnologico.getEficiencia());
		stm.setTimestamp(13, Timestamp.valueOf(tecnologico.getValidade()));
	    }

	    stm.executeUpdate();

	    try (ResultSet rs = stm.getGeneratedKeys())
	    {
		if (rs.next())
		{
		    return rs.getInt(1);
		} else
		{
		    throw new SQLException("Erro ao obter ID gerado.");
		}
	    }
	}
    }

    public Relatorio select(int id) throws SQLException, NotFoundException
    {
	String sql = """
            SELECT 
		Relatorio.idRelatorio AS id,
		Relatorio.descricao AS descricao,
		Relatorio.autor AS autor,
		Relatorio.dataEmissao AS dataEmissao,
		Relatorio.tipoRelatorio AS tipoRelatorio,
		Relatorio.orcamentoTotal AS orcamentoTotal,
		Relatorio.despesas AS despesas,
		Relatorio.valorGerado AS valorGerado,
		Relatorio.emissaoEvitada AS emissaoEvitada,
		Relatorio.recursosEconomizados AS recursosEconomizados,
		Relatorio.qtdEnergiaGerada AS qtdEnergiaGerada,
		Relatorio.eficiencia AS eficiencia,
		Relatorio.validade AS validade,
		Projeto.id AS 'projeto.id',
		Projeto.nome AS 'projeto.nome',
		Projeto.tipo AS 'projeto.tipo',
		Projeto.descricao AS 'projeto.descricao',
		Projeto.status AS 'projeto.status',
		Projeto.localizacao AS 'projeto.localizacao',
		Projeto.duracao AS 'projeto.duracao',
		Projeto.orcamento AS 'projeto.orcamento',
		Projeto.dataInicio AS 'projeto.dataInicio',
		Projeto.dataTermino AS 'projeto.dataTermino',
		Gestor.id AS 'gestor.id',
		Gestor.nome AS 'gestor.nome',
		Gestor.email AS 'gestor.email',
		Gestor.telefone AS 'gestor.telefone',
		Gestor.descricao AS 'gestor.descricao',
		Equipe.id AS 'equipe.id',
		Equipe.especialidade AS 'equipe.especialidade',
		Equipe.email AS 'equipe.email',
		Equipe.descricao AS 'equipe.descricao',
		Equipe.qntFuncionarios AS 'equipe.qntFuncionarios'
	    FROM
              Relatorio
		    JOIN
              Projeto ON Relatorio.Projeto_id = Projeto.idProjeto
		    JOIN
	      Gestor ON Projeto.Gestor_id = Gestor.idGestor
		    JOIN
	      Equipe ON Projeto.Equipe_id = Equipe.idEquipe
            WHERE idRelatorio = ?
        """;

	try (PreparedStatement stmt = conexao.prepareStatement(sql))
	{
	    stmt.setInt(1, id);
	    try (ResultSet result = stmt.executeQuery())
	    {
		if (result.next())
		{
		    String tipoRelatorio = result.getString("tipoRelatorio");
		    Tipo tipo = Tipo.valueOf(tipoRelatorio.toUpperCase());
		    Relatorio relatorio;

		    switch (tipo)
		    {
			case FINANCEIRO ->
			{
			    relatorio = new Financeiro();
			    ((Financeiro) relatorio).setOrcamentoTotal(result.getDouble("orcamentoTotal"));
			    ((Financeiro) relatorio).setDespesas(result.getDouble("despesas"));
			    ((Financeiro) relatorio).setValorGerado(result.getDouble("valorGerado"));
			}
			case IMPACTO_AMBIENTAL ->
			{
			    relatorio = new ImpactoAmbiental();
			    ((ImpactoAmbiental) relatorio).setEmissaoEvitada(result.getDouble("emissaoEvitada"));
			    ((ImpactoAmbiental) relatorio).setRecursosEconomizados(result.getDouble("recursosEconomizados"));
			}
			case TECNOLOGICO ->
			{
			    Timestamp dataValidade = result.getTimestamp("validade");
			    relatorio = new Tecnologico();
			    ((Tecnologico) relatorio).setValidade(dataValidade != null ? dataValidade.toLocalDateTime() : null);
			    ((Tecnologico) relatorio).setQtdEnergiaGerada(result.getDouble("qtdEnergiaGerada"));
			    ((Tecnologico) relatorio).setEficiencia(result.getDouble("eficiencia"));
			}
			default -> throw new IllegalArgumentException("Tipo desconhecido: " + tipoRelatorio);
		    }

		    Timestamp dataEmissao = result.getTimestamp("dataEmissao");

		    relatorio.setIdRelatorio(result.getInt("id"));
		    relatorio.setDescricao(result.getString("descricao"));
		    relatorio.setAutor(result.getString("autor"));
		    relatorio.setDataEmissao(dataEmissao != null ? dataEmissao.toLocalDateTime() : null);
		    relatorio.setTipoRelatorio(tipo);

		    Timestamp projetoDataInicio = result.getTimestamp("projeto.dataInicio");
		    Timestamp projetoDataTermino = result.getTimestamp("projeto.dataTermino");

		    Projeto projeto = new Projeto()
			    .setIdProjeto(result.getInt("projeto.id"))
			    .setNome(result.getString("projeto.nome"))
			    .setTipo(result.getString("projeto.tipo"))
			    .setDescricao(result.getString("projeto.descricao"))
			    .setStatus(result.getString("projeto.status"))
			    .setLocalizacao(result.getString("projeto.localizacao"))
			    .setDuracao(result.getInt("projeto.duracao"))
			    .setOrcamento(result.getDouble("projeto.orcamento"))
			    .setDataInicio(projetoDataInicio != null ? projetoDataInicio.toLocalDateTime() : null)
			    .setDataTermino(projetoDataTermino != null ? projetoDataTermino.toLocalDateTime() : null)
			    .setGestor(new Gestor()
				    .setIdGestor(result.getInt("gestor.id"))
				    .setNome(result.getString("gestor.nome"))
				    .setEmail(result.getString("gestor.email"))
				    .setTelefone(result.getString("gestor.telefone"))
				    .setDescricao(result.getString("gestor.descricao")))
			    .setEquipe(new Equipe()
				    .setIdEquipe(result.getInt("equipe.id"))
				    .setEspecialidade(result.getString("equipe.especialidade"))
				    .setEmail(result.getString("equipe.email"))
				    .setDescricao(result.getString("equipe.descricao"))
				    .setQtdFuncionarios(result.getInt("equipe.qtdFuncionarios")));

		    relatorio.setProjeto(projeto);

		    return relatorio;
		} else
		{
		    throw new NotFoundException("Relatório não encontrado!");
		}
	    }
	}
    }

    public List<Relatorio> search() throws SQLException
    {
	String sql = """
        SELECT 
            Relatorio.idRelatorio AS id,
            Relatorio.descricao AS descricao,
            Relatorio.autor AS autor,
            Relatorio.dataEmissao AS dataEmissao,
            Relatorio.tipoRelatorio AS tipoRelatorio,
            Relatorio.orcamentoTotal AS orcamentoTotal,
            Relatorio.despesas AS despesas,
            Relatorio.valorGerado AS valorGerado,
            Relatorio.emissaoEvitada AS emissaoEvitada,
            Relatorio.recursosEconomizados AS recursosEconomizados,
            Relatorio.qtdEnergiaGerada AS qtdEnergiaGerada,
            Relatorio.eficiencia AS eficiencia,
            Relatorio.validade AS validade,
            Projeto.id AS 'projeto.id',
            Projeto.nome AS 'projeto.nome',
            Projeto.tipo AS 'projeto.tipo',
            Projeto.descricao AS 'projeto.descricao',
            Projeto.status AS 'projeto.status',
            Projeto.localizacao AS 'projeto.localizacao',
            Projeto.duracao AS 'projeto.duracao',
            Projeto.orcamento AS 'projeto.orcamento',
            Projeto.dataInicio AS 'projeto.dataInicio',
        FROM
	    Relatorio
	      JOIN
	    Projeto ON Relatorio.Projeto_id = Projeto.idProjeto
	      JOIN
	Gestor ON Projeto.Gestor_id = Gestor.idGestor
	      JOIN
	Equipe ON Projeto.Equipe_id = Equipe.idEquipe""";

	List<Relatorio> relatorios = new ArrayList<>();

	try (PreparedStatement stmt = conexao.prepareStatement(sql);
		ResultSet result = stmt.executeQuery())
	{
	    while (result.next())
	    {
		String tipoRelatorio = result.getString("tipoRelatorio");
		Tipo tipo = Tipo.valueOf(tipoRelatorio.toUpperCase());
		Relatorio relatorio;

		switch (tipo)
		{
		    case FINANCEIRO ->
		    {
			relatorio = new Financeiro();
			((Financeiro) relatorio).setOrcamentoTotal(result.getDouble("orcamentoTotal"));
			((Financeiro) relatorio).setDespesas(result.getDouble("despesas"));
			((Financeiro) relatorio).setValorGerado(result.getDouble("valorGerado"));
		    }
		    case IMPACTO_AMBIENTAL ->
		    {
			relatorio = new ImpactoAmbiental();
			((ImpactoAmbiental) relatorio).setEmissaoEvitada(result.getDouble("emissaoEvitada"));
			((ImpactoAmbiental) relatorio).setRecursosEconomizados(result.getDouble("recursosEconomizados"));
		    }
		    case TECNOLOGICO ->
		    {
			Timestamp dataValidade = result.getTimestamp("validade");
			relatorio = new Tecnologico();
			((Tecnologico) relatorio).setValidade(dataValidade != null ? dataValidade.toLocalDateTime() : null);
			((Tecnologico) relatorio).setQtdEnergiaGerada(result.getDouble("qtdEnergiaGerada"));
			((Tecnologico) relatorio).setEficiencia(result.getDouble("eficiencia"));
		    }
		    default -> throw new IllegalArgumentException("Tipo desconhecido: " + tipoRelatorio);
		}

		Timestamp dataEmissao = result.getTimestamp("dataEmissao");

		relatorio.setIdRelatorio(result.getInt("id"));
		relatorio.setDescricao(result.getString("descricao"));
		relatorio.setAutor(result.getString("autor"));
		relatorio.setDataEmissao(dataEmissao != null ? dataEmissao.toLocalDateTime() : null);
		relatorio.setTipoRelatorio(tipo);

		Timestamp projetoDataInicio = result.getTimestamp("projeto.dataInicio");
		Timestamp projetoDataTermino = result.getTimestamp("projeto.dataTermino");

		Projeto projeto = new Projeto()
			.setIdProjeto(result.getInt("projeto.id"))
			.setNome(result.getString("projeto.nome"))
			.setTipo(result.getString("projeto.tipo"))
			.setDescricao(result.getString("projeto.descricao"))
			.setStatus(result.getString("projeto.status"))
			.setLocalizacao(result.getString("projeto.localizacao"))
			.setDuracao(result.getInt("projeto.duracao"))
			.setOrcamento(result.getDouble("projeto.orcamento"))
			.setDataInicio(projetoDataInicio != null ? projetoDataInicio.toLocalDateTime() : null)
			.setDataTermino(projetoDataTermino != null ? projetoDataTermino.toLocalDateTime() : null)
			.setGestor(new Gestor()
				.setIdGestor(result.getInt("gestor.id"))
				.setNome(result.getString("gestor.nome"))
				.setEmail(result.getString("gestor.email"))
				.setTelefone(result.getString("gestor.telefone"))
				.setDescricao(result.getString("gestor.descricao")))
			.setEquipe(new Equipe()
				.setIdEquipe(result.getInt("equipe.id"))
				.setEspecialidade(result.getString("equipe.especialidade"))
				.setEmail(result.getString("equipe.email"))
				.setDescricao(result.getString("equipe.descricao"))
				.setQtdFuncionarios(result.getInt("equipe.qtdFuncionarios")));

		relatorio.setProjeto(projeto);

		relatorios.add(relatorio);
	    }
	}

	return relatorios;
    }

    public void update(Relatorio relatorio) throws SQLException, NotFoundException
    {
	String sql = """
        UPDATE Relatorio
        SET
            descricao = ?,
            autor = ?,
            dataEmissao = ?,
            tipoRelatorio = ?,
	    Projeto_id = ?,
            orcamentoTotal = ?,
            despesas = ?,
            valorGerado = ?,
            emissaoEvitada = ?,
            recursosEconomizados = ?,
            qtdEnergiaGerada = ?,
            eficiencia = ?,
            validade = ?
        WHERE idRelatorio = ?
    """;

	try (PreparedStatement stmt = conexao.prepareStatement(sql))
	{
	    stmt.setString(1, relatorio.getDescricao());
	    stmt.setString(2, relatorio.getAutor());
	    stmt.setTimestamp(3, relatorio.getDataEmissao() != null
		    ? Timestamp.valueOf(relatorio.getDataEmissao()) : null);
	    stmt.setString(4, relatorio.getTipoRelatorio().name());
	    stmt.setString(5, relatorio.getTipoRelatorio().name());
	    
	    
	    if (relatorio instanceof Financeiro financeiro)
	    {
		stmt.setDouble(6, financeiro.getOrcamentoTotal());
		stmt.setDouble(7, financeiro.getDespesas());
		stmt.setDouble(8, financeiro.getValorGerado());
		stmt.setNull(9, java.sql.Types.DOUBLE); 
		stmt.setNull(10, java.sql.Types.DOUBLE); 
		stmt.setNull(11, java.sql.Types.DOUBLE); 
		stmt.setNull(12, java.sql.Types.DOUBLE); 
		stmt.setNull(13, java.sql.Types.TIMESTAMP); 
	    } else if (relatorio instanceof ImpactoAmbiental impactoAmbiental)
	    {
		stmt.setNull(6, java.sql.Types.DOUBLE); 
		stmt.setNull(7, java.sql.Types.DOUBLE); 
		stmt.setNull(8, java.sql.Types.DOUBLE); 
		stmt.setDouble(9, impactoAmbiental.getEmissaoEvitada());
		stmt.setDouble(10, impactoAmbiental.getRecursosEconomizados());
		stmt.setNull(11, java.sql.Types.DOUBLE); 
		stmt.setNull(12, java.sql.Types.DOUBLE); 
		stmt.setNull(13, java.sql.Types.TIMESTAMP); 
	    } else if (relatorio instanceof Tecnologico tecnologico)
	    {
		stmt.setNull(6, java.sql.Types.DOUBLE); 
		stmt.setNull(7, java.sql.Types.DOUBLE);
		stmt.setNull(8, java.sql.Types.DOUBLE);
		stmt.setNull(9, java.sql.Types.DOUBLE); 
		stmt.setNull(9, java.sql.Types.DOUBLE); 
		stmt.setDouble(11, tecnologico.getQtdEnergiaGerada());
		stmt.setDouble(12, tecnologico.getEficiencia());
		stmt.setTimestamp(13, tecnologico.getValidade() != null
			? Timestamp.valueOf(tecnologico.getValidade()) : null);
	    }

	    stmt.setInt(13, relatorio.getIdRelatorio()); 

	    int rowsUpdated = stmt.executeUpdate();

	    if (rowsUpdated == 0)
	    {
		throw new NotFoundException("Relatório com ID " + relatorio.getIdRelatorio() + " não encontrado.");
	    }
	}
    }

    public void delete(int id) throws SQLException, NotFoundException
    {
	String sql = "DELETE FROM Relatorio WHERE idRelatorio = ?";

	try (PreparedStatement stmt = conexao.prepareStatement(sql))
	{
	    stmt.setInt(1, id);
	    int linha = stmt.executeUpdate();
	    if (linha == 0)
	    {
		throw new NotFoundException("Relatório com ID " + id + " não encontrado.");
	    }
	}
    }

}
