package com.aurionpro.test;

import java.util.InputMismatchException;
import java.util.Scanner;

import com.aurionpro.model.CreditCard1;
import com.aurionpro.model.IPaymentGateway;
import com.aurionpro.model.NetBanking;
import com.aurionpro.model.Upi;

public class PaymentSystem {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		while (true) {
			IPaymentGateway payment = null;
			double totalPaid = 0.0;

			while (true) {
				try {
					System.out.println("\nChoose payment method:");
					System.out.println("1. Credit Card");
					System.out.println("2. Debit Card");
					System.out.println("3. UPI");
					System.out.println("4. Exit");

					System.out.print("Enter your choice (1–4): ");
					int choice = scanner.nextInt();

					switch (choice) {
					case 1:
						payment = new CreditCard1();
						break;
					case 2:
						payment = new NetBanking();
						break;
					case 3:
						payment = new Upi();
						break;
					case 4:
						System.out.println("Exiting the program. Thank you!");
						scanner.close();
						return;
					default:
						System.out.println("Invalid choice. Please select a valid option.");
						continue;
					}

					break;
				} catch (InputMismatchException e) {
					System.out.println("Invalid input! Please enter a number between 1 and 4.");
					scanner.nextLine(); 
				}
			}

			while (true) {
				System.out.println("\n--- Payment Menu ---");
				System.out.println("1. Make Payment");
				System.out.println("2. Refund Full Amount");
				System.out.println("3. Exit");
				System.out.println("4. Start again");
				System.out.print("Choose an option: ");

				try {
					int operation = scanner.nextInt();
					double amount = 0.0;

					switch (operation) {
					case 1:
						while (true) {
							try {
								System.out.print("Enter payment amount: ₹");
								amount = scanner.nextDouble();
								scanner.nextLine(); 

								if (amount <= 0) {
									System.out.println("Amount must be greater than 0. Try again.");
									continue;
								}

							
								while (true) {
									try {
										System.out.print("Are you sure you want to make this payment of ₹" + amount
												+ "? (yes/no): ");
										String confirm = scanner.nextLine().trim().toLowerCase();

										if (confirm.equals("yes")) {
											payment.pay(amount);
											totalPaid =totalPaid + amount;
											System.out.println("Paid: ₹" + totalPaid);
											break;
										} else if (confirm.equals("no")) {
											System.out.println(" Payment cancelled.");
											break;
										} else {
											System.out.println("Invalid input. Please type 'yes' or 'no'.");
										}
									} catch (Exception e) {
										System.out.println(" Error during confirmation: " + e);
										scanner.nextLine();
									}
								}
								break;

							} catch (InputMismatchException e) {
								System.out.println("Please enter a valid numeric amount.");
								scanner.nextLine();
							}
						}
						break;

					case 2:
						if (totalPaid <= 0) {
							System.out.println("No amount to refund.");
						} else {
							System.out.println("Refunding : ₹" + totalPaid);
							payment.refund(totalPaid);
							totalPaid = 0.0;
							System.out.println("Refund Successful");
						}
						break;

					case 3:
						System.out.println("Thank you. Exiting.");
						scanner.close();
						return;

					case 4:
						System.out.println("Restarting...\n");
						break;

					default:
						System.out.println("Invalid option. Try again.");
					}

					if (operation == 4)
						break;

				} catch (InputMismatchException e) {
					System.out.println("Invalid input. Please enter a number.");
					scanner.nextLine(); 
				}
			}
		}
	}
}
