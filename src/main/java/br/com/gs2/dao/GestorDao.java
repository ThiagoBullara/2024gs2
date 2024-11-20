package br.com.gs2.dao;

import br.com.gs2.exception.NotFoundException;
import br.com.gs2.factory.ConnectionFactory;
import br.com.gs2.model.Gestor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestorDao implements AutoCloseable
{

    private final Connection conexao;

    public GestorDao(Connection conexao) throws SQLException
    {
	this.conexao = conexao;
    }

    public GestorDao() throws SQLException
    {
	conexao = ConnectionFactory.getConnection();
    }

    public Connection getConnection()
    {
	return conexao;
    }

    public int insert(Gestor gestor) throws SQLException
    {
	String sql = "INSERT INTO Gestor (nome, email, telefone, descricao) VALUES (?, ?, ?, ?)";

	try (PreparedStatement stm = conexao.prepareStatement(sql, new String[]
	{
	    "idGestor"
	}))
	{
	    stm.setString(1, gestor.getNome());
	    stm.setString(2, gestor.getEmail());
	    stm.setString(3, gestor.getTelefone());
	    stm.setString(4, gestor.getDescricao());
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

    public Gestor select(int id) throws SQLException, NotFoundException
    {
	String sql = """
                 SELECT 
                     Gestor.idGestor AS id,
                     Gestor.nome AS nome,
                     Gestor.email AS email,
                     Gestor.telefone AS telefone,
                     Gestor.descricao AS descricao
                 FROM
                     Gestor
                 WHERE
                     Gestor.idGestor = ?""";

	try (PreparedStatement stm = conexao.prepareStatement(sql))
	{
	    stm.setInt(1, id);
	    try (ResultSet result = stm.executeQuery())
	    {
		if (!result.next())
		{
		    throw new NotFoundException("Gestor não encontrado");
		}

		int gestorID = result.getInt("id");
		String nome = result.getString("nome");
		String email = result.getString("email");
		String telefone = result.getString("telefone");
		String descricao = result.getString("descricao");

		return new Gestor()
			.setIdGestor(gestorID)
			.setNome(nome)
			.setEmail(email)
			.setTelefone(telefone)
			.setDescricao(descricao);
	    }
	}
    }

    public List<Gestor> search() throws SQLException
    {
	String sql = """
		SELECT 
		    Gestor.idGestor AS id,
		    Gestor.nome AS nome,
		    Gestor.email AS email,
		    Gestor.telefone AS telefone,
		    Gestor.descricao AS descricao
		FROM
		    Gestor""";

	List<Gestor> lista = new ArrayList<>();

	try (PreparedStatement stm = conexao.prepareStatement(sql);
		ResultSet result = stm.executeQuery())
	{

	    while (result.next())
	    {
		int gestorID = result.getInt("id");
		String nome = result.getString("nome");
		String email = result.getString("email");
		String telefone = result.getString("telefone");
		String descricao = result.getString("descricao");

		lista.add(new Gestor()
			.setIdGestor(gestorID)
			.setNome(nome)
			.setEmail(email)
			.setTelefone(telefone)
			.setDescricao(descricao));
	    }
	}

	return lista;
    }

    public void update(Gestor gestor) throws SQLException
    {
	PreparedStatement stm = conexao.prepareStatement("UPDATE Gestor SET nome = ?, email = ?, telefone = ?, descricao = ? where Gestor.idGestor= ?");
	stm.setString(1, gestor.getNome());
	stm.setString(2, gestor.getEmail());
	stm.setString(3, gestor.getTelefone());
	stm.setString(4, gestor.getDescricao());
	stm.setInt(5, gestor.getIdGestor());
	stm.executeUpdate();
    }

    public void delete(int id) throws SQLException, NotFoundException
    {
	PreparedStatement stm = conexao.prepareStatement("DELETE from Gestor where Gestor.idGestor = ?");
	stm.setInt(1, id);
	int linha = stm.executeUpdate();
	if (linha == 0)
	    throw new NotFoundException("Gestor não encontrado para ser removido");
    }

}