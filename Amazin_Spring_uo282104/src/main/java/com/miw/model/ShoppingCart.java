package com.miw.model;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {

private Map<Integer, Integer> items; // bookId, quantity
	
	public ShoppingCart() {
		this.items = new HashMap<>();
	}
	
	public Map<Integer, Integer> getItems() {
		return items;
	}
	
	public void setItems(Map<Integer, Integer> items) {
		this.items = items;
	}
	
	public void addBook(int bookId, int quantity) {
		if (items.containsKey(bookId)) {
			items.put(bookId, items.get(bookId) + quantity);
		} else {
			items.put(bookId, quantity);
		}
	}
	
	public void removeBook(int bookId) {
		items.remove(bookId);
	}
	
	public void updateQuantity(int bookId, int quantity) {
		if (quantity <= 0) {
			removeBook(bookId);
		} else {
			items.put(bookId, quantity);
		}
	}
	
	public int getQuantity(int bookId) {
		return items.getOrDefault(bookId, 0);
	}
	
	public void clear() {
		items.clear();
	}
	
	public boolean isEmpty() {
		return items.isEmpty();
	}
	
	public int getTotalItems() {
		return items.values().stream().mapToInt(Integer::intValue).sum();
	}
}
