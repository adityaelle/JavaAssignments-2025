package com.aurionpro.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Banking {
	public static int currentUserId = 0;

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		while (true) {
			System.out.println("----- Welcome to Laxmi Chit Fund -----");
			System.out.println("1. Login");
			System.out.println("2. Create Account");
			System.out.println("3. Exit");

			try {
				System.out.print("Enter your choice: ");
				int initialChoice = Integer.parseInt(scanner.nextLine());

				switch (initialChoice) {
					case 1:
						if (Login.login(scanner)) {
							mainMenu(scanner);
						}
						break;
					case 2:
						CreateAccount.createAccount(scanner);
						break;
					case 3:
						System.out.println("Thank you for using our banking system.");
						System.exit(0);
					default:
						System.out.println("Invalid choice. Try again.");
				}
			} catch (NumberFormatException e) {
				System.out.println("Please enter a valid number.");
			} catch (Exception e) {
				System.out.println("An error occurred: " + e.getMessage());
			}
		}
	}

	private static void mainMenu(Scanner scanner) {
		while (true) {
			System.out.println("\n----- Banking Menu -----");
			System.out.println("1. View Balance");
			System.out.println("2. Send Money");
			System.out.println("3. View Receipts");
			System.out.println("4. Deposit Money");
			System.out.println("5. Delete Account");
			System.out.println("6. Logout");

			try {
				System.out.print("Enter your choice: ");
				int choice = Integer.parseInt(scanner.nextLine());

				switch (choice) {
					case 1:
						ViewBalance.viewBalance();
						break;
					case 2:
						SendMoney.sendMoney(scanner);
						break;
					case 3:
						ViewReceipts.viewReceipts();
						break;
					case 4:
						DepositMoney.deposit(scanner);
						break;
					case 5:
						DeleteAccount.deleteAccount(scanner);
						return; 
					case 6:
						System.out.println("You have been logged out.");
						currentUserId = 0;
						return;
					default:
						System.out.println("Invalid choice. Please try again.");
				}
			} catch (NumberFormatException e) {
				System.out.println("Please enter a valid number.");
			} catch (Exception e) {
				System.out.println("An error occurred: " + e.getMessage());
			}
		}
	}

	public static Connection getConnection() throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "root", "Aditya&adi1");
	}
}
