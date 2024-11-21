package br.com.gs2.view;

import br.com.gs2.dao.ProjetoDao;
import br.com.gs2.dao.RecursoDao;
import br.com.gs2.exception.NotFoundException;
import br.com.gs2.factory.ConnectionFactory;
import br.com.gs2.model.Projeto;
import br.com.gs2.model.Recurso;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class RecursoView
{

    public static void menu(Scanner scanner) throws NotFoundException
    {

	try
	{
	    int escolha = 0;
	    do
	    {
		System.out.println("""
		Escolha uma opção:
		1 - Cadastrar
		2 - Pesquisar por Código
		3 - Listar
		4 - Atualizar
		5 - Remover
		0 - Voltar ao Menu Principal
		""");
		try
		{
		    escolha = scanner.nextInt();
		} catch (InputMismatchException e)
		{
		    System.out.println("Entrada inválida! Por favor, insira um número.");
		    scanner.next();
		    continue;
		}

		switch (escolha)
		{
		    case 1 -> insert(scanner);
		    case 2 -> select(scanner);
		    case 3 -> search();
		    case 4 -> update(scanner);
		    case 5 -> delete(scanner);
		    case 0 -> System.out.println("Voltando ao menu principal...");
		    default -> System.out.println("Opção inválida! Tente novamente.");
		}
	    } while (escolha != 0);
	} catch (SQLException e)
	{
	    System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
	}

    }

    private static void insert(Scanner scanner) throws NotFoundException
    {
	Recurso novoRecurso = new Recurso();

	System.out.println("Digite o tipo do recurso:");
	novoRecurso.setTipo(scanner.next() + scanner.nextLine());

	System.out.println("Digite a quantidade do recurso:");
	novoRecurso.setQuantidade(scanner.nextInt());
	scanner.nextLine();

	System.out.println("Digite o custo unitário do recurso:");
	novoRecurso.setCustoUnitario(scanner.nextDouble());
	scanner.nextLine();

	System.out.println("Digite o fornecedor do recurso:");
	novoRecurso.setFornecedor(scanner.next() + scanner.nextLine());

	try (Connection conexao = ConnectionFactory.getConnection();
		ProjetoDao projetoDao = new ProjetoDao(conexao);
		RecursoDao recursoDao = new RecursoDao(conexao))
	{
	    List<Projeto> projetos = projetoDao.search();

	    if (projetos.isEmpty())
	    {
		System.out.println("Nenhum projeto encontrado. Operação de cadastro de recurso cancelada.");
		return;
	    }

	    System.out.println("Selecione o projeto para o recurso (ID):");
	    for (Projeto projeto : projetos)
	    {
		System.out.println("ID: " + projeto.getIdProjeto() + " - Nome: " + projeto.getNome());
	    }

	    Projeto projetoSelecionado = null;

	    while (projetoSelecionado == null)
	    {
		while (!scanner.hasNextInt())
		{
		    System.out.println("Entrada inválida. Por favor, digite um número inteiro correspondente ao ID do projeto.");
		    scanner.next();
		}

		int idProjeto = scanner.nextInt();
		scanner.nextLine();

		projetoSelecionado = projetos.stream()
			.filter(projeto -> projeto.getIdProjeto() == idProjeto)
			.findFirst()
			.orElse(null);

		if (projetoSelecionado == null)
		{
		    System.out.println("ID do projeto inválido. Tente novamente.");
		}
	    }

	    novoRecurso.setProjeto(projetoSelecionado);

	    int idRecurso = recursoDao.insert(novoRecurso);
	    System.out.println("Recurso cadastrado com sucesso!");

	    Recurso recursoCadastrado = recursoDao.select(idRecurso);
	    System.out.println("ID: " + recursoCadastrado.getIdRecurso() + " - Tipo: " + recursoCadastrado.getTipo());

	} catch (SQLException e)
	{
	    System.err.println("Erro ao cadastrar recurso: " + e.getMessage());
	}
    }

    private static void select(Scanner scanner)
    {
	System.out.println("Digite o código do recurso:");
	int id = scanner.nextInt();

	try (Connection conexao = ConnectionFactory.getConnection();
		RecursoDao recursoDao = new RecursoDao(conexao))
	{

	    Recurso recurso = recursoDao.select(id);

	    System.out.println("Recurso encontrado:");
	    System.out.println("ID: " + recurso.getIdRecurso());
	    System.out.println("Tipo: " + recurso.getTipo());
	    System.out.println("Quantidade: " + recurso.getQuantidade());
	    System.out.println("Custo Unitário: " + recurso.getCustoUnitario());
	    System.out.println("Fornecedor: " + recurso.getFornecedor());

	    Projeto projeto = recurso.getProjeto();
	    System.out.println("Projeto associado:");
	    System.out.println("  ID: " + projeto.getIdProjeto());
	    System.out.println("  Nome: " + projeto.getNome());

	} catch (SQLException | NotFoundException e)
	{
	    System.err.println("Erro ao pesquisar recurso: " + e.getMessage());
	}
    }

    private static void search()
    {
	try (Connection conexao = ConnectionFactory.getConnection();
		RecursoDao recursoDao = new RecursoDao(conexao))
	{

	    List<Recurso> recursos = recursoDao.search();

	    if (recursos.isEmpty())
	    {
		System.out.println("Nenhum recurso encontrado.");
		return;
	    }

	    System.out.println("Lista de recursos:");
	    for (Recurso recurso : recursos)
	    {
		System.out.println("Recurso encontrado:");
		System.out.println("ID: " + recurso.getIdRecurso());
		System.out.println("Tipo: " + recurso.getTipo());
		System.out.println("Quantidade: " + recurso.getQuantidade());
		System.out.println("Custo Unitário: " + recurso.getCustoUnitario());
		System.out.println("Fornecedor: " + recurso.getFornecedor());

		Projeto projeto = recurso.getProjeto();
		System.out.println("Projeto associado:");
		System.out.println("  ID: " + projeto.getIdProjeto());
		System.out.println("  Nome: " + projeto.getNome());
	    }

	} catch (SQLException e)
	{
	    System.err.println("Erro ao listar recursos: " + e.getMessage());
	}
    }

    private static void update(Scanner scanner) throws SQLException, NotFoundException
    {
	System.out.println("Digite o código do recurso que deseja atualizar:");
	int id = scanner.nextInt();
	scanner.nextLine();

	try (Connection conexao = ConnectionFactory.getConnection();
		ProjetoDao projetoDao = new ProjetoDao(conexao);
		RecursoDao recursoDao = new RecursoDao(conexao))
	{

	    Recurso recurso = recursoDao.select(id);
	    if (recurso == null)
	    {
		System.out.println("Recurso não encontrado.");
		return;
	    }

	    System.out.println("Digite o novo tipo do recurso:");
	    recurso.setTipo(scanner.next() + scanner.nextLine());

	    System.out.println("Digite a nova quantidade do recurso:");
	    recurso.setQuantidade(scanner.nextInt());
	    scanner.nextLine();

	    System.out.println("Digite o novo custo unitário do recurso:");
	    recurso.setCustoUnitario(scanner.nextDouble());
	    scanner.nextLine();

	    System.out.println("Digite o novo fornecedor do recurso:");
	    recurso.setFornecedor(scanner.next() + scanner.nextLine());

	    List<Projeto> projetos = projetoDao.search();

	    if (projetos.isEmpty())
	    {
		System.out.println("Nenhum projeto encontrado. Operação de atualizar de recurso cancelada.");
		return;
	    }

	    System.out.println("Selecione o projeto para o recurso (ID):");
	    for (Projeto projeto : projetos)
	    {
		System.out.println("ID: " + projeto.getIdProjeto() + " - Nome: " + projeto.getNome());
	    }

	    Projeto projetoSelecionado = null;

	    while (projetoSelecionado == null)
	    {
		while (!scanner.hasNextInt())
		{
		    System.out.println("Entrada inválida. Por favor, digite um número inteiro correspondente ao ID do projeto.");
		    scanner.next();
		}

		int idProjeto = scanner.nextInt();
		scanner.nextLine();

		projetoSelecionado = projetos.stream()
			.filter(projeto -> projeto.getIdProjeto() == idProjeto)
			.findFirst()
			.orElse(null);

		if (projetoSelecionado == null)
		{
		    System.out.println("ID do projeto inválido. Tente novamente.");
		}
	    }

	    recursoDao.update(recurso);
	    System.out.println("Recurso atualizado com sucesso!");

	} catch (SQLException e)
	{
	    System.err.println("Erro ao atualizar recurso: " + e.getMessage());
	}
    }

    private static void delete(Scanner scanner)
    {
	System.out.println("Digite o código do recurso que deseja remover:");
	int id = scanner.nextInt();
	scanner.nextLine();

	try (Connection conexao = ConnectionFactory.getConnection();
		RecursoDao recursoDao = new RecursoDao(conexao))
	{

	    Recurso recurso = recursoDao.select(id);
	    if (recurso == null)
	    {
		System.out.println("Recurso não encontrado.");
		return;
	    }

	    System.out.println("Tem certeza que deseja remover o recurso " + recurso.getTipo() + "? (S/N)");
	    String confirmacao = scanner.nextLine();
	    if (!confirmacao.equalsIgnoreCase("S"))
	    {
		System.out.println("Operação cancelada.");
		return;
	    }

	    recursoDao.delete(id);
	    System.out.println("Recurso removido com sucesso!");

	} catch (SQLException | NotFoundException e)
	{
	    System.err.println("Erro ao remover recurso: " + e.getMessage());
	}
    }

}