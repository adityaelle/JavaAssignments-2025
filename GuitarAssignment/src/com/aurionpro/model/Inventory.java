package com.aurionpro.model;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
	private static Inventory instance = new Inventory();
	private List<Guitar> guitars = new ArrayList<>();

	public static Inventory getInstance() {
	    return instance;
	}

    public void addGuitar(String serialNumber, double price, GuitarSpec spec) {
        guitars.add(new Guitar(serialNumber, price, spec));
    }

    public List<Guitar> search(GuitarSpec searchSpec) {
        List<Guitar> matched = new ArrayList<>();
        for (Guitar g : guitars) {
            if (g.getSpec().matches(searchSpec)) matched.add(g);
        }
        return matched;
    }
}