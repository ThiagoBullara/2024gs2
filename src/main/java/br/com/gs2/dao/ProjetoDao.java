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
	String sql = "INSERT INTO Projeto (nome, tipo, descricao, status, localizacao, duracao, orcamento, dataInicio, dataTermino, Fk_id_gestor, Fk_id_equipe) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	try (PreparedStatement stm = conexao.prepareStatement(sql, new String[]
	{
	    "id_projeto"
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
	    System.out.print("\nErro ao fechar a conexão: \n" + e.getMessage() + "\n");
	}
    }

    public Projeto select(int id) throws SQLException, NotFoundException
    {
	String sql = """
                SELECT 
                     Projeto.id_projeto AS id,
                     Projeto.nome AS nome,
                     Projeto.tipo AS tipo,
                     Projeto.descricao AS descricao,
                     Projeto.status AS status,
                     Projeto.localizacao AS localizacao,
                     Projeto.duracao AS duracao,
                     Projeto.orcamento AS orcamento,
                     Projeto.dataInicio AS dataInicio,
                     Projeto.dataTermino AS dataTermino,
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
                     Projeto
				JOIN
					 Gestor ON Projeto.Fk_id_gestor = Gestor.id_gestor
				JOIN
					 Equipe ON Projeto.FK_id_equipe = Equipe.id_equipe
				WHERE
					 Projeto.id_projeto = ?""";

	try (PreparedStatement stm = conexao.prepareStatement(sql))
	{
	    stm.setInt(1, id);
	    try (ResultSet result = stm.executeQuery())
	    {
		if (!result.next())
		{
		    throw new NotFoundException("\nProjeto não encontrado");
		}
		
		Timestamp dataInicio = result.getTimestamp("dataInicio");
		Timestamp dataTermino = result.getTimestamp("dataTermino");

		return new Projeto()
			.setIdProjeto(result.getInt("id"))
			.setNome(result.getString("nome"))
			.setTipo(result.getString("tipo"))
			.setDescricao(result.getString("descricao"))
			.setStatus(result.getString("status"))
			.setLocalizacao(result.getString("localizacao"))
			.setDuracao(result.getInt("duracao"))
			.setOrcamento(result.getDouble("orcamento"))
			.setDataInicio(dataInicio != null ? dataInicio.toLocalDateTime() : null)
			.setDataTermino(dataTermino != null ? dataTermino.toLocalDateTime() : null)
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
			    .setQtdFuncionarios(result.getInt("equipe.qtdFuncionarios")));
	    }
	}
    }

    public List<Projeto> search() throws SQLException
    {
	String sql = """
		SELECT 
		    Projeto.id_projeto AS id,
		    Projeto.nome AS nome,
		    Projeto.tipo AS tipo,
		    Projeto.descricao AS descricao,
		    Projeto.status AS status,
		    Projeto.localizacao AS localizacao,
		    Projeto.duracao AS duracao,
		    Projeto.orcamento AS orcamento,
		    Projeto.dataInicio AS dataInicio,
		    Projeto.dataTermino AS dataTermino,
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
		    Projeto
		JOIN
		    Gestor ON Projeto.FK_id_gestor = Gestor.id_gestor
		JOIN
		    Equipe ON Projeto.FK_id_equipe = Equipe.id_equipe""";

	List<Projeto> lista = new ArrayList<>();

	try (PreparedStatement stm = conexao.prepareStatement(sql);
		ResultSet result = stm.executeQuery())
	{

	    while (result.next())
	    {
		Timestamp dataTimestamp = result.getTimestamp("dataInicio");
		LocalDateTime dataInicio = dataTimestamp != null ? dataTimestamp.toLocalDateTime() : null;
		
		Timestamp dataTimestamp2 = result.getTimestamp("dataTermino");
		LocalDateTime dataTermino = dataTimestamp2 != null ? dataTimestamp2.toLocalDateTime() : null;

		lista.add(new Projeto()
			.setIdProjeto(result.getInt("id"))
			.setNome(result.getString("nome"))
			.setTipo(result.getString("tipo"))
			.setDescricao(result.getString("descricao"))
			.setStatus(result.getString("status"))
			.setLocalizacao(result.getString("localizacao"))
			.setDuracao(result.getInt("duracao"))
			.setOrcamento(result.getDouble("orcamento"))
			.setDataInicio(dataInicio)
			.setDataTermino(dataTermino)
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

	return lista;
    }

    public void update(Projeto projeto) throws SQLException
    {
	PreparedStatement stm = conexao.prepareStatement("UPDATE Projeto SET nome = ?, tipo = ?, descricao = ?, status = ?, localizacao = ?, duracao = ?, orcamento = ?, dataInicio = ?, dataTermino = ?, FK_id_gestor = ?, FK_id_equipe = ? WHERE Projeto.id_projeto= ?");
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
	PreparedStatement stm = conexao.prepareStatement("DELETE from Projeto where Projeto.id_projeto = ?");
	stm.setInt(1, id);
	int linha = stm.executeUpdate();
	if (linha == 0)
	    throw new NotFoundException("\nProjeto não encontrado para ser removido");
    }

}