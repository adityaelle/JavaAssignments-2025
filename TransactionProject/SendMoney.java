package com.aurionpro.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class SendMoney {
    public static void sendMoney(Scanner scanner) {
        try {
            Connection connection = Banking.getConnection();
            int senderId = Banking.currentUserId;

            System.out.print("Enter recipient User ID: ");
            int receiverId = Integer.parseInt(scanner.nextLine());

            if (receiverId == senderId) {
                System.out.println("You cannot send money to yourself.");
                return;
            }

            PreparedStatement checkUser = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
            checkUser.setInt(1, receiverId);
            ResultSet rsCheck = checkUser.executeQuery();
            if (!rsCheck.next()) {
                System.out.println("Recipient not found.");
                return;
            }

            System.out.print("Enter amount to send: ");
            double amount = Double.parseDouble(scanner.nextLine());

            if (amount <= 0) {
                System.out.println("Amount must be positive.");
                return;
            }

            PreparedStatement getBalance = connection.prepareStatement("SELECT balance FROM users WHERE id = ?");
            getBalance.setInt(1, senderId);
            ResultSet rsBal = getBalance.executeQuery();
            if (rsBal.next()) {
                double senderBalance = rsBal.getDouble("balance");
                if (senderBalance < amount) {
                    System.out.println("Insufficient balance.");
                    return;
                }

                PreparedStatement deduct = connection.prepareStatement("UPDATE users SET balance = balance - ? WHERE id = ?");
                deduct.setDouble(1, amount);
                deduct.setInt(2, senderId);
                deduct.executeUpdate();

                PreparedStatement add = connection.prepareStatement("UPDATE users SET balance = balance + ? WHERE id = ?");
                add.setDouble(1, amount);
                add.setInt(2, receiverId);
                add.executeUpdate();

                PreparedStatement receipt = connection.prepareStatement(
                    "INSERT INTO receipts (sender_id, receiver_id, amount, transaction_type) VALUES (?, ?, ?, ?)");
                receipt.setInt(1, senderId);
                receipt.setInt(2, receiverId);
                receipt.setDouble(3, amount);
                receipt.setString(4, "sent");
                receipt.executeUpdate();

                System.out.println(" " + amount + " sent to user ID " + receiverId + " successfully.");
            }

            connection.close();
        } catch (Exception e) {
            System.out.println("Error in sending money: " + e.getMessage());
        }
    }
}
