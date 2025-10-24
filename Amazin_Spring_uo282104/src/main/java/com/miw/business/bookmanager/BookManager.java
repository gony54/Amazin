
package com.miw.business.bookmanager;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.*;

import com.miw.model.Book;
import com.miw.model.ShoppingCart;
import com.miw.persistence.book.BookDataService;
import com.miw.persistence.vat.VATDataService;

public class BookManager implements BookManagerService {
	Logger logger = LogManager.getLogger(this.getClass());
	private BookDataService bookDataService= null;
	private VATDataService ivaDataService = null;
	private Map<Integer, Integer> discounts = null;

	public Map<Integer, Integer> getDiscounts() {
		return discounts;
	}

	public void setDiscounts(Map<Integer, Integer> discounts) {
		this.discounts = discounts;
	}

	public BookDataService getBookDataService() {
		return bookDataService;
	}

	public void setBookDataService(BookDataService bookDataService) {
		this.bookDataService = bookDataService;
	}

	public VATDataService getIvaDataService() {
		return ivaDataService;
	}

	public void setIvaDataService(VATDataService ivaDataService) {
		this.ivaDataService = ivaDataService;
	}

	public List<Book> getBooks() throws Exception {
		logger.debug("Asking for books");
		List<Book> books = bookDataService.getBooks();
		
		// We calculate the final price with the VAT value
		for (Book b : books) {
			b.setPrice(b.getBasePrice() * (1 + b.getVat().getValue() )* discounts.get(b.getVat().getTaxGroup()));
		}
		return books;
	}
	
	public Book getSpecialOffer() throws Exception
	{
		List<Book> books = bookDataService.getBooks();
		int number = (int) (Math.random()*books.size());
		logger.debug("Applying disccount to "+books.get(number).getTitle());
		books.get(number).setPrice((double)books.get(number).getBasePrice()*0.85);
		return books.get(number);
	}
	
	public Book newBook(Book book, int family) throws Exception {
		// TODO Auto-generated method stub
		book.setVat(this.ivaDataService.getVAT(family));
		book.setStock(10); // Set default stock for new books
		return this.bookDataService.newBook(book);
	}
	
	public void processPurchase(ShoppingCart cart) throws Exception {
		logger.debug("Processing purchase");
		List<Book> books = bookDataService.getBooks();
		
		// Update stock for each book in the cart
		for (Map.Entry<Integer, Integer> entry : cart.getItems().entrySet()) {
			int bookId = entry.getKey();
			int quantity = entry.getValue();
			
			for (Book book : books) {
				if (book.getId() == bookId) {
					int newStock = book.getStock() - quantity;
					if (newStock < 0) {
						throw new Exception("Insufficient stock for book: " + book.getTitle());
					}
					book.setStock(newStock);
					bookDataService.updateBook(book);
					logger.debug("Updated stock for " + book.getTitle() + ". New stock: " + newStock);
					break;
				}
			}
		}
	}
}
