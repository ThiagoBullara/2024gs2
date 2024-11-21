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

    private static void insert(Scanner scanner) throws SQLException, NotFoundException
    {
	Equipe novaEquipe = new Equipe();
	System.out.println("Digite o nome da equipe:");
	novaEquipe.setNome(scanner.next() + scanner.nextLine());
	System.out.println("Digite a especialidade da equipe:");
	novaEquipe.setEspecialidade(scanner.next() + scanner.nextLine());
	System.out.println("Digite o email da equipe:");
	novaEquipe.setEmail(scanner.next() + scanner.nextLine());
	System.out.println("Digite uma descricao para a equipe:");
	novaEquipe.setDescricao(scanner.next() + scanner.nextLine());
	System.out.println("Digite a quantidade de funcionários na equipe:");
	novaEquipe.setQtdFuncionarios(scanner.nextInt());

	try (Connection conexao = ConnectionFactory.getConnection();
		EquipeDao equipeDao = new EquipeDao(conexao))
	{
	    int id = equipeDao.insert(novaEquipe);
	    System.out.println("Equipe cadastrada com sucesso!");
	    Equipe equipe = equipeDao.select(id);
	    System.out.println(equipe.getIdEquipe() + " - " + equipe.getNome());

	} catch (SQLException e)
	{
	    System.err.println("Erro ao cadastrar equipe: " + e.getMessage());
	}
    }

    private static void select(Scanner scanner)
    {
	System.out.println("Digite o código do equipe:");
	int id = scanner.nextInt();
	try (Connection conexao = ConnectionFactory.getConnection();
		EquipeDao equipeDao = new EquipeDao(conexao))
	{
	    Equipe equipe = equipeDao.select(id);
	    System.out.println("Equipe encontrada:");
	    System.out.println(equipe.getIdEquipe()+ " - " + equipe.getNome()
		    + ", Especialidade: " + equipe.getEspecialidade() + ", Email: " + equipe.getEmail()
		    + ", Descricao: " + equipe.getDescricao()+ ", Quantidade de Funcionários: " + equipe.getQtdFuncionarios());
	} catch (SQLException | NotFoundException e)
	{
	    System.err.println("Erro ao pesquisar equipe: " + e.getMessage());
	}
    }

    private static void search()
    {
	try (Connection conexao = ConnectionFactory.getConnection();
		EquipeDao equipeDao = new EquipeDao(conexao))
	{
	    List<Equipe> equipes = equipeDao.search();
	    System.out.println("Lista de equipes:");
	    for (Equipe equipe : equipes)
	    {
		System.out.println(equipe.getIdEquipe()+ " - " + equipe.getNome()
		    + ", Especialidade: " + equipe.getEspecialidade() + ", Email: " + equipe.getEmail()
		    + ", Descricao: " + equipe.getDescricao()+ ", Quantidade de Funcionários: " + equipe.getQtdFuncionarios());
	    }
	} catch (SQLException e)
	{
	    System.err.println("Erro ao listar equipes: " + e.getMessage());
	}
    }

    private static void update(Scanner scanner)
    {
	System.out.println("Digite o código da equipe que deseja atualizar:");
	int id = scanner.nextInt();
	try (Connection conexao = ConnectionFactory.getConnection();
		EquipeDao equipeDao = new EquipeDao(conexao))
	{
	    Equipe equipe = equipeDao.select(id);
	    System.out.println("Digite o novo nome da equipe:");
	    equipe.setNome(scanner.next() + scanner.nextLine());
	    System.out.println("Digite a nova especialidade da equipe:");
	    equipe.setEspecialidade(scanner.next() + scanner.nextLine());
	    System.out.println("Digite o novo email da equipe:");
	    equipe.setEmail(scanner.next() + scanner.nextLine());
	    System.out.println("Digite uma nova descricao para a equipe:");
	    equipe.setDescricao(scanner.next() + scanner.nextLine());
	    System.out.println("Digite a nova quantidade de funcionários na equipe:");
	    equipe.setQtdFuncionarios(scanner.nextInt());
	    equipeDao.update(equipe);
	    System.out.println("Equipe atualizada com sucesso!");
	} catch (SQLException | NotFoundException e)
	{
	    System.err.println("Erro ao atualizar equipe: " + e.getMessage());
	}
    }

    private static void delete(Scanner scanner)
    {
	System.out.println("Digite o código da equipe que deseja remover:");
	int id = scanner.nextInt();
	try (Connection conexao = ConnectionFactory.getConnection();
		EquipeDao equipeDao = new EquipeDao(conexao))
	{
	    equipeDao.delete(id);
	    System.out.println("Equipe removida com sucesso!");
	} catch (SQLException | NotFoundException e)
	{
	    System.err.println("Erro ao remover equipe: " + e.getMessage());
	}
    }
}
