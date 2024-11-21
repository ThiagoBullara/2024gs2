package br.com.gs2;

import br.com.gs2.exception.NotFoundException;
import br.com.gs2.view.EquipeView;
import br.com.gs2.view.GestorView;
import br.com.gs2.view.ProjetoView;
import br.com.gs2.view.RecursoView;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MainMenu
{

    public static void main(String[] args) throws NotFoundException
    {

	try (Scanner scanner = new Scanner(System.in))
	{
	    int escolha;

	    do
	    {

		try
		{

		    System.out.println("\n\n--------- Bem vindo ao SGPS / Sistema de Gerenciamento de Projetos Sustentáveis ---------\n");
		    System.out.println("\nEscolha uma opção:\n");
		    System.out.println("1 - Equipe");
		    System.out.println("2 - Gestor");
		    System.out.println("3 - Projeto");
		    System.out.println("4 - Recurso");
		    System.out.println("5 - Relatorio");
		    System.out.println("0 - Sair\n");
		    System.out.print("Opção: ");
		    escolha = scanner.nextInt();

		    switch (escolha)
		    {
			case 1 -> EquipeView.menu(scanner);
			case 2 -> GestorView.menu(scanner);
			case 3 -> ProjetoView.menu(scanner);
			case 4 -> RecursoView.menu(scanner);
			case 5 -> System.out.println("");
			case 0 ->
			    System.out.println("\nSaindo...");
			default ->
			    System.out.println("\nOpção inválida! Tente novamente.");
		    }
		} catch (InputMismatchException e)
		{
		    System.out.println("\nEntrada inválida. Por favor, insira um número.");
		    scanner.nextLine();
		    escolha = -1;
		} catch (NotFoundException e)
		{
		    System.out.println("\nErro: " + e.getMessage());
		    escolha = -1;
		}
	    } while (escolha != 0);
	}
    }
}
