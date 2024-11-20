package br.com.gs2.dao;

import br.com.gs2.exception.NotFoundException;
import br.com.gs2.factory.ConnectionFactory;
import br.com.gs2.model.Equipe;
import br.com.gs2.model.Gestor;
import br.com.gs2.model.Projeto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProjetoDao implements AutoCloseable
{

    private final Connection conexao;

    public ProjetoDao(Connection conexao) throws SQLException
    {
	this.conexao = conexao;
    }

    public ProjetoDao() throws SQLException
    {
	conexao = ConnectionFactory.getConnection();
    }

    public Connection getConnection()
    {
	return conexao;
    }

    public int insert(Projeto projeto) throws SQLException
    {
	String sql = "INSERT INTO Projeto (nome, tipo, descricao, status, localizacao, duracao, orcamento, dataInicio, dataTermino, Gestor_id, Equipe_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	try (PreparedStatement stm = conexao.prepareStatement(sql, new String[]
	{
	    "idProjeto"
	}))
	{
	    stm.setString(1, projeto.getNome());
	    stm.setString(2, projeto.getTipo());
	    stm.setString(3, projeto.getDescricao());
	    stm.setString(4, projeto.getStatus());
	    stm.setString(5, projeto.getLocalizacao());
	    stm.setInt(6, projeto.getDuracao());
	    stm.setDouble(7, projeto.getOrcamento());
	    stm.setTimestamp(8, Timestamp.valueOf(projeto.getDataInicio()));
	    stm.setTimestamp(9, Timestamp.valueOf(projeto.getDataTermino()));
	    stm.setInt(10, projeto.getGestor().getIdGestor());
	    stm.setInt(11, projeto.getEquipe().getIdEquipe());
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

    public Projeto select(int id) throws SQLException, NotFoundException
    {
	String sql = """
                 SELECT 
                     Projeto.idProjeto AS id,
                     Projeto.nome AS nome,
                     Projeto.tipo AS tipo,
                     Projeto.descricao AS descricao,
                     Projeto.status AS status,
                     Projeto.localizacao AS localizacao,
                     Projeto.duracao AS duracao,
                     Projeto.orcamento AS orcamento,
                     Projeto.dataInicio AS dataInicio,
                     Projeto.dataTermino AS dataTermino,
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
                     Projeto
			JOIN
		    Gestor ON Projeto.Gestor_id = Gestor.idGestor
			JOIN
		    Equipe ON Projeto.Equipe_id = Equipe.idEquipe
                 WHERE
                     Projeto.idProjeto = ?""";

	try (PreparedStatement stm = conexao.prepareStatement(sql))
	{
	    stm.setInt(1, id);
	    try (ResultSet result = stm.executeQuery())
	    {
		if (!result.next())
		{
		    throw new NotFoundException("Projeto não encontrado");
		}

		int projetoID = result.getInt("id");
		String nome = result.getString("nome");
		String tipo = result.getString("tipo");
		String descricao = result.getString("descricao");
		String status = result.getString("status");
		String localizacao = result.getString("localizacao");
		int duracao = result.getInt("duracao");
		double orcamento = result.getDouble("orcamento");
		
		
		Timestamp dataTimestamp = result.getTimestamp("dataInicio");
		LocalDateTime dataInicio = dataTimestamp != null ? dataTimestamp.toLocalDateTime() : null;
		
		Timestamp dataTimestamp2 = result.getTimestamp("dataTermino");
		LocalDateTime dataTermino = dataTimestamp != null ? dataTimestamp.toLocalDateTime() : null;
		
		int gestorID = result.getInt("gestor.id");
		String gestorNome = result.getString("gestor.nome");
		String gestorEmail = result.getString("gestor.email");
		String gestorTelefone = result.getString("gestor.telefone");
		String gestorDescricao = result.getString("gestor.descricao");

		int equipeID = result.getInt("equipe.id");
		String equipeEspecialidade = result.getString("equipe.especialidade");
		String equipeEmail = result.getString("equipe.email");
		String equipeDescricao = result.getString("equipe.descricao");
		int equipeQtdFuncionarios = result.getInt("equipe.qtdFuncionarios");

		Gestor gestor = new Gestor()
			.setIdGestor(gestorID)
			.setNome(gestorNome)
			.setEmail(gestorEmail)
			.setTelefone(gestorTelefone)
			.setDescricao(gestorDescricao);

		Equipe equipe = new Equipe()
			.setIdEquipe(equipeID)
			.setEspecialidade(equipeEspecialidade)
			.setEmail(equipeEmail)
			.setDescricao(equipeDescricao)
			.setQtdFuncionarios(equipeQtdFuncionarios);
		
		
		
		return new Projeto()
			.setIdProjeto(projetoID)
			.setNome(nome)
			.setTipo(tipo)
			.setDescricao(descricao)
			.setStatus(status)
			.setLocalizacao(localizacao)
			.setDuracao(duracao)
			.setOrcamento(orcamento)
			.setDataInicio(dataInicio)
			.setDataTermino(dataTermino)
			.setGestor(gestor)
			.setEquipe(equipe);
	    }
	}
    }

    public List<Projeto> search() throws SQLException
    {
	String sql = """
		SELECT 
		    Projeto.idProjeto AS id,
		    Projeto.nome AS nome,
		    Projeto.tipo AS tipo,
		    Projeto.descricao AS descricao,
		    Projeto.status AS status,
		    Projeto.localizacao AS localizacao,
		    Projeto.duracao AS duracao,
		    Projeto.orcamento AS orcamento,
		    Projeto.dataInicio AS dataInicio,
		    Projeto.dataTermino AS dataTermino,
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
		    Projeto
		       JOIN
		   Gestor ON Projeto.Gestor_id = Gestor.idGestor
		       JOIN
		   Equipe ON Projeto.Equipe_id = Equipe.idEquipe""";

	List<Projeto> lista = new ArrayList<>();

	try (PreparedStatement stm = conexao.prepareStatement(sql);
		ResultSet result = stm.executeQuery())
	{

	    while (result.next())
	    {
		int projetoID = result.getInt("id");
		String nome = result.getString("nome");
		String tipo = result.getString("tipo");
		String descricao = result.getString("descricao");
		String status = result.getString("status");
		String localizacao = result.getString("localizacao");
		int duracao = result.getInt("duracao");
		double orcamento = result.getDouble("orcamento");
		
		
		Timestamp dataTimestamp = result.getTimestamp("dataInicio");
		LocalDateTime dataInicio = dataTimestamp != null ? dataTimestamp.toLocalDateTime() : null;
		
		Timestamp dataTimestamp2 = result.getTimestamp("dataTermino");
		LocalDateTime dataTermino = dataTimestamp != null ? dataTimestamp.toLocalDateTime() : null;
		
		int gestorID = result.getInt("gestor.id");
		String gestorNome = result.getString("gestor.nome");
		String gestorEmail = result.getString("gestor.email");
		String gestorTelefone = result.getString("gestor.telefone");
		String gestorDescricao = result.getString("gestor.descricao");

		int equipeID = result.getInt("equipe.id");
		String equipeEspecialidade = result.getString("equipe.especialidade");
		String equipeEmail = result.getString("equipe.email");
		String equipeDescricao = result.getString("equipe.descricao");
		int equipeQtdFuncionarios = result.getInt("equipe.qtdFuncionarios");

		Gestor gestor = new Gestor()
			.setIdGestor(gestorID)
			.setNome(gestorNome)
			.setEmail(gestorEmail)
			.setTelefone(gestorTelefone)
			.setDescricao(gestorDescricao);

		Equipe equipe = new Equipe()
			.setIdEquipe(equipeID)
			.setEspecialidade(equipeEspecialidade)
			.setEmail(equipeEmail)
			.setDescricao(equipeDescricao)
			.setQtdFuncionarios(equipeQtdFuncionarios);

		lista.add(new Projeto()
			.setIdProjeto(projetoID)
			.setNome(nome)
			.setTipo(tipo)
			.setDescricao(descricao)
			.setStatus(status)
			.setLocalizacao(localizacao)
			.setDuracao(duracao)
			.setOrcamento(orcamento)
			.setDataInicio(dataInicio)
			.setDataTermino(dataTermino)
			.setGestor(gestor)
			.setEquipe(equipe));
	    }
	}

	return lista;
    }

    public void update(Projeto projeto) throws SQLException
    {
	PreparedStatement stm = conexao.prepareStatement("UPDATE Projeto SET nome = ?, tipo = ?, descricao = ?, status = ?, localizacao = ?, duracao = ?, orcamento = ?, dataInicio = ?, dataTermino = ?, Gestor_id = ?, Equipe_id = ? where Projeto.idProjeto= ?");
	stm.setString(1, projeto.getNome());
	stm.setString(2, projeto.getTipo());
	stm.setString(3, projeto.getDescricao());
	stm.setString(4, projeto.getStatus());
	stm.setString(5, projeto.getLocalizacao());
	stm.setInt(6, projeto.getDuracao());
	stm.setDouble(7, projeto.getOrcamento());
	stm.setTimestamp(8, Timestamp.valueOf(projeto.getDataInicio()));
	stm.setTimestamp(9, Timestamp.valueOf(projeto.getDataTermino()));
	stm.setInt(10, projeto.getGestor().getIdGestor());
	stm.setInt(11, projeto.getEquipe().getIdEquipe());
	stm.setInt(12, projeto.getIdProjeto());
	stm.executeUpdate();
    }

    public void delete(int id) throws SQLException, NotFoundException
    {
	PreparedStatement stm = conexao.prepareStatement("DELETE from Projeto where Projeto.idProjeto = ?");
	stm.setInt(1, id);
	int linha = stm.executeUpdate();
	if (linha == 0)
	    throw new NotFoundException("Projeto não encontrado para ser removido");
    }

}