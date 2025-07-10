package com.aurionpro.model;

public class Upi implements IPaymentGateway {
    public void pay(double amount) {
        System.out.println("Paid Rs. " + amount + " via UPI.");
    }

    public void refund(double amount) {
        System.out.println("Refunded Rs. " + amount + " to UPI.");
    }
}
