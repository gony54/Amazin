package com.miw.business.bookmanager;

import java.util.List;

import com.miw.model.Book;
import com.miw.model.ShoppingCart;

public interface BookManagerService {
	public List<Book> getBooks() throws Exception;
	public Book getSpecialOffer() throws Exception;
	public Book newBook(Book book, int family) throws Exception;
	public void processPurchase(ShoppingCart cart) throws Exception;
}