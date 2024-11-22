package br.com.gs2.view;

import br.com.gs2.dao.ProjetoDao;
import br.com.gs2.dao.RecursoDao;
import br.com.gs2.dao.RelatorioDao;
import br.com.gs2.exception.NotFoundException;
import br.com.gs2.factory.ConnectionFactory;
import br.com.gs2.model.Financeiro;
import br.com.gs2.model.ImpactoAmbiental;
import br.com.gs2.model.Projeto;
import br.com.gs2.model.Recurso;
import br.com.gs2.model.Relatorio;
import br.com.gs2.model.Relatorio.Tipo;
import static br.com.gs2.model.Relatorio.Tipo.FINANCEIRO;
import static br.com.gs2.model.Relatorio.Tipo.IMPACTO_AMBIENTAL;
import static br.com.gs2.model.Relatorio.Tipo.TECNOLOGICO;
import br.com.gs2.model.Tecnologico;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class RelatorioView
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
		4 - Listar por tipo
		5 - Atualizar
		6 - Remover
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
		    case 4 -> search(scanner);
		    case 5 -> update(scanner);
		    case 6 -> delete(scanner);
		    case 0 -> System.out.println("\nVoltando ao menu principal...");
		    default -> System.out.println("\nOpção inválida! Tente novamente.");
		}
	    } while (escolha != 0);
	} catch (SQLException e)
	{
	    System.err.println("\nErro ao conectar ao banco de dados: \n" + e.getMessage() + "\n");
	}

    }

    private static void insert(Scanner scanner) throws NotFoundException
    {
	System.out.println("\nEscolha o tipo de relatório para cadastrar:");
	System.out.println("\n1 - Financeiro");
	System.out.println("2 - Impacto Ambiental");
	System.out.println("3 - Tecnologico");

	System.out.print("Opção: ");
	int tipo = scanner.nextInt();
	scanner.nextLine();

	Relatorio novoRelatorio;

	switch (tipo)
	{
	    case 1 ->
	    {
		Financeiro financeiro = new Financeiro();
		System.out.print("\nDigite o orcamento total: ");
		while (!scanner.hasNextDouble())
		{
		    System.out.println("\nEntrada inválida. Digite um número decimal.");
		    scanner.next();
		}
		financeiro.setOrcamentoTotal(scanner.nextDouble());
		scanner.nextLine();
		System.out.print("\nDigite as despesas: ");
		while (!scanner.hasNextDouble())
		{
		    System.out.println("\nEntrada inválida. Digite um número decimal.");
		    scanner.next();
		}
		financeiro.setOrcamentoTotal(scanner.nextDouble());
		scanner.nextLine();
		System.out.print("\nDigite o valor gerado: ");
		while (!scanner.hasNextDouble())
		{
		    System.out.println("\nEntrada inválida. Digite um número decimal.");
		    scanner.next();
		}
		financeiro.setOrcamentoTotal(scanner.nextDouble());
		scanner.nextLine();
		novoRelatorio = financeiro;
		novoRelatorio.setTipoRelatorio(Tipo.FINANCEIRO);
	    }
	    case 2 ->
	    {
		ImpactoAmbiental impactoAmbiental = new ImpactoAmbiental();
		System.out.print("\nDigite a quantidade de emissão evitada: ");
		while (!scanner.hasNextDouble())
		{
		    System.out.println("\nEntrada inválida. Digite um número decimal.");
		    scanner.next();
		}
		impactoAmbiental.setEmissaoEvitada(scanner.nextDouble());
		scanner.nextLine();
		System.out.print("\nDigite a quantidade dos recursos economizados: ");
		while (!scanner.hasNextDouble())
		{
		    System.out.println("\nEntrada inválida. Digite um número decimal.");
		    scanner.next();
		}
		impactoAmbiental.setRecursosEconomizados(scanner.nextDouble());
		scanner.nextLine();
		novoRelatorio = impactoAmbiental;
		novoRelatorio.setTipoRelatorio(Tipo.IMPACTO_AMBIENTAL);
	    }
	    case 3 ->
	    {
		Tecnologico tecnologico = new Tecnologico();
		System.out.print("\nDigite a quantidade de energia gerada: ");
		tecnologico.setQtdEnergiaGerada(scanner.nextDouble());
		scanner.nextLine();
		System.out.println("\nDigite a eficiência: ");
		tecnologico.setEficiencia(scanner.nextDouble());
		scanner.nextLine();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		tecnologico.setDataEmissao(ProjetoView.lerData(scanner, formatter, "\nDigite a data de validade do relatório no formato (dd/MM/yyyy HH:mm): "));
		novoRelatorio = tecnologico;
		novoRelatorio.setTipoRelatorio(Tipo.TECNOLOGICO);
	    }
	    default ->
	    {
		System.out.println("\nTipo inválido. Operação cancelada.");
		return;
	    }
	}

	System.out.print("\nDigite a descrição do relatório: ");
	novoRelatorio.setDescricao(scanner.next() + scanner.nextLine());
	System.out.print("\nDigite o nome do autor do relatório: ");
	novoRelatorio.setAutor(scanner.next() + scanner.nextLine());
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	novoRelatorio.setDataEmissao(ProjetoView.lerData(scanner, formatter, "\nDigite a data de emissão do relatório no formato (dd/MM/yyyy HH:mm):"));
	try (Connection conexao = ConnectionFactory.getConnection();
		ProjetoDao projetoDao = new ProjetoDao(conexao);
		RelatorioDao relatorioDao = new RelatorioDao(conexao))
	{

	    List<Projeto> projetos = projetoDao.search();

	    if (projetos.isEmpty())
	    {
		System.out.println("\nNenhum projeto encontrado. Operação de cadastro de relatório cancelada.");
		return;
	    }

	    System.out.print("\nSelecione o projeto para o relatório (ID): ");
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
		novoRelatorio.setProjeto(projetoSelecionado);
	    int idRelatorio = relatorioDao.insert(novoRelatorio);
	    System.out.println("\nRelatório cadastrado com sucesso! ID: " + idRelatorio);

	} catch (SQLException e)
	{
	    System.err.println("\nErro ao cadastrar relatório: \n" + e.getMessage() + "\n");
	}
    }

    private static void select(Scanner scanner)
    {
	System.out.print("\nDigite o código do relatório: ");
	int id = scanner.nextInt();

	try (Connection conexao = ConnectionFactory.getConnection();
		RelatorioDao relatorioDao = new RelatorioDao(conexao))
	{

	    Relatorio relatorio = relatorioDao.select(id);

	    System.out.println("Relatorio encontrado:");
	    exibirResumoRelatorio(relatorio);

	} catch (SQLException | NotFoundException e)
	{
	    System.err.println("\nErro ao pesquisar relatório: \n" + e.getMessage() + "\n");
	}
    }

    private static void search()
    {
	try (Connection conexao = ConnectionFactory.getConnection();
		RelatorioDao relatorioDao = new RelatorioDao(conexao))
	{

	    List<Relatorio> relatorios = relatorioDao.search();

	    if (relatorios.isEmpty())
	    {
		System.out.println("\nNenhum relatório encontrado.");
		return;
	    }

	    System.out.println("\nLista de relatórios:");
	    for (Relatorio relatorio : relatorios)
	    {
		exibirResumoRelatorio(relatorio);
	    }

	} catch (SQLException e)
	{
	    System.err.println("\nErro ao listar relatórios: " + e.getMessage());
	}
    }

    private static void search(Scanner scanner)
    {
	System.out.print("\nDigite o tipo de relatório (FINANCEIRO, IMPACTO_AMBIENTAL, TECNOLOGICO): ");
	String tipoRelatorio = scanner.next().toUpperCase();

	try
	{
	    Tipo tipo = Tipo.valueOf(tipoRelatorio);

	    try (Connection conexao = ConnectionFactory.getConnection();
		    RelatorioDao relatorioDao = new RelatorioDao(conexao))
	    {

		List<Relatorio> relatorios = relatorioDao.search(tipo);

		if (relatorios.isEmpty())
		{
		    System.out.println("\nNenhum relatório encontrado para o tipo: " + tipo);
		    return;
		}

		System.out.println("\nLista de relatórios do tipo: " + tipo);
		for (Relatorio relatorio : relatorios)
		{
		    exibirResumoRelatorio(relatorio);
		}

	    } catch (SQLException e)
	    {
		System.err.println("\nErro ao listar relatórios: \n" + e.getMessage() + "\n");
	    }

	} catch (IllegalArgumentException e)
	{
	    System.err.println("\nTipo de relatório inválido. Tente novamente.");
	}
    }

    private static void update(Scanner scanner) throws SQLException, NotFoundException
    {

	System.out.print("\nDigite o código do recurso que deseja atualizar: ");
	int id = scanner.nextInt();
	scanner.nextLine();

		try (Connection conexao = ConnectionFactory.getConnection();
			ProjetoDao projetoDao = new ProjetoDao(conexao);
			RelatorioDao relatorioDao = new RelatorioDao(conexao)){ 
			Relatorio relatorio = relatorioDao.select(id);
			if (relatorio == null)
			{
			System.out.println("\nRelatório não encontrado.");
			return;
			}
			System.out.println("\nEscolha o novo tipo de relatorio:");
			System.out.println("\n1 - Financeiro");
			System.out.println("2 - Impacto Ambiental");
			System.out.println("3 - Tecnologico");

			int tipo = scanner.nextInt();
			scanner.nextLine();

			switch (tipo)
			{
				case 1 ->
				{
				Financeiro financeiro = new Financeiro();
				System.out.print("\nDigite o orcamento total: ");
				while (!scanner.hasNextDouble())
				{
					System.out.println("\nEntrada inválida. Digite um número decimal.");
					scanner.next();
				}
				financeiro.setOrcamentoTotal(scanner.nextDouble());
				scanner.nextLine();

				System.out.print("\nDigite as despesas: ");
				while (!scanner.hasNextDouble())
				{
					System.out.println("\nEntrada inválida. Digite um número decimal.");
					scanner.next();
				}
				financeiro.setOrcamentoTotal(scanner.nextDouble());
				scanner.nextLine();

				System.out.print("\nDigite o valor gerado:");
				while (!scanner.hasNextDouble())
				{
					System.out.println("\nEntrada inválida. Digite um número decimal.");
					scanner.next();
				}
				financeiro.setOrcamentoTotal(scanner.nextDouble());
				scanner.nextLine();

				relatorio = financeiro;
				relatorio.setTipoRelatorio(Tipo.FINANCEIRO);
				}
				case 2 ->
				{
				ImpactoAmbiental impactoAmbiental = new ImpactoAmbiental();

				System.out.print("\nDigite a quantidade de emissão evitada: ");
				while (!scanner.hasNextDouble())
				{
					System.out.println("\nEntrada inválida. Digite um número decimal.");
					scanner.next();
				}
				impactoAmbiental.setEmissaoEvitada(scanner.nextDouble());
				scanner.nextLine();

				System.out.print("\nDigite a quantidade dos recursos economizados: ");
				while (!scanner.hasNextDouble())
				{
					System.out.println("\nEntrada inválida. Digite um número decimal.");
					scanner.next();
				}
				impactoAmbiental.setRecursosEconomizados(scanner.nextDouble());
				scanner.nextLine();

				relatorio = impactoAmbiental;
				relatorio.setTipoRelatorio(Tipo.IMPACTO_AMBIENTAL);
				}
				case 3 ->
				{
				Tecnologico tecnologico = new Tecnologico();

				System.out.print("\nDigite a quantidade de energia gerada: ");
				tecnologico.setQtdEnergiaGerada(scanner.nextDouble());
				scanner.nextLine();

				System.out.print("\nDigite a eficiência: ");
				tecnologico.setEficiencia(scanner.nextDouble());
				scanner.nextLine();

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
				tecnologico.setDataEmissao(ProjetoView.lerData(scanner, formatter, "\nDigite a data de validade do relatório no formato (dd/MM/yyyy HH:mm):"));

				relatorio = tecnologico;
				relatorio.setTipoRelatorio(Tipo.TECNOLOGICO);
				}
				default ->
				{
				System.out.println("\nTipo inválido. Operação cancelada.");
				return;
				}
			}

			System.out.print("\nDigite a nova descrição do relatório: ");
			relatorio.setDescricao(scanner.next() + scanner.nextLine());

			System.out.print("\nDigite o novo nome do autor do relatório: ");
			relatorio.setAutor(scanner.next() + scanner.nextLine());

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
			relatorio.setDataEmissao(ProjetoView.lerData(scanner, formatter, "\nDigite a nova data de emissão do relatório no formato (dd/MM/yyyy HH:mm):"));

			List<Projeto> projetos = projetoDao.search();

			if (projetos.isEmpty())
			{
			System.out.println("\nNenhum projeto encontrado. Operação de alteração de relatório cancelada.");
			return;
			}

			System.out.print("\nSelecione o projeto para o relatório (ID): ");
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
			relatorio.setProjeto(projetoSelecionado);
			relatorioDao.update(relatorio);
			System.out.println("\nRelatório alterado com sucesso!");
			
		} catch (SQLException e)
		{
			System.err.println("\nErro ao tentar alterar relatório: \n" + e.getMessage() + "\n");
		}

    }

    private static void delete(Scanner scanner)
    {
	System.out.print("\nDigite o código do relatorio que deseja remover: ");
	int id = scanner.nextInt();
	scanner.nextLine();

	try (Connection conexao = ConnectionFactory.getConnection();
		RelatorioDao relatorioDao = new RelatorioDao(conexao))
	{

	    Relatorio relatorio = relatorioDao.select(id);
	    if (relatorio == null)
	    {
		System.out.println("\nRelatorio não encontrado.");
		return;
	    }

	    System.out.print("\nTem certeza que deseja remover o relatorio ? (S/N): ");
	    String confirmacao = scanner.nextLine();
	    if (!confirmacao.equalsIgnoreCase("S"))
	    {
		System.out.println("\nOperação cancelada.");
		return;
	    }

	    relatorioDao.delete(id);
	    System.out.println("\nRelatorio removido com sucesso!");

	} catch (SQLException | NotFoundException e)
	{
	    System.err.println("\nErro ao remover relatorio: \n" + e.getMessage() + "\n");
	}
    }

    private static void exibirResumoRelatorio(Relatorio relatorio)
    {
	System.out.println("\nID: " + relatorio.getIdRelatorio());
	System.out.println("Autor: " + relatorio.getAutor());
	System.out.println("Descrição: " + relatorio.getDescricao());
	System.out.println("Tipo: " + relatorio.getTipoRelatorio());
	System.out.println("Data de emissão: " + relatorio.getDataEmissao());
	System.out.println("-------------------------------------------------------");
	switch (relatorio.getTipoRelatorio())
	    {
		case FINANCEIRO ->
		{
		    Financeiro financeiro = (Financeiro) relatorio;
		    System.out.println("\nDetalhes Financeiros:");
		    System.out.println("  Orçamento Total: " + financeiro.getOrcamentoTotal());
		    System.out.println("  Despesas: " + financeiro.getDespesas());
		    System.out.println("  Valor gerado: " + financeiro.getValorGerado());
		}
		case IMPACTO_AMBIENTAL ->
		{
		    ImpactoAmbiental impactoAmbiental = (ImpactoAmbiental) relatorio;
		    System.out.println("\nDetalhes Impacto Ambiental:");
		    System.out.println("  Emissão evitada: " + impactoAmbiental.getEmissaoEvitada());
		    System.out.println("  Recursos Economizados: " + impactoAmbiental.getRecursosEconomizados());
		}
		case TECNOLOGICO ->
		{
		    Tecnologico tecnologico = (Tecnologico) relatorio;
		    System.out.println("\nDetalhes Tecnologicos:");
		    System.out.println("  Quantidade de energia gerada: " + tecnologico.getQtdEnergiaGerada());
		    System.out.println("  Eficiência: " + tecnologico.getEficiencia());
		    System.out.println("  Validade: " + tecnologico.getValidade());
		}
	    }
	    Projeto projeto = relatorio.getProjeto();
	    System.out.println("\nProjeto associado:");
	    System.out.println("  ID: " + projeto.getIdProjeto());
	    System.out.println("  Nome: " + projeto.getNome());
    }

}