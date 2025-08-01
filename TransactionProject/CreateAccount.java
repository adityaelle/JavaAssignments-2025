package com.aurionpro.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class CreateAccount {

	public static void createAccount(Scanner scanner) {
		try {
			System.out.print("Enter your name: ");
			String name = scanner.nextLine();

			System.out.print("Enter initial deposit amount: ");
			double balance = scanner.nextDouble();
			scanner.nextLine();
			
			System.out.print("Enter a password for your account: ");
			String password = scanner.nextLine();

			Connection connection = Banking.getConnection();
			PreparedStatement ps = connection.prepareStatement("INSERT INTO users (name, balance, password) VALUES (?, ?, ?)");
			ps.setString(1, name);
			ps.setDouble(2, balance);
			ps.setString(3, password);

			int rowsInserted = ps.executeUpdate();
			if (rowsInserted > 0) {
				System.out.println("Account created successfully for " + name + "!");
			} else {
				System.out.println("Account creation failed.");
			}
			connection.close();
		} catch (Exception e) {
			System.out.println("Error creating account: " + e.getMessage());
		}
	}
}
