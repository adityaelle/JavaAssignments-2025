package com.aurionpro.model;

public class NetBanking implements IPaymentGateway {
    public void pay(double amount) {
        System.out.println("Paid Rs. " + amount + " via Net Banking.");
    }

    public void refund(double amount) {
        System.out.println("Refunded Rs. " + amount + " to Net Banking account.");
    }
}

