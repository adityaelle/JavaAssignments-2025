package com.aurionpro.model;

public class Invoice {
	private String buyerName;
	private Guitar guitar;
	private String date;
	private double totalAmount;

	public Invoice(String buyerName, Guitar guitar, String date, double totalAmount) {
		this.buyerName = buyerName;
		this.guitar = guitar;
		this.date = date;
		this.totalAmount = totalAmount;
	}

	public String generateText() {
		return "INVOICE\nBuyer: " + buyerName + "\nGuitar: " + guitar.getSpec().getModel() + "\nAmout:" + totalAmount
				+ "\nDate: " + date;
	}
}
