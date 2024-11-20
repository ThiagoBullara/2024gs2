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
		    Recurso.nome AS nome,
		    Recurso.tipo AS tipo,
		    Recurso.descricao AS descricao,
		    Recurso.status AS status,
		    Recurso.localizacao AS localizacao,
		    Recurso.duracao AS duracao,
		    Recurso.orcamento AS orcamento,
		    Recurso.dataInicio AS dataInicio,
		    Recurso.dataTermino AS dataTermino,
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
		
		int recursoID = result.getInt("id");
		String tipo = result.getString("tipo");
		int quantidade = result.getInt("quantidade");
		double custoUnitario = result.getDouble("custoUnitario");
		String fornecedor = result.getString("fornecedor");
		
		int projetoID = result.getInt("projeto.id");
		String projetoNome = result.getString("projeto.nome");
		String projetoTipo = result.getString("projeto.tipo");
		String projetoDescricao = result.getString("projeto.descricao");
		String projetoStatus = result.getString("projeto.status");
		String projetoLocalizacao = result.getString("projeto.localizacao");
		int projetoDuracao = result.getInt("projeto.duracao");
		double projetoOrcamento = result.getDouble("projeto.orcamento");

		Timestamp dataTimestamp = result.getTimestamp("projeto.dataInicio");
		LocalDateTime projetoDataInicio = dataTimestamp != null ? dataTimestamp.toLocalDateTime() : null;

		Timestamp dataTimestamp2 = result.getTimestamp("projeto.dataTermino");
		LocalDateTime projetoDataTermino = dataTimestamp != null ? dataTimestamp.toLocalDateTime() : null;

		int gestorID = result.getInt("gestor.id");
		String gestorNome = result.getString("gestor.nome");
		String gestorEmail = result.getString("gestor.emial");
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

		Projeto projeto = new Projeto()
			.setIdProjeto(projetoID)
			.setNome(projetoNome)
			.setTipo(projetoTipo)
			.setDescricao(projetoDescricao)
			.setStatus(projetoStatus)
			.setLocalizacao(projetoLocalizacao)
			.setDuracao(projetoDuracao)
			.setOrcamento(projetoOrcamento)
			.setDataInicio(projetoDataInicio)
			.setDataTermino(projetoDataTermino)
			.setGestor(gestor)
			.setEquipe(equipe);
		
		return new Recurso()
			.setIdRecurso(recursoID)
			.setTipo(tipo)
			.setQuantidade(quantidade)
			.setCustoUnitario(custoUnitario)
			.setFornecedor(fornecedor)
			.setProjeto(projeto);
	    }
	}
    }

    public List<Recurso> search() throws SQLException
    {
	String sql = """
		SELECT 
		    Recurso.idRecurso AS id,
		    Recurso.nome AS nome,
		    Recurso.tipo AS tipo,
		    Recurso.descricao AS descricao,
		    Recurso.status AS status,
		    Recurso.localizacao AS localizacao,
		    Recurso.duracao AS duracao,
		    Recurso.orcamento AS orcamento,
		    Recurso.dataInicio AS dataInicio,
		    Recurso.dataTermino AS dataTermino,
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
		int recursoID = result.getInt("id");
		String tipo = result.getString("tipo");
		int quantidade = result.getInt("quantidade");
		double custoUnitario = result.getDouble("custoUnitario");
		String fornecedor = result.getString("fornecedor");
		
		int projetoID = result.getInt("projeto.id");
		String projetoNome = result.getString("projeto.nome");
		String projetoTipo = result.getString("projeto.tipo");
		String projetoDescricao = result.getString("projeto.descricao");
		String projetoStatus = result.getString("projeto.status");
		String projetoLocalizacao = result.getString("projeto.localizacao");
		int projetoDuracao = result.getInt("projeto.duracao");
		double projetoOrcamento = result.getDouble("projeto.orcamento");

		Timestamp dataTimestamp = result.getTimestamp("projeto.dataInicio");
		LocalDateTime projetoDataInicio = dataTimestamp != null ? dataTimestamp.toLocalDateTime() : null;

		Timestamp dataTimestamp2 = result.getTimestamp("projeto.dataTermino");
		LocalDateTime projetoDataTermino = dataTimestamp != null ? dataTimestamp.toLocalDateTime() : null;

		int gestorID = result.getInt("gestor.id");
		String gestorNome = result.getString("gestor.nome");
		String gestorEmail = result.getString("gestor.emial");
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

		Projeto projeto = new Projeto()
			.setIdProjeto(projetoID)
			.setNome(projetoNome)
			.setTipo(projetoTipo)
			.setDescricao(projetoDescricao)
			.setStatus(projetoStatus)
			.setLocalizacao(projetoLocalizacao)
			.setDuracao(projetoDuracao)
			.setOrcamento(projetoOrcamento)
			.setDataInicio(projetoDataInicio)
			.setDataTermino(projetoDataTermino)
			.setGestor(gestor)
			.setEquipe(equipe);

		lista.add(new Recurso()
			.setIdRecurso(recursoID)
			.setTipo(tipo)
			.setQuantidade(quantidade)
			.setCustoUnitario(custoUnitario)
			.setFornecedor(fornecedor)
			.setProjeto(projeto));
	    }
	}

	return lista;
    }

    public void update(Recurso recurso) throws SQLException
    {
	PreparedStatement stm = conexao.prepareStatement("UPDATE Recurso SET tipo = ?, quantidade = ?, custoUnitario = ?, forncedor = ?, Projeto_id = ? where Recurso.idRecurso= ?");
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
