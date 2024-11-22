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
		\nEscolha uma opção:\n
		1 - Cadastrar
		2 - Pesquisar por Código
		3 - Listar
		4 - Atualizar
		5 - Remover
		0 - Voltar ao Menu Principal
		""");
		try
		{
			System.out.print("Opção: ");
		    escolha = scanner.nextInt();
		} catch (InputMismatchException e)
		{
		    System.out.println("\nEntrada inválida! Por favor, insira um número.");
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
		    case 0 -> System.out.println("\nVoltando ao menu principal...");
		    default -> System.out.println("\nOpção inválida! Tente novamente.");
		}
	    } while (escolha != 0);
	} catch (SQLException e)
	{
	    System.err.print("\nErro ao conectar ao banco de dados: \n" + e.getMessage() + "\n");
	}

    }

    private static void insert(Scanner scanner) throws NotFoundException
    {
	Recurso novoRecurso = new Recurso();

	System.out.print("\nDigite o tipo do recurso: ");
	novoRecurso.setTipo(scanner.next() + scanner.nextLine());
	System.out.print("\nDigite a quantidade do recurso: ");
	while (!scanner.hasNextInt())
	{
	    System.out.print("\nEntrada inválida. Digite um número inteiro: ");
	    scanner.next();
	}
	novoRecurso.setQuantidade(scanner.nextInt());
	scanner.nextLine();
	System.out.print("\nDigite o custo unitário do recurso: ");
	while (!scanner.hasNextInt())
	{
	    System.out.print("\nEntrada inválida. Digite um número inteiro: ");
	    scanner.next();
	}
	novoRecurso.setCustoUnitario(scanner.nextInt());
	scanner.nextLine();
	System.out.print("\nDigite o fornecedor do recurso: ");
	novoRecurso.setFornecedor(scanner.next() + scanner.nextLine());

	try (Connection conexao = ConnectionFactory.getConnection();
		ProjetoDao projetoDao = new ProjetoDao(conexao);
		RecursoDao recursoDao = new RecursoDao(conexao))
	{
	    List<Projeto> projetos = projetoDao.search();

	    if (projetos.isEmpty())
	    {
		System.out.println("\nNenhum projeto encontrado. Operação de cadastro de recurso cancelada.");
		return;
	    }

	    System.out.print("\nSelecione o projeto para o recurso (ID): ");
	    for (Projeto projeto : projetos)
	    {
		System.out.println("\nID: " + projeto.getIdProjeto() + " - Nome: " + projeto.getNome());
	    }

	    Projeto projetoSelecionado = null;

	    while (projetoSelecionado == null)
	    {
		while (!scanner.hasNextInt())
		{
		    System.out.println("\nEntrada inválida. Por favor, digite um número inteiro correspondente ao ID do projeto.");
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
		    System.out.println("\nID do projeto inválido. Tente novamente.");
		}
	    }

	    novoRecurso.setProjeto(projetoSelecionado);

	    int idRecurso = recursoDao.insert(novoRecurso);
	    System.out.println("\nRecurso cadastrado com sucesso!");

	    Recurso recursoCadastrado = recursoDao.select(idRecurso);
	    System.out.println("\nID: " + recursoCadastrado.getIdRecurso() + " - Tipo: " + recursoCadastrado.getTipo());

	} catch (SQLException e)
	{
	    System.err.println("\nErro ao cadastrar recurso: \n" + e.getMessage() + "\n");
	}
    }

    private static void select(Scanner scanner)
    {
	System.out.print("\nDigite o código do recurso: ");
	int id = scanner.nextInt();

	try (Connection conexao = ConnectionFactory.getConnection();
		RecursoDao recursoDao = new RecursoDao(conexao))
	{

	    Recurso recurso = recursoDao.select(id);

		System.out.println("\n-----------------------------------------------------------------");
	    System.out.println("\nRecurso encontrado:");
	    System.out.println("\nID: " + recurso.getIdRecurso());
	    System.out.println("Tipo: " + recurso.getTipo());
	    System.out.println("Quantidade: " + recurso.getQuantidade());
	    System.out.println("Custo Unitário: " + recurso.getCustoUnitario());
	    System.out.println("Fornecedor: " + recurso.getFornecedor());

	    Projeto projeto = recurso.getProjeto();
	    System.out.println("\nProjeto associado:");
	    System.out.println("\nID: " + projeto.getIdProjeto());
	    System.out.println("Nome: " + projeto.getNome());
		System.out.println("\n-----------------------------------------------------------------");

	} catch (SQLException | NotFoundException e)
	{
	    System.err.println("\nErro ao pesquisar recurso: \n" + e.getMessage() + "\n");
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
		System.out.println("\nNenhum recurso encontrado.");
		return;
	    }

	    System.out.println("\nLista de recursos:");
	    for (Recurso recurso : recursos)
	    {
		System.out.println("\n-----------------------------------------------------------------");
		System.out.println("\nID: " + recurso.getIdRecurso());
		System.out.println("Tipo: " + recurso.getTipo());
		System.out.println("Quantidade: " + recurso.getQuantidade());
		System.out.println("Custo Unitário: " + recurso.getCustoUnitario());
		System.out.println("Fornecedor: " + recurso.getFornecedor());

		Projeto projeto = recurso.getProjeto();
		System.out.println("\nProjeto associado:");
		System.out.println("ID: " + projeto.getIdProjeto());
		System.out.println("Nome: " + projeto.getNome());
	    }
		System.out.println("\n-----------------------------------------------------------------");

	} catch (SQLException e)
	{
	    System.err.println("\nErro ao listar recursos: \n" + e.getMessage() + "\n");
	}
    }

    private static void update(Scanner scanner) throws SQLException, NotFoundException
    {
	System.out.print("\nDigite o código do recurso que deseja atualizar: ");
	int id = scanner.nextInt();
	scanner.nextLine();

	try (Connection conexao = ConnectionFactory.getConnection();
		ProjetoDao projetoDao = new ProjetoDao(conexao);
		RecursoDao recursoDao = new RecursoDao(conexao))
	{

	    Recurso recurso = recursoDao.select(id);
	    if (recurso == null)
	    {
		System.out.println("\nRecurso não encontrado.");
		return;
	    }

	    System.out.print("\nDigite o novo tipo do recurso: ");
	    recurso.setTipo(scanner.next() + scanner.nextLine());
	    System.out.print("\nDigite a nova quantidade do recurso: ");
	    recurso.setQuantidade(scanner.nextInt());
	    scanner.nextLine();
	    System.out.print("\nDigite o novo custo unitário do recurso: ");
	    recurso.setCustoUnitario(scanner.nextDouble());
	    scanner.nextLine();
	    System.out.print("\nDigite o novo fornecedor do recurso: ");
	    recurso.setFornecedor(scanner.next() + scanner.nextLine());

	    List<Projeto> projetos = projetoDao.search();

	    if (projetos.isEmpty())
	    {
		System.out.println("\nNenhum projeto encontrado. Operação de atualizar de recurso cancelada.");
		return;
	    }

	    System.out.println("\nSelecione o projeto para o recurso (ID)");
	    for (Projeto projeto : projetos)
	    {
		System.out.println("\nID: " + projeto.getIdProjeto() + " - Nome: " + projeto.getNome());
		System.out.println("\n------------------------------------------------------------------------");
	    }

	    Projeto projetoSelecionado = null;
		System.out.print("Projeto: ");
	    while (projetoSelecionado == null)
	    {
		while (!scanner.hasNextInt())
		{
		    System.out.println("\nEntrada inválida. Por favor, digite um número inteiro correspondente ao ID do projeto.");
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
		    System.out.println("\nID do projeto inválido. Tente novamente.");
		}
	    }

	    recursoDao.update(recurso);
	    System.out.println("\nRecurso atualizado com sucesso!");

	} catch (SQLException e)
	{
	    System.err.println("\nErro ao atualizar recurso: \n" + e.getMessage() + "\n");
	}
    }

    private static void delete(Scanner scanner)
    {
	System.out.print("\nDigite o código do recurso que deseja remover: ");
	int id = scanner.nextInt();
	scanner.nextLine();

	try (Connection conexao = ConnectionFactory.getConnection();
		RecursoDao recursoDao = new RecursoDao(conexao))
	{

	    Recurso recurso = recursoDao.select(id);
	    if (recurso == null)
	    {
		System.out.println("\nRecurso não encontrado.");
		return;
	    }

	    System.out.print("\nTem certeza que deseja remover o recurso " + recurso.getTipo() + "? (S/N): ");
	    String confirmacao = scanner.nextLine();
	    if (!confirmacao.equalsIgnoreCase("S"))
	    {
		System.out.println("\nOperação cancelada.");
		return;
	    }

	    recursoDao.delete(id);
	    System.out.println("\nRecurso removido com sucesso!");

	} catch (SQLException | NotFoundException e)
	{
	    System.err.println("\nErro ao remover recurso: \n" + e.getMessage() + "\n");
	}
    }

}