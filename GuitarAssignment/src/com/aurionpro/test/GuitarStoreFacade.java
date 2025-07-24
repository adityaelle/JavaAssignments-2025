package com.aurionpro.test;

import java.util.List;
import java.util.Scanner;

import com.aurionpro.model.Builder;
import com.aurionpro.model.Guitar;
import com.aurionpro.model.GuitarSpec;
import com.aurionpro.model.Inventory;
import com.aurionpro.model.Invoice;
import com.aurionpro.model.InvoiceBuilder;
import com.aurionpro.model.Type;
import com.aurionpro.model.Wood;

public class GuitarStoreFacade {
	private Scanner scanner = new Scanner(System.in);
	private Inventory inventory = Inventory.getInstance();

	public void start() {
		while (true) {
			initializeInventory();

			System.out.println("--- Welcome to Guitar Shop ---");

			String userName;
			while (true) {
				System.out.println("Please enter your name:");
				userName = scanner.nextLine().trim();
				if (userName.isEmpty()) {
					System.out.println("Name cannot be empty. Please try again.\n");
					continue;
				}
				if (!userName.matches("[a-zA-Z]+")) {
					System.out.println("Name must contain only letters. Please try again.\n");
					continue;
				}
				break;
			}

			System.out.println("\nEnter search criteria (press Enter to skip):");

			Builder builder = chooseEnum(Builder.class, "Builder");
			System.out.print("Model: ");
			String model = emptyToNull(scanner.nextLine());

			Type type = chooseEnum(Type.class, "Type");
			Wood backWood = chooseEnum(Wood.class, "Back Wood");
			Wood topWood = chooseEnum(Wood.class, "Top Wood");

			GuitarSpec userSpec = new GuitarSpec(builder, model, type, backWood, topWood);
			List<Guitar> results = inventory.search(userSpec);

			if (results.isEmpty()) {
				System.out.println("No guitars match your search.");
			} else {
				System.out.println("\nMatching Guitars:");
				for (int i = 0; i < results.size(); i++) {
					Guitar g = results.get(i);
					System.out.println((i + 1) + ". " + g.getSpec().getBuilder() + " " + g.getSpec().getModel() + " ("
							+ g.getSpec().getTopWood() + " wood) - " + g.getPrice() + " [Serial: "
							+ g.getSerialNumber() + "]");

				}

				int choice;
				while (true) {
					System.out.println("\nEnter the number of the guitar you want to buy:");
					String input = scanner.nextLine();
					try {
						choice = Integer.parseInt(input);
						if (choice >= 1 && choice <= results.size()) {
							break;
						} else {
							System.out.println("Invalid choice. Please try again.\n");
						}
					} catch (NumberFormatException e) {
						System.out.println("Invalid input. Please enter a valid number.\n");
					}
				}

				Guitar selectedGuitar = results.get(choice - 1);
				Invoice invoice = new InvoiceBuilder().setBuyerName(userName).setGuitar(selectedGuitar)
						.setDate(java.time.LocalDate.now().toString()).amount().build();

				System.out.println("\n---- INVOICE ----");
				System.out.println(invoice.generateText());

				System.out.println("\nWould you like to start a new search? (yes/no):");
				String again = scanner.nextLine().trim().toLowerCase();
				if (!again.equals("yes")) {
					System.out.println("Thank you for visiting the Guitar Store!");
					break;
				}
			}
		}
	}

	private <T extends Enum<T>> T chooseEnum(Class<T> enumClass, String prompt) {
		T[] values = enumClass.getEnumConstants();
		while (true) {
			System.out.println("Choose " + prompt + ":");
			for (int i = 0; i < values.length; i++) {
				System.out.println((i + 1) + ". " + values[i]);
			}
			System.out.print("Enter choice (or press Enter to skip): ");
			String input = scanner.nextLine().trim();

			if (input.isEmpty())
				return null;

			try {
				int index = Integer.parseInt(input);
				if (index >= 1 && index <= values.length) {
					return values[index - 1];
				} else {
					System.out.println("Invalid number. Try again.");
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Enter a number.\n");
			}
		}
	}

	private String emptyToNull(String input) {
		input = input.trim();
		if (input.isEmpty()) {
			return null;
		}
		return input;
	}

	private void initializeInventory() {
		{
			if (!inventory.search(new GuitarSpec(null, null, null, null, null)).isEmpty()) {
				return;
			}
			inventory.addGuitar("SN001", 1200,
					new GuitarSpec(Builder.FENDER, "Stratocaster", Type.ELECTRIC, Wood.ALDER, Wood.MAPLE));
			inventory.addGuitar("SN002", 1500,
					new GuitarSpec(Builder.GIBSON, "Les Paul", Type.ELECTRIC, Wood.MAHOGANY, Wood.CEDAR));
			inventory.addGuitar("SN003", 1000,
					new GuitarSpec(Builder.MARTIN, "D-28", Type.ACOUSTIC, Wood.MAHOGANY, Wood.SITKA));
			inventory.addGuitar("SN004", 1300,
					new GuitarSpec(Builder.FENDER, "Stratocaster", Type.ELECTRIC, Wood.ALDER, Wood.MAPLE));
			inventory.addGuitar("SN005", 1600,
					new GuitarSpec(Builder.FENDER, "D-28", Type.ACOUSTIC, Wood.COCOBOLO, Wood.BRAZILIAN_ROSEWOOD));
		}
	}
}