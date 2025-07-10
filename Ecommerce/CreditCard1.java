package com.aurionpro.model;

public class CreditCard1 implements IPaymentGateway {
    public void pay(double amount) {
        System.out.println("Paid Rs. " + amount + " via Credit Card.");
    }

    public void refund(double amount) {
        System.out.println("Refunded Rs. " + amount + " to Credit Card.");
    }
}

