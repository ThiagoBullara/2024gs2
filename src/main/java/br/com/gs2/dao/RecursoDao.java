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
	String sql = "INSERT INTO Recurso (tipo, quantidade, custo_unitario, fornecedor, FK_id_projeto) VALUES (?, ?, ?, ?, ?)";

	try (PreparedStatement stm = conexao.prepareStatement(sql, new String[]
	{
	    "id_recurso"
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
		    throw new SQLException("\nErro ao obter ID gerado.");
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
	    System.out.println("\nErro ao fechar a conexão: \n" + e.getMessage() + "\n");
	}
    }

    public Recurso select(int id) throws SQLException, NotFoundException
    {
	String sql = """
                 SELECT 
					Recurso.id_recurso AS id,
					Recurso.nome AS nome,
					Recurso.tipo AS tipo,
					Recurso.descricao AS descricao,
					Recurso.status AS status,
					Recurso.localizacao AS localizacao,
					Recurso.duracao AS duracao,
					Recurso.orcamento AS orcamento,
					Recurso.dataInicio AS dataInicio,
					Recurso.dataTermino AS dataTermino,
					Projeto.id_projeto AS "projeto.id",
					Projeto.nome AS "projeto.nome",
					Projeto.tipo AS "projeto.tipo",
					Projeto.descricao AS "projeto.descricao",
					Projeto.status AS "projeto.status",
					Projeto.localizacao AS "projeto.localizacao",
					Projeto.duracao AS "projeto.duracao",
					Projeto.orcamento AS "projeto.orcamento",
					Projeto.dataInicio AS "projeto.dataInicio",
					Projeto.dataTermino AS "projeto.dataTermino",
					Gestor.id_gestor AS "gestor.id",
					Gestor.nome AS "gestor.nome",
					Gestor.email AS "gestor.email",
					Gestor.telefone AS "gestor.telefone",
					Gestor.descricao AS "gestor.descricao",
					Equipe.id_equipe AS "equipe.id",
					Equipe.nome AS "equipe.nome",
					Equipe.especialidade AS "equipe.especialidade",
					Equipe.email AS "equipe.email",
					Equipe.descricao AS "equipe.descricao",
					Equipe.qtd_funcionarios AS "equipe.qtdFuncionarios"
                 FROM
                    Recurso
				 JOIN
		   			Projeto ON Recurso.FK_id_projeto = Projeto.id_projeto
                 JOIN
                    Gestor ON Projeto.FK_id_gestor = Gestor.id_gestor
                 JOIN
                    Equipe ON Projeto.FK_id_equipe = Equipe.id_equipe
                 WHERE
                    Recurso.id_recurso = ?""";

	try (PreparedStatement stm = conexao.prepareStatement(sql))
	{
	    stm.setInt(1, id);
	    try (ResultSet result = stm.executeQuery())
	    {
		if (!result.next())
		{
		    throw new NotFoundException("\nRecurso não encontrado");
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
			    .setIdProjeto(result.getInt("projeto.id"))
			    .setNome(result.getString("projeto.nome"))
			    .setTipo(result.getString("projeto.tipo"))
			    .setDescricao(result.getString("projeto.descricao"))
			    .setStatus(result.getString("projeto.status"))
			    .setLocalizacao(result.getString("projeto.localizacao"))
			    .setDuracao(result.getInt("projeto.duracao"))
			    .setOrcamento(result.getDouble("projeto.orcamento"))
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
				.setNome(result.getString("equipe.nome"))
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
				Recurso.id_recurso AS id,
				Recurso.nome AS nome,
				Recurso.tipo AS tipo,
				Recurso.descricao AS descricao,
				Recurso.status AS status,
				Recurso.localizacao AS localizacao,
				Recurso.duracao AS duracao,
				Recurso.orcamento AS orcamento,
				Recurso.dataInicio AS dataInicio,
				Recurso.dataTermino AS dataTermino,
				Projeto.id_projeto AS "projeto.id",
				Projeto.nome AS "projeto.nome",
				Projeto.tipo AS "projeto.tipo",
				Projeto.descricao AS "projeto.descricao",
				Projeto.status AS "projeto.status",
				Projeto.localizacao AS "projeto.localizacao",
				Projeto.duracao AS "projeto.duracao",
				Projeto.orcamento AS "projeto.orcamento",
				Projeto.dataInicio AS "projeto.dataInicio",
				Projeto.dataTermino AS "projeto.dataTermino",
				Gestor.id_gestor AS "gestor.id",
				Gestor.nome AS "gestor.nome",
				Gestor.email AS "gestor.email",
				Gestor.telefone AS "gestor.telefone",
				Gestor.descricao AS "gestor.descricao",
				Equipe.id_equipe AS "equipe.id",
				Equipe.nome AS "equipe.nome",
				Equipe.especialidade AS "equipe.especialidade",
				Equipe.email AS "equipe.email",
				Equipe.descricao AS "equipe.descricao",
				Equipe.qtd_funcionarios AS "equipe.qtdFuncionarios"
		    FROM
		    	Recurso
			JOIN
		    	Projeto ON Recurso.FK_id_projeto = Projeto.id_projeto
			JOIN
		    	Gestor ON Projeto.FK_id_gestor = Gestor.id_gestor
			JOIN
		    	Equipe ON Projeto.FK_id_equipe = Equipe.id_equipe""";

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
				.setNome(result.getString("equipe.nome"))
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
	PreparedStatement stm = conexao.prepareStatement("UPDATE Recurso SET tipo = ?, quantidade = ?, custo_unitario = ?, forncedor = ?, Projeto_id = ? where Recurso.id_recurso= ?");
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
	PreparedStatement stm = conexao.prepareStatement("DELETE from Recurso where Recurso.id_recurso = ?");
	stm.setInt(1, id);
	int linha = stm.executeUpdate();
	if (linha == 0)
	    throw new NotFoundException("\nRecurso não encontrado para ser removido");
    }

}