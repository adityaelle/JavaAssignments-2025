package com.aurionpro.model;

import java.sql.*;

public class ViewReceipts {
	public static void viewReceipts() {
		try {
			int userId = Banking.currentUserId;

			Connection connection = Banking.getConnection();
			PreparedStatement ps = connection.prepareStatement(
				"SELECT sender_id, receiver_id, amount, timestamp, transaction_type FROM receipts WHERE sender_id = ? OR receiver_id = ? ORDER BY timestamp DESC"
			);

			ps.setInt(1, userId);
			ps.setInt(2, userId);

			ResultSet rs = ps.executeQuery();

			boolean hasTransactions = false;

			System.out.println("\nYour Transaction History:");
			System.out.printf("%-20s %-15s %-12s %-10s\n", "Timestamp", "Transaction Type", "Other ID", "Amount");
			System.out.println("----------------------------------------------------------------");

			while (rs.next()) {
				hasTransactions = true;
				int sender = rs.getInt("sender_id");
				int receiver = rs.getInt("receiver_id");
				double amount = rs.getDouble("amount");
				Timestamp time = rs.getTimestamp("timestamp");
				String type = rs.getString("transaction_type");

				int otherId = (sender == userId) ? receiver : sender;

				System.out.printf("%-20s %-15s %-12d %-10.2f\n", time.toString(), type, otherId, amount);
			}

			if (!hasTransactions) {
				System.out.println("No transactions found.");
			}

			connection.close();
		} catch (Exception e) {
			System.out.println("Error viewing receipts: " + e.getMessage());
		}
	}
}
