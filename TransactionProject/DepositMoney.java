package com.aurionpro.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class DepositMoney {
    public static void deposit(Scanner scanner) {
        try {
            Connection connection = Banking.getConnection();
            int userId = Banking.currentUserId;

            System.out.print("Enter amount to deposit: ₹");
            double amount = Double.parseDouble(scanner.nextLine());

            if (amount <= 0) {
                System.out.println("Amount must be positive.");
                return;
            }

            
            PreparedStatement ps = connection.prepareStatement("UPDATE users SET balance = balance + ? WHERE id = ?");
            ps.setDouble(1, amount);
            ps.setInt(2, userId);
            ps.executeUpdate();

            PreparedStatement receipt = connection.prepareStatement(
                "INSERT INTO receipts (sender_id, receiver_id, amount, transaction_type) VALUES (?, ?, ?, ?)");
            receipt.setInt(1, 0); 
            receipt.setInt(2, userId);
            receipt.setDouble(3, amount);
            receipt.setString(4, "deposit");
            receipt.executeUpdate();

            System.out.println(" " + amount + " deposited successfully.");
            connection.close();
        } catch (Exception e) {
            System.out.println("Error in deposit: " + e.getMessage());
        }
    }
}
