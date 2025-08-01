package com.aurionpro.model;

import java.sql.*;
import java.util.Scanner;

public class DeleteAccount {
	public static void deleteAccount(Scanner scanner) {
		try {
			int userId = Banking.currentUserId;

			System.out.print("Are you sure you want to delete your account? (yes/no): ");
			String confirm = scanner.next();

			if (!confirm.equalsIgnoreCase("yes")) {
				System.out.println("Account deletion cancelled.");
				return;
			}

			Connection connection = Banking.getConnection();

			PreparedStatement ps = connection.prepareStatement("DELETE FROM users WHERE id = ?");
			ps.setInt(1, userId);

			int rowsDeleted = ps.executeUpdate();
			if (rowsDeleted > 0) {
				System.out.println("Account deleted successfully.");
			} else {
				System.out.println("User not found.");
			}
			connection.close();
		} catch (Exception e) {
			System.out.println("Error deleting account: " + e.getMessage());
		}
	}
}
