package br.com.gs2.dao;

import br.com.gs2.exception.NotFoundException;
import br.com.gs2.factory.ConnectionFactory;
import br.com.gs2.model.Equipe;
import br.com.gs2.model.Gestor;
import br.com.gs2.model.Projeto;
import br.com.gs2.model.Recurso;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RecursoDao implements AutoCloseable
{

    private final Connection conexao;

    public RecursoDao(Connection conexao) throws SQLException
    {
	this.conexao = conexao;
    }

    public RecursoDao() throws SQLException
    {
	conexao = ConnectionFactory.getConnection();
    }

    public Connection getConnection()
    {
	return conexao;
    }

    public int insert(Recurso recurso) throws SQLException
    {
	String sql = "INSERT INTO Recurso (tipo, quantidade, custoUnitario, fornecedor, Projeto_id) VALUES (?, ?, ?, ?, ?)";

	try (PreparedStatement stm = conexao.prepareStatement(sql, new String[]
	{
	    "idRecurso"
	}))
	{
	    stm.setString(1, recurso.getTipo());
	    stm.setInt(2, recurso.getQuantidade());
	    stm.setDouble(3, recurso.getCustoUnitario());
	    stm.setString(4, recurso.getFornecedor());
	    stm.setInt(5, recurso.getProjeto().getIdProjeto());
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

    public void fecharConexao() throws SQLException
    {
	conexao.close();
    }

    @Override
    public void close()
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

    public Recurso select(int id) throws SQLException, NotFoundException
    {
	String sql = """
                 SELECT 
		    Recurso.idRecurso AS id,
		    Recurso.tipo AS tipo,
		    Recurso.quantidade AS quantidade,
		    Recurso.custoUnitario AS custoUnitario,
		    Recurso.fornecedor AS fornecedor,
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
                    Recurso
			JOIN
		    Projeto ON Recurso.Projeto_id = Projeto.idProjeto
                         JOIN
                    Gestor ON Projeto.Gestor_id = Gestor.idGestor
                         JOIN
                    Equipe ON Projeto.Equipe_id = Equipe.idEquipe
                 WHERE
                     Recurso.idRecurso = ?""";

	try (PreparedStatement stm = conexao.prepareStatement(sql))
	{
	    stm.setInt(1, id);
	    try (ResultSet result = stm.executeQuery())
	    {
		if (!result.next())
		{
		    throw new NotFoundException("Recurso não encontrado");
		}
		
		Timestamp dataTimestamp = result.getTimestamp("projeto.dataInicio");
		LocalDateTime projetoDataInicio = dataTimestamp != null ? dataTimestamp.toLocalDateTime() : null;

		Timestamp dataTimestamp2 = result.getTimestamp("projeto.dataTermino");
		LocalDateTime projetoDataTermino = dataTimestamp2 != null ? dataTimestamp2.toLocalDateTime() : null;
		
		return new Recurso()
			.setIdRecurso(result.getInt("id"))
			.setTipo(result.getString("tipo"))
			.setQuantidade(result.getInt("quantidade"))
			.setCustoUnitario(result.getDouble("custoUnitario"))
			.setFornecedor(result.getString("fornecedor"))
			.setProjeto(new Projeto()
			    .setIdProjeto(result.getInt("id"))
			    .setNome(result.getString("nome"))
			    .setTipo(result.getString("tipo"))
			    .setDescricao(result.getString("descricao"))
			    .setStatus(result.getString("status"))
			    .setLocalizacao(result.getString("localizacao"))
			    .setDuracao(result.getInt("duracao"))
			    .setOrcamento(result.getDouble("orcamento"))
			    .setDataInicio(projetoDataInicio)
			    .setDataTermino(projetoDataTermino)
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
				.setQtdFuncionarios(result.getInt("equipe.qtdFuncionarios"))));
	    }
	}
    }

    public List<Recurso> search() throws SQLException
    {
	String sql = """
		SELECT 
		    Recurso.idRecurso AS id,
		    Recurso.tipo AS tipo,
		    Recurso.quantidade AS quantidade,
		    Recurso.custoUnitario AS custoUnitario,
		    Recurso.fornecedor AS fornecedor,
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
		       Recurso
			JOIN
		    Projeto ON Recurso.Projeto_id = Projeto.idProjeto
			    JOIN
		       Gestor ON Projeto.Gestor_id = Gestor.idGestor
			    JOIN
		       Equipe ON Projeto.Equipe_id = Equipe.idEquipe""";

	List<Recurso> lista = new ArrayList<>();

	try (PreparedStatement stm = conexao.prepareStatement(sql);
		ResultSet result = stm.executeQuery())
	{

	    while (result.next())
	    {
		Timestamp dataTimestamp = result.getTimestamp("projeto.dataInicio");
		LocalDateTime projetoDataInicio = dataTimestamp != null ? dataTimestamp.toLocalDateTime() : null;

		Timestamp dataTimestamp2 = result.getTimestamp("projeto.dataTermino");
		LocalDateTime projetoDataTermino = dataTimestamp2 != null ? dataTimestamp2.toLocalDateTime() : null;

		lista.add(new Recurso()
			.setIdRecurso(result.getInt("id"))
			.setTipo(result.getString("tipo"))
			.setQuantidade(result.getInt("quantidade"))
			.setCustoUnitario(result.getDouble("custoUnitario"))
			.setFornecedor(result.getString("fornecedor"))
			.setProjeto(new Projeto()
			    .setIdProjeto(result.getInt("id"))
			    .setNome(result.getString("nome"))
			    .setTipo(result.getString("tipo"))
			    .setDescricao(result.getString("descricao"))
			    .setStatus(result.getString("status"))
			    .setLocalizacao(result.getString("localizacao"))
			    .setDuracao(result.getInt("duracao"))
			    .setOrcamento(result.getDouble("orcamento"))
			    .setDataInicio(projetoDataInicio)
			    .setDataTermino(projetoDataTermino)
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
				.setQtdFuncionarios(result.getInt("equipe.qtdFuncionarios")))));
	    }
	}

	return lista;
    }

    public void update(Recurso recurso) throws SQLException
    {
	PreparedStatement stm = conexao.prepareStatement("UPDATE Recurso SET tipo = ?, quantidade = ?, custoUnitario = ?, fornecedor = ?, Projeto_id = ? where Recurso.idRecurso= ?");
	stm.setString(1, recurso.getTipo());
	stm.setInt(2, recurso.getQuantidade());
	stm.setDouble(3, recurso.getCustoUnitario());
	stm.setString(4, recurso.getFornecedor());
	stm.setInt(5, recurso.getProjeto().getIdProjeto());
	stm.setInt(6, recurso.getIdRecurso());
	stm.executeUpdate();
    }

    public void delete(int id) throws SQLException, NotFoundException
    {
	PreparedStatement stm = conexao.prepareStatement("DELETE from Recurso where Recurso.idRecurso = ?");
	stm.setInt(1, id);
	int linha = stm.executeUpdate();
	if (linha == 0)
	    throw new NotFoundException("Recurso não encontrado para ser removido");
    }

}
