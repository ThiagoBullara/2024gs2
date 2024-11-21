package br.com.gs2.view;

import br.com.gs2.dao.GestorDao;
import br.com.gs2.exception.NotFoundException;
import br.com.gs2.factory.ConnectionFactory;
import br.com.gs2.model.Gestor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class GestorView
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
	Gestor novoGestor = new Gestor();
	System.out.print("\nDigite o nome do gestor: ");
	novoGestor.setNome(scanner.next() + scanner.nextLine());
	System.out.print("\nDigite o email do gestor: ");
	novoGestor.setEmail(scanner.next() + scanner.nextLine());
	System.out.print("\nDigite o telefone do gestor: ");
	novoGestor.setTelefone(scanner.next() + scanner.nextLine());
	System.out.print("\nDigite uma descrição para o gestor: ");
	novoGestor.setDescricao(scanner.next() + scanner.nextLine());

	try (Connection conexao = ConnectionFactory.getConnection();
		GestorDao gestorDao = new GestorDao(conexao))
	{
	    int id = gestorDao.insert(novoGestor);
	    System.out.println("\nGestor cadastrado com sucesso!");
	    Gestor gestor = gestorDao.select(id);
	    System.out.println("\nID: " + gestor.getIdGestor() + " - " + gestor.getNome());

	} catch (SQLException e)
	{
	    System.err.print("\nErro ao cadastrar gestor: \n" + e.getMessage() + "\n");
	}
    }

    private static void select(Scanner scanner)
    {
	System.out.print("\nDigite o código do gestor: ");
	int id = scanner.nextInt();
	try (Connection conexao = ConnectionFactory.getConnection();
		GestorDao gestorDao = new GestorDao(conexao))
	{
	    Gestor gestor = gestorDao.select(id);
	    System.out.println("\nGestor encontrado: ");
	    System.out.println("\nID: " + gestor.getIdGestor() + " \nNome: " + gestor.getNome()
		    + " \nEmail: " + gestor.getEmail() + " \nTelefone: " + gestor.getTelefone()
		    + " \nDescrição: " + gestor.getDescricao());
	} catch (SQLException | NotFoundException e)
	{
	    System.err.print("\nErro ao pesquisar gestor: \n" + e.getMessage() + "\n");
	}
    }

    private static void search()
    {
	try (Connection conexao = ConnectionFactory.getConnection();
		GestorDao gestorDao = new GestorDao(conexao))
	{
	    List<Gestor> gestors = gestorDao.search();
	    System.out.println("\nLista de gestores: ");
	    for (Gestor gestor : gestors)
	    {
			System.out.println("\nID: " + gestor.getIdGestor() + " \nNome: " + gestor.getNome()
		    + " \nEmail: " + gestor.getEmail() + " \nTelefone: " + gestor.getTelefone()
		    + " \nDescrição: " + gestor.getDescricao());
	    }
	} catch (SQLException e)
	{
	    System.err.print("\nErro ao listar gestores: \n" + e.getMessage() + "\n");
	}
    }

    private static void update(Scanner scanner)
    {
	System.out.print("\nDigite o código do gestor que deseja atualizar: ");
	int id = scanner.nextInt();
	try (Connection conexao = ConnectionFactory.getConnection();
		GestorDao gestorDao = new GestorDao(conexao))
	{
	    Gestor gestor = gestorDao.select(id);

	    System.out.print("\nDigite o nome do gestor: ");
	    gestor.setNome(scanner.next() + scanner.nextLine());
	    System.out.print("\nDigite o email do gestor: ");
	    gestor.setEmail(scanner.next() + scanner.nextLine());
	    System.out.print("\nDigite o telefone do gestor: ");
	    gestor.setTelefone(scanner.next() + scanner.nextLine());
	    System.out.print("\nDigite uma descrição para o gestor: ");
	    gestor.setDescricao(scanner.next() + scanner.nextLine());
	    gestorDao.update(gestor);
	    System.out.println("\nGestor atualizado com sucesso!");
	} catch (SQLException | NotFoundException e)
	{
	    System.err.print("\nErro ao atualizar gestor: \n" + e.getMessage() + "\n");
	}
    }

    private static void delete(Scanner scanner)
    {
	System.out.print("\nDigite o código do gestor que deseja remover: ");
	int id = scanner.nextInt();
	try (Connection conexao = ConnectionFactory.getConnection();
		GestorDao gestorDao = new GestorDao(conexao))
	{
	    gestorDao.delete(id);
	    System.out.println("\nGestor removido com sucesso!");
	} catch (SQLException | NotFoundException e)
	{
	    System.err.print("\nErro ao remover gestor: \n" + e.getMessage() + "\n");
	}
    }
}