package com.aurionpro.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Login {

	public static boolean login(Scanner scanner) {
		try {
			System.out.print("Enter your user ID: ");
			int id = scanner.nextInt();
			scanner.nextLine(); 

			if (id <= 0) {
				System.out.println("Invalid ID. Must be positive.");
				return false;
			}

			Connection connection = Banking.getConnection();
			PreparedStatement checkId = connection.prepareStatement("SELECT name FROM users WHERE id = ?");
			checkId.setInt(1, id);
			ResultSet rsCheck = checkId.executeQuery();

			if (!rsCheck.next()) {
				System.out.println("User ID does not exist.");
				connection.close();
				return false;
			}

			System.out.print("Enter your password: ");
			String password = scanner.nextLine();

			PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE id = ? AND password = ?");
			ps.setInt(1, id);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				Banking.currentUserId = id;
				System.out.println("Login successful. Welcome, " + rs.getString("name") + "!");
				connection.close();
				return true;
			} else {
				System.out.println("Login failed. Invalid password.");
			}
			connection.close();
		} catch (Exception e) {
			System.out.println("Login error: " + e.getMessage());
		}
		return false;
	}
}
