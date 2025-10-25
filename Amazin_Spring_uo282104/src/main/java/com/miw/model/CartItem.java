package com.miw.model;

public class CartItem {
	private Book book;
	private int quantity;
	private boolean reserved;

	public CartItem(Book book, int quantity) {
		this.book = book;
		this.quantity = quantity;
		this.reserved = false;
	}

	public CartItem(Book book, int quantity, boolean reserved) {
		this.book = book;
		this.quantity = quantity;
		this.reserved = reserved;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public boolean isReserved() {
		return reserved;
	}

	public void setReserved(boolean reserved) {
		this.reserved = reserved;
	}

	public double getSubtotal() {
		if (reserved)
			return book.getPrice() * quantity * 0.05;
		return book.getPrice() * quantity;
	}

	public double getRemainingAmount() {
		if (reserved)
			return book.getPrice() * quantity * 0.95;
		return 0;
	}
}