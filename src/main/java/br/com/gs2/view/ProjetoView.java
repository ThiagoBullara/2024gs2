package br.com.gs2.view;

import br.com.gs2.dao.EquipeDao;
import br.com.gs2.dao.GestorDao;
import br.com.gs2.dao.ProjetoDao;
import br.com.gs2.exception.NotFoundException;
import br.com.gs2.factory.ConnectionFactory;
import br.com.gs2.model.Equipe;
import br.com.gs2.model.Gestor;
import br.com.gs2.model.Projeto;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ProjetoView
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
	Projeto novoProjeto = new Projeto();
	System.out.println("Digite o nome do projeto:");
	novoProjeto.setNome(scanner.next() + scanner.nextLine());
	System.out.println("Digite o tipo do projeto:");
	novoProjeto.setTipo(scanner.next() + scanner.nextLine());
	System.out.println("Digite uma descrição para o projeto:");
	novoProjeto.setDescricao(scanner.next() + scanner.nextLine());
	System.out.println("Digite o status do projeto:");
	novoProjeto.setStatus(scanner.next() + scanner.nextLine());
	System.out.println("Digite a localização do projeto:");
	novoProjeto.setLocalizacao(scanner.next() + scanner.nextLine());
	System.out.println("Digite a duração do projeto (em dias):");
	while (!scanner.hasNextInt())
	{
	    System.out.println("Entrada inválida. Digite um número inteiro.");
	    scanner.next();
	}
	novoProjeto.setDuracao(scanner.nextInt());
	scanner.nextLine();

	System.out.println("Digite o orçamento do projeto:");
	while (!scanner.hasNextDouble())
	{
	    System.out.println("Entrada inválida. Digite um número decimal.");
	    scanner.next();
	}
	novoProjeto.setOrcamento(scanner.nextDouble());
	scanner.nextLine();

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	novoProjeto.setDataInicio(lerData(scanner, formatter, "Digite a data de início do projeto (dd/MM/yyyy HH:mm):"));
	novoProjeto.setDataTermino(lerData(scanner, formatter, "Digite a data de término do projeto (dd/MM/yyyy HH:mm):"));

	try (Connection conexao = ConnectionFactory.getConnection();
		EquipeDao equipeDao = new EquipeDao(conexao);
		GestorDao gestorDao = new GestorDao(conexao);
		ProjetoDao projetoDao = new ProjetoDao(conexao))
	{

	    boolean equipeValida = false;
	    while (!equipeValida)
	    {
		List<Equipe> equipes = equipeDao.search();
		if (equipes.isEmpty())
		{
		    System.out.println("Nenhuma equipe encontrada. Deseja criar uma nova equipe? (S/N)");
		    String resposta = scanner.nextLine();
		    if (resposta.equalsIgnoreCase("S"))
		    {
			Equipe novaEquipe = new Equipe();
			System.out.println("Digite o nome da nova equipe:");
			novaEquipe.setNome(scanner.next() + scanner.nextLine());
			System.out.println("Digite a especialidade da equipe:");
			novaEquipe.setEspecialidade(scanner.next() + scanner.nextLine());
			System.out.println("Digite o email da equipe:");
			novaEquipe.setEmail(scanner.next() + scanner.nextLine());
			System.out.println("Digite uma descrição para a equipe:");
			novaEquipe.setDescricao(scanner.next() + scanner.nextLine());
			System.out.println("Digite a quantidade de funcionários na equipe:");
			novaEquipe.setQtdFuncionarios(scanner.nextInt());

			int equipeId = equipeDao.insert(novaEquipe);
			novaEquipe.setIdEquipe(equipeId);
			novoProjeto.setEquipe(novaEquipe);
			equipeValida = true;
		    } else if (resposta.equalsIgnoreCase("N"))
		    {
			System.out.println("Operação cancelada. Não é possível cadastrar um projeto sem uma equipe.");
			return;
		    } else
		    {
			System.out.println("Resposta inválida. Por favor, responda com 'S' ou 'N'.");
		    }
		} else
		{
		    System.out.println("Selecione a equipe para o projeto (ID):");
		    for (Equipe equipe : equipes)
		    {
			System.out.println("ID: " + equipe.getIdEquipe() + " - Nome: " + equipe.getNome());
		    }

		    while (!scanner.hasNextInt())
		    {
			System.out.println("Entrada inválida. Digite um número inteiro.");
			scanner.next();
		    }
		    int equipeId = scanner.nextInt();
		    scanner.nextLine();

		    Equipe equipeSelecionada = equipes.stream()
			    .filter(equipe -> equipe.getIdEquipe() == equipeId)
			    .findFirst()
			    .orElse(null);

		    if (equipeSelecionada != null)
		    {
			novoProjeto.setEquipe(equipeSelecionada);
			equipeValida = true;
		    } else
		    {
			System.out.println("ID da equipe inválido. Tente novamente.");
		    }
		}
	    }

	    boolean gestorValido = false;
	    while (!gestorValido)
	    {
		try
		{
		    List<Gestor> gestores = gestorDao.search();
		    if (gestores.isEmpty())
		    {
			System.out.println("Nenhum gestor encontrado. Deseja criar um novo gestor? (S/N)");
			String resposta = scanner.next();
			if (resposta.equalsIgnoreCase("S"))
			{
			    Gestor novoGestor = new Gestor();
			    System.out.println("Digite o nome do gestor:");
			    novoGestor.setNome(scanner.next() + scanner.nextLine());
			    System.out.println("Digite o email do gestor:");
			    novoGestor.setEmail(scanner.next() + scanner.nextLine());
			    System.out.println("Digite o telefone do gestor:");
			    novoGestor.setTelefone(scanner.next() + scanner.nextLine());
			    System.out.println("Digite uma descrição para o gestor:");
			    novoGestor.setDescricao(scanner.next() + scanner.nextLine());

			    int gestorId = gestorDao.insert(novoGestor);
			    novoGestor.setIdGestor(gestorId);
			    novoProjeto.setGestor(novoGestor);
			    gestorValido = true;
			} else if (resposta.equalsIgnoreCase("N"))
			{
			    System.out.println("Operação cancelada. Não é possível cadastrar um projeto sem um gestor.");
			    return;
			} else
			{
			    System.out.println("Entrada inválida. Por favor, responda com 'S' para sim ou 'N' para não.");
			}
		    } else
		    {
			System.out.println("Selecione o gestor para o projeto (ID):");
			for (Gestor gestor : gestores)
			{
			    System.out.println("ID: " + gestor.getIdGestor() + " - Nome: " + gestor.getNome());
			}
			int gestorID = scanner.nextInt();
			Gestor gestorSelecionado = gestores.stream()
				.filter(gestor -> gestor.getIdGestor() == gestorID)
				.findFirst()
				.orElse(null);

			if (gestorSelecionado != null)
			{
			    novoProjeto.setGestor(gestorSelecionado);
			    gestorValido = true;
			} else
			{
			    System.out.println("ID do gestor inválido. Tente novamente.");
			}
		    }
		} catch (InputMismatchException e)
		{
		    System.out.println("Entrada inválida. Por favor, insira um número.");
		    scanner.next();
		} catch (SQLException e)
		{
		    System.err.println("Erro ao buscar gestores: " + e.getMessage());
		}
	    }

	    int idProjeto = projetoDao.insert(novoProjeto);
	    System.out.println("Projeto cadastrado com sucesso!");
	    Projeto projeto = projetoDao.select(idProjeto);
	    System.out.println(projeto.getIdProjeto() + " - " + projeto.getNome());
	} catch (SQLException e)
	{
	    System.err.println("Erro ao cadastrar projeto: " + e.getMessage());
	}
    }

    private static void select(Scanner scanner)
    {
	System.out.println("Digite o código do projeto:");
	int id = scanner.nextInt();

	try (Connection conexao = ConnectionFactory.getConnection();
		ProjetoDao projetoDao = new ProjetoDao(conexao))
	{

	    Projeto projeto = projetoDao.select(id);

	    System.out.println("Projeto encontrado:");
	    System.out.println("ID: " + projeto.getIdProjeto());
	    System.out.println("Nome: " + projeto.getNome());
	    System.out.println("Tipo: " + projeto.getTipo());
	    System.out.println("Descrição: " + projeto.getDescricao());
	    System.out.println("Status: " + projeto.getStatus());
	    System.out.println("Localização: " + projeto.getLocalizacao());
	    System.out.println("Duração: " + projeto.getDuracao() + " meses");
	    System.out.println("Orçamento: R$ " + projeto.getOrcamento());
	    System.out.println("Data de Início: " + projeto.getDataInicio());
	    System.out.println("Data de Término: " + projeto.getDataTermino());

	    if (projeto.getEquipe() != null)
	    {
		Equipe equipe = projeto.getEquipe();
		System.out.println("Equipe associada:");
		System.out.println("  ID: " + equipe.getIdEquipe());
		System.out.println("  Nome: " + equipe.getNome());
		System.out.println("  Especialidade: " + equipe.getEspecialidade());
		System.out.println("  Email: " + equipe.getEmail());
	    } else
	    {
		System.out.println("Nenhuma equipe associada.");
	    }

	    if (projeto.getGestor() != null)
	    {
		Gestor gestor = projeto.getGestor();
		System.out.println("Gestor associado:");
		System.out.println("  ID: " + gestor.getIdGestor());
		System.out.println("  Nome: " + gestor.getNome());
		System.out.println("  Email: " + gestor.getEmail());
		System.out.println("  Telefone: " + gestor.getTelefone());
	    } else
	    {
		System.out.println("Nenhum gestor associado.");
	    }

	} catch (SQLException | NotFoundException e)
	{
	    System.err.println("Erro ao pesquisar projeto: " + e.getMessage());
	}
    }

    private static void search()
    {
	try (Connection conexao = ConnectionFactory.getConnection();
		ProjetoDao projetoDao = new ProjetoDao(conexao))
	{

	    List<Projeto> projetos = projetoDao.search();

	    if (projetos.isEmpty())
	    {
		System.out.println("Nenhum projeto encontrado.");
		return;
	    }

	    System.out.println("Lista de projetos:");
	    for (Projeto projeto : projetos)
	    {
		System.out.println("ID: " + projeto.getIdProjeto()
			+ " | Nome: " + projeto.getNome()
			+ " | Status: " + projeto.getStatus()
			+ " | Data de Início: " + projeto.getDataInicio());
	    }

	} catch (SQLException e)
	{
	    System.err.println("Erro ao listar projetos: " + e.getMessage());
	}
    }

    private static void update(Scanner scanner)
    {
	System.out.println("Digite o código do projeto que deseja atualizar:");
	int id = scanner.nextInt();
	scanner.nextLine();

	try (Connection conexao = ConnectionFactory.getConnection();
		EquipeDao equipeDao = new EquipeDao(conexao);
		GestorDao gestorDao = new GestorDao(conexao);
		ProjetoDao projetoDao = new ProjetoDao(conexao))
	{

	    Projeto projeto = projetoDao.select(id);
	    if (projeto == null)
	    {
		System.out.println("Projeto não encontrado.");
		return;
	    }

	    System.out.println("Digite o novo nome do projeto:");
	    projeto.setNome(scanner.next() + scanner.nextLine());
	    System.out.println("Digite o novo tipo do projeto:");
	    projeto.setTipo(scanner.next() + scanner.nextLine());
	    System.out.println("Digite uma nova descrição para o projeto:");
	    projeto.setDescricao(scanner.next() + scanner.nextLine());
	    System.out.println("Digite o novo status do projeto:");
	    projeto.setStatus(scanner.next() + scanner.nextLine());
	    System.out.println("Digite a nova localização do projeto:");
	    projeto.setLocalizacao(scanner.next() + scanner.nextLine());
	    System.out.println("Digite a nova duração do projeto (em dias):");
	    while (!scanner.hasNextInt())
	    {
		System.out.println("Entrada inválida. Digite um número inteiro.");
		scanner.next();
	    }
	    projeto.setDuracao(scanner.nextInt());
	    scanner.nextLine();

	    System.out.println("Digite o novo orçamento do projeto:");
	    while (!scanner.hasNextDouble())
	    {
		System.out.println("Entrada inválida. Digite um número decimal.");
		scanner.next();
	    }
	    projeto.setOrcamento(scanner.nextDouble());
	    scanner.nextLine();

	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	    projeto.setDataInicio(lerData(scanner, formatter, "Digite a nova data de início do projeto (dd/MM/yyyy HH:mm):"));
	    projeto.setDataTermino(lerData(scanner, formatter, "Digite a nova data de término do projeto (dd/MM/yyyy HH:mm):"));

	    System.out.println("Deseja alterar o gestor do projeto? (S/N)");
	    String alterarGestor = scanner.nextLine();
	    if (alterarGestor.equalsIgnoreCase("S"))
	    {
		System.out.println("Lista de gestores disponíveis:");
		List<Gestor> gestores = gestorDao.search();
		for (Gestor gestor : gestores)
		{
		    System.out.println("ID: " + gestor.getIdGestor() + " - " + gestor.getNome());
		}

		System.out.println("Digite o ID do novo gestor:");
		int idGestor = scanner.nextInt();
		scanner.nextLine();
		Gestor gestor = gestorDao.select(idGestor);
		if (gestor == null)
		{
		    System.out.println("Gestor não encontrado. Operação cancelada.");
		    return;
		}
		projeto.setGestor(gestor);
	    }

	    System.out.println("Deseja alterar a equipe do projeto? (S/N)");
	    String alterarEquipe = scanner.nextLine();
	    if (alterarEquipe.equalsIgnoreCase("S"))
	    {
		System.out.println("Lista de equipes disponíveis:");
		List<Equipe> equipes = equipeDao.search();
		for (Equipe equipe : equipes)
		{
		    System.out.println("ID: " + equipe.getIdEquipe() + " - " + equipe.getNome());
		}

		System.out.println("Digite o ID da nova equipe:");
		int idEquipe = scanner.nextInt();
		scanner.nextLine(); 
		Equipe equipe = equipeDao.select(idEquipe);
		if (equipe == null)
		{
		    System.out.println("Equipe não encontrada. Operação cancelada.");
		    return;
		}
		projeto.setEquipe(equipe);
	    }

	    projetoDao.update(projeto);
	    System.out.println("Projeto atualizado com sucesso!");

	} catch (SQLException | NotFoundException e)
	{
	    System.err.println("Erro ao atualizar projeto: " + e.getMessage());
	}
    }

    private static void delete(Scanner scanner)
    {
	System.out.println("Digite o código do projeto que deseja remover:");
	int id = scanner.nextInt();
	scanner.nextLine();

	try (Connection conexao = ConnectionFactory.getConnection();
		ProjetoDao projetoDao = new ProjetoDao(conexao))
	{

	    Projeto projeto = projetoDao.select(id);
	    if (projeto == null)
	    {
		System.out.println("Projeto não encontrado.");
		return;
	    }

	    System.out.println("Tem certeza que deseja remover o projeto " + projeto.getNome() + "? (S/N)");
	    String confirmacao = scanner.nextLine();
	    if (!confirmacao.equalsIgnoreCase("S"))
	    {
		System.out.println("Operação cancelada.");
		return;
	    }

	    projetoDao.delete(id);
	    System.out.println("Projeto removido com sucesso!");

	} catch (SQLException | NotFoundException e)
	{
	    System.err.println("Erro ao remover projeto: " + e.getMessage());
	}
    }

    private static LocalDateTime lerData(Scanner scanner, DateTimeFormatter formatter, String mensagem)
    {
	LocalDateTime data = null;
	while (data == null)
	{
	    System.out.println(mensagem);
	    String input = scanner.nextLine();
	    try
	    {
		data = LocalDateTime.parse(input, formatter);
	    } catch (DateTimeParseException e)
	    {
		System.out.println("Formato inválido. Tente novamente.");
	    }
	}
	return data;
    }

}
