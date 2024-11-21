package br.com.gs2.dao;

import br.com.gs2.exception.NotFoundException;
import br.com.gs2.factory.ConnectionFactory;
import br.com.gs2.model.Equipe;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EquipeDao implements AutoCloseable
{

    private final Connection conexao;

    public EquipeDao(Connection conexao) throws SQLException
    {
	this.conexao = conexao;
    }

    public EquipeDao() throws SQLException
    {
	conexao = ConnectionFactory.getConnection();
    }

    public Connection getConnection()
    {
	return conexao;
    }

    public int insert(Equipe equipe) throws SQLException
    {
	String sql = "INSERT INTO Equipe (nome, especialidade, email, descricao, qtdFuncionarios) VALUES (?, ?, ?, ?, ?)";

	try (PreparedStatement stm = conexao.prepareStatement(sql, new String[]
	{
	    "idEquipe"
	}))
	{
	    stm.setString(1, equipe.getNome());
	    stm.setString(2, equipe.getEspecialidade());
	    stm.setString(3, equipe.getEmail());
	    stm.setString(4, equipe.getDescricao());
	    stm.setInt(5, equipe.getQtdFuncionarios());
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

    public Equipe select(int id) throws SQLException, NotFoundException
    {
	String sql = """
                 SELECT 
                     Equipe.idEquipe AS id,
                     Equipe.nome AS nome,
                     Equipe.especialidade AS especialidade,
                     Equipe.email AS email,
                     Equipe.descricao AS descricao,
                     Equipe.qtdFuncionarios AS qtdFuncionarios
                 FROM
                     Equipe
                 WHERE
                     Equipe.idEquipe = ?""";

	try (PreparedStatement stm = conexao.prepareStatement(sql))
	{
	    stm.setInt(1, id);
	    try (ResultSet result = stm.executeQuery())
	    {
		if (!result.next())
		{
		    throw new NotFoundException("Equipe não encontrada");
		}
		return new Equipe()
			.setIdEquipe(result.getInt("id"))
			.setNome(result.getString("nome"))
			.setEspecialidade(result.getString("especialidade"))
			.setEmail(result.getString("email"))
			.setDescricao(result.getString("descricao"))
			.setQtdFuncionarios(result.getInt("qtdFuncionarios"));
	    }
	}
    }

    public List<Equipe> search() throws SQLException
    {
	String sql = """
		SELECT 
		    Equipe.idEquipe AS id,
		    Equipe.nome AS nome,
		    Equipe.especialidade AS especialidade,
		    Equipe.email AS email,
		    Equipe.descricao AS descricao,
		    Equipe.qtdFuncionarios AS qtdFuncionarios
		FROM
		    Equipe""";

	List<Equipe> lista = new ArrayList<>();

	try (PreparedStatement stm = conexao.prepareStatement(sql);
		ResultSet result = stm.executeQuery())
	{

	    while (result.next())
	    {
		lista.add(new Equipe()
			.setIdEquipe(result.getInt("id"))
			.setNome(result.getString("nome"))
			.setEspecialidade(result.getString("especialidade"))
			.setEmail(result.getString("email"))
			.setDescricao(result.getString("descricao"))
			.setQtdFuncionarios(result.getInt("qtdFuncionarios")));
	    }
	}

	return lista;
    }

    public void update(Equipe equipe) throws SQLException
    {
	PreparedStatement stm = conexao.prepareStatement("UPDATE Equipe SET nome = ?, especialidade = ?, email = ?, descricao = ?, qtdFuncionarios = ? where Equipe.idEquipe= ?");
	stm.setString(1, equipe.getNome());
	stm.setString(2, equipe.getEspecialidade());
	stm.setString(3, equipe.getEmail());
	stm.setString(4, equipe.getDescricao());
	stm.setInt(5, equipe.getQtdFuncionarios());
	stm.setInt(6, equipe.getIdEquipe());
	stm.executeUpdate();
    }

    public void delete(int id) throws SQLException, NotFoundException
    {
	PreparedStatement stm = conexao.prepareStatement("DELETE from Equipe where Equipe.idEquipe = ?");
	stm.setInt(1, id);
	int linha = stm.executeUpdate();
	if (linha == 0)
	    throw new NotFoundException("Equipe não encontrada para ser removida");
    }

}