package com.aurionpro.model;

public class InvoiceBuilder {
	private String buyerName;
	private Guitar guitar;
	private String date;
	private double totalAmount;

	public InvoiceBuilder setBuyerName(String name) {
		this.buyerName = name;
		return this;
	}

	public InvoiceBuilder setGuitar(Guitar guitar) {
		this.guitar = guitar;
		return this;
	}

	public InvoiceBuilder setDate(String date) {
		this.date = date;
		return this;
	}

	public InvoiceBuilder amount() {
		this.totalAmount = guitar.getPrice();
		return this;
	}

	public Invoice build() {
		return new Invoice(buyerName, guitar, date, totalAmount);
	}
}
