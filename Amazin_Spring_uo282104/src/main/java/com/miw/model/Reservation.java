package com.miw.model;

import java.util.ArrayList;
import java.util.List;

public class Reservation {
	private List<CartItem> reservedItems;

	public Reservation() {
		this.reservedItems = new ArrayList<>();
	}

	public List<CartItem> getReservedItems() {
		return reservedItems;
	}

	public void setReservedItems(List<CartItem> reservedItems) {
		this.reservedItems = reservedItems;
	}

	public void addReservation(Book book, int quantity) {
		CartItem existingItem = null;

		for (CartItem item : reservedItems) {
			if (item.getBook().getId() == book.getId()) {
				existingItem = item;
				break;
			}
		}

		if (existingItem != null) {
			existingItem.setQuantity(existingItem.getQuantity() + quantity);
		} else {
			reservedItems.add(new CartItem(book, quantity, true));
		}
	}

	public void removeReservation(int bookId) {
		CartItem itemToRemove = null;

		for (CartItem item : reservedItems) {
			if (item.getBook().getId() == bookId) {
				itemToRemove = item;
				break;
			}
		}

		if (itemToRemove != null) {
			reservedItems.remove(itemToRemove);
		}
	}

	public CartItem getReservation(int bookId) {
		for (CartItem item : reservedItems) {
			if (item.getBook().getId() == bookId) {
				return item;
			}
		}
		return null;
	}

	public void clear() {
		reservedItems.clear();
	}

	public boolean isEmpty() {
		return reservedItems.isEmpty();
	}

	public int getTotalReservedItems() {
		int total = 0;
		for (CartItem item : reservedItems) {
			total += item.getQuantity();
		}
		return total;
	}

	public double getTotalReservationCost() {
		double total = 0.0;
		for (CartItem item : reservedItems) {
			total += item.getSubtotal();
		}
		return total;
	}
}