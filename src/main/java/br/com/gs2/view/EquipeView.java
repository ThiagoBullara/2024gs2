package br.com.gs2.view;

import br.com.gs2.dao.EquipeDao;
import br.com.gs2.exception.NotFoundException;
import br.com.gs2.factory.ConnectionFactory;
import br.com.gs2.model.Equipe;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class EquipeView
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

    private static void insert(Scanner scanner) throws SQLException, NotFoundException
    {
	Equipe novaEquipe = new Equipe();
	System.out.print("\nDigite o nome da equipe: ");
	novaEquipe.setNome(scanner.next() + scanner.nextLine());
	System.out.print("\nDigite a especialidade da equipe: ");
	novaEquipe.setEspecialidade(scanner.next() + scanner.nextLine());
	System.out.print("\nDigite o email da equipe: ");
	novaEquipe.setEmail(scanner.next() + scanner.nextLine());
	System.out.print("\nDigite uma descricao para a equipe: ");
	novaEquipe.setDescricao(scanner.next() + scanner.nextLine());
	System.out.print("\nDigite a quantidade de funcionários na equipe: ");
	while (!scanner.hasNextInt())
	{
	    System.out.print("\nEntrada inválida. Digite um número inteiro: ");
	    scanner.next();
	}
	novaEquipe.setQtdFuncionarios(scanner.nextInt());
	scanner.nextLine();

	try (Connection conexao = ConnectionFactory.getConnection();
		EquipeDao equipeDao = new EquipeDao(conexao))
	{
	    int id = equipeDao.insert(novaEquipe);
	    System.out.println("\nEquipe cadastrada com sucesso!");
	    Equipe equipe = equipeDao.select(id);
	    System.out.println("\nID: " + equipe.getIdEquipe() + " - " + equipe.getNome());

	} catch (SQLException e)
	{
	    System.err.print("\nErro ao cadastrar equipe: \n" + e.getMessage() + "\n");
	}
    }

    private static void select(Scanner scanner)
    {
	System.out.print("\nDigite o código do equipe: ");
	int id = scanner.nextInt();
	try (Connection conexao = ConnectionFactory.getConnection();
		EquipeDao equipeDao = new EquipeDao(conexao))
	{
	    Equipe equipe = equipeDao.select(id);
	    System.out.println("\nEquipe encontrada: ");
	    System.out.println("\nID: " + equipe.getIdEquipe()+ " \nNome: " + equipe.getNome()
		    + " \nEspecialidade: " + equipe.getEspecialidade() + " \nEmail: " + equipe.getEmail()
		    + " \nDescricao: " + equipe.getDescricao()+ " \nQuantidade de Funcionários: " + equipe.getQtdFuncionarios());
	} catch (SQLException | NotFoundException e)
	{
	    System.err.print("\nErro ao pesquisar equipe: \n" + e.getMessage() + "\n");
	}
    }

    private static void search()
    {
	try (Connection conexao = ConnectionFactory.getConnection();
		EquipeDao equipeDao = new EquipeDao(conexao))
	{
	    List<Equipe> equipes = equipeDao.search();
	    System.out.println("\nLista de equipes: ");
	    for (Equipe equipe : equipes)
	    {
			System.out.println("\nID: " + equipe.getIdEquipe()+ " \nNome: " + equipe.getNome()
		    + " \nEspecialidade: " + equipe.getEspecialidade() + " \nEmail: " + equipe.getEmail()
		    + " \nDescricao: " + equipe.getDescricao()+ " \nQuantidade de Funcionários: " + equipe.getQtdFuncionarios());
	    }
	} catch (SQLException e)
	{
	    System.err.print("\nErro ao listar equipes: \n" + e.getMessage() + "\n");
	}
    }

    private static void update(Scanner scanner)
    {
	System.out.print("\nDigite o código da equipe que deseja atualizar: ");
	int id = scanner.nextInt();
	try (Connection conexao = ConnectionFactory.getConnection();
		EquipeDao equipeDao = new EquipeDao(conexao))
	{
	    Equipe equipe = equipeDao.select(id);
	    System.out.print("\nDigite o novo nome da equipe: ");
	    equipe.setNome(scanner.next() + scanner.nextLine());
	    System.out.print("\nDigite a nova especialidade da equipe: ");
	    equipe.setEspecialidade(scanner.next() + scanner.nextLine());
	    System.out.print("\nDigite o novo email da equipe: ");
	    equipe.setEmail(scanner.next() + scanner.nextLine());
	    System.out.print("\nDigite uma nova descricao para a equipe: ");
	    equipe.setDescricao(scanner.next() + scanner.nextLine());
	    System.out.print("\nDigite a nova quantidade de funcionários na equipe: ");
	    equipe.setQtdFuncionarios(scanner.nextInt());
	    equipeDao.update(equipe);
	    System.out.println("\nEquipe atualizada com sucesso!");
	} catch (SQLException | NotFoundException e)
	{
	    System.err.print("\nErro ao atualizar equipe: \n" + e.getMessage() + "\n");
	}
    }

    private static void delete(Scanner scanner)
    {
	System.out.print("\nDigite o código da equipe que deseja remover: ");
	int id = scanner.nextInt();
	try (Connection conexao = ConnectionFactory.getConnection();
		EquipeDao equipeDao = new EquipeDao(conexao))
	{
	    equipeDao.delete(id);
	    System.out.println("\nEquipe removida com sucesso!");
	} catch (SQLException | NotFoundException e)
	{
	    System.err.print("\nErro ao remover equipe: \n" + e.getMessage() + "\n");
	}
    }
}