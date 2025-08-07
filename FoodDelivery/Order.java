package com.aurionpro.model;

import java.sql.*;
import java.util.*;

public class Order {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/food_ordering_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Aditya&adi1";
    private Map<MenuItem, Integer> cart;
    private double totalAmount;

    public Order() {
        cart = new LinkedHashMap<>();
        totalAmount = 0;
    }

    public void addItem(MenuItem item, int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be positive!");
        }
        cart.put(item, cart.getOrDefault(item, 0) + qty);
        recalculateTotal();
        System.out.println(item.getName() + " added to cart.");
    }

    public void removeItem(int index) {
        if (index < 0 || index >= cart.size()) {
            System.out.println("Invalid item number!");
            return;
        }
        MenuItem item = new ArrayList<>(cart.keySet()).get(index);
        int quantity = cart.get(item);
        if (quantity > 1) {
            cart.put(item, quantity - 1);
            System.out.println("Removed one quantity of " + item.getName() + ". Remaining: " + (quantity - 1));
        } else {
            cart.remove(item);
            System.out.println(item.getName() + " removed from the cart.");
        }
        recalculateTotal();
    }

    public void viewCart() {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty!");
            return;
        }
        System.out.println("\n--- Your Cart ---");
        int index = 1;
        for (Map.Entry<MenuItem, Integer> entry : cart.entrySet()) {
            System.out.println(index + ". " + entry.getKey().getName() + " x " + entry.getValue() +
                    " = " + (entry.getKey().getPrice() * entry.getValue()));
            index++;
        }
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public boolean isEmpty() {
        return cart.isEmpty();
    }

    public Map<MenuItem, Integer> getItems() {
        return cart;
    }

    public int saveOrder(double discountedTotal, PaymentMode paymentMode, String deliveryPartnerName) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false);

            try {
                Integer deliveryPartnerId = null;

                if (deliveryPartnerName != null && !deliveryPartnerName.isEmpty()) {
                    try (PreparedStatement partnerStmt = conn.prepareStatement(
                            "SELECT partner_id FROM delivery_partners WHERE partner_name = ? AND is_active = TRUE")) {
                        partnerStmt.setString(1, deliveryPartnerName);
                        try (ResultSet rs = partnerStmt.executeQuery()) {
                            if (rs.next()) {
                                deliveryPartnerId = rs.getInt("partner_id");
                            } else {
                                throw new SQLException("Delivery partner not found or inactive: " + deliveryPartnerName);
                            }
                        }
                    }
                }

                int orderId;
                try (PreparedStatement orderStmt = conn.prepareStatement(
                        "INSERT INTO orders (total_amount, discounted_amount, payment_mode, delivery_partner_id, order_status) " +
                                "VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    orderStmt.setDouble(1, totalAmount);
                    orderStmt.setDouble(2, discountedTotal);
                    orderStmt.setString(3, paymentMode.name());
                    if (deliveryPartnerId != null) {
                        orderStmt.setInt(4, deliveryPartnerId);
                    } else {
                        orderStmt.setNull(4, Types.INTEGER);
                    }
                    orderStmt.setString(5, "CONFIRMED");
                    orderStmt.executeUpdate();

                    try (ResultSet rs = orderStmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            orderId = rs.getInt(1);
                        } else {
                            throw new SQLException("Failed to retrieve generated order ID.");
                        }
                    }
                }

                try (PreparedStatement itemStmt = conn.prepareStatement(
                        "INSERT INTO order_items (order_id, item_id, quantity) VALUES (?, ?, ?)")) {
                    for (Map.Entry<MenuItem, Integer> entry : cart.entrySet()) {
                        itemStmt.setInt(1, orderId);
                        itemStmt.setInt(2, entry.getKey().getItemId());
                        itemStmt.setInt(3, entry.getValue());
                        itemStmt.addBatch();
                    }
                    itemStmt.executeBatch();
                }

                conn.commit();
                return orderId;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private void recalculateTotal() {
        totalAmount = 0;
        for (Map.Entry<MenuItem, Integer> entry : cart.entrySet()) {
            totalAmount += entry.getKey().getPrice() * entry.getValue();
        }
    }
}
