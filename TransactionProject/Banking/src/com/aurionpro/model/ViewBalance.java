package com.aurionpro.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewBalance {
	public static void viewBalance() {
		try {
			int userId = Banking.currentUserId;

			Connection connection = Banking.getConnection();
			PreparedStatement ps = connection.prepareStatement("SELECT balance FROM users WHERE id = ?");
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				double balance = rs.getDouble("balance");
				System.out.println("Your current balance is: " + balance);
			} else {
				System.out.println("User not found.");
			}
			connection.close();
		} catch (Exception e) {
			System.out.println("Error viewing balance: " + e.getMessage());
		}
	}
}
