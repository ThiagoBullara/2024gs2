package br.com.gs2;

import br.com.gs2.exception.NotFoundException;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MainMenu {
    public static void main(String[] args){
	
        try (Scanner scanner = new Scanner(System.in)){
	    int escolha;

	    do{
		
            try{
			
                System.out.println("\n\n--------- Bem vindo ao SGPS / Sistema de Gerenciamento de Projéteis Sustentáveis ---------\n");
                System.out.println("\nEscolha uma opção:\n");
                System.out.println("1 - OP 1");
                System.out.println("2 - OP 2");
                System.out.println("3 - OP 3");
                System.out.println("4 - OP 4");	    
                System.out.println("5 - Op 5");
                System.out.println("0 - Sair\n");
                System.out.print("Opção: ");
                escolha = scanner.nextInt();
                
                switch (escolha){
			        case 1 -> ;
			        case 2 -> ;
                    case 3 -> ;
                    case 4 -> ;
                    case 5 -> ;
                    case 0 ->
                        System.out.println("\nSaindo...");
                    default ->
                        System.out.println("\nOpção inválida! Tente novamente.");
                }
		    } catch (InputMismatchException e){
		        System.out.println("\nEntrada inválida. Por favor, insira um número.");
		        scanner.nextLine();
		        escolha = -1;
		    } catch (NotFoundException e){
		        System.out.println("\nErro: " + e.getMessage());
		        escolha = -1;
		    }
	    } while (escolha != 0);
	    }
    }
}