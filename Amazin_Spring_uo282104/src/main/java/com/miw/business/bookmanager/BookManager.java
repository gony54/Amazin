
package com.miw.business.bookmanager;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.miw.model.Book;
import com.miw.model.CartItem;
import com.miw.model.Reservation;
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
		book.setVat(this.ivaDataService.getVAT(family));
		book.setStock(10); // Set default stock for new books
		return this.bookDataService.newBook(book);
	}
	
	public void processPurchase(ShoppingCart cart) throws Exception {
		logger.debug("Processing purchase");
	    List<Book> books = bookDataService.getBooks();
	    
	    for (CartItem cartItem : cart.getItems()) {
	        int bookId = cartItem.getBook().getId();
	        int quantity = cartItem.getQuantity();
	        
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
	
	public void processReservation(Reservation reservation) throws Exception {
		logger.debug("Processing reservation");
		List<Book> books = bookDataService.getBooks();
		
		for (CartItem reservedItem : reservation.getReservedItems()) {
			int bookId = reservedItem.getBook().getId();
			int quantity = reservedItem.getQuantity();
			
			for (Book book : books) {
				if (book.getId() == bookId) {
					int newStock = book.getStock() - quantity;
					if (newStock < 0) {
						throw new Exception("Insufficient stock for book reservation: " + book.getTitle());
					}
					book.setStock(newStock);
					bookDataService.updateBook(book);
					logger.debug("Reserved " + quantity + " units of " + book.getTitle() + ". New stock: " + newStock);
					break;
				}
			}
		}
	}
	
	public void cancelReservation(int bookId, int quantity) throws Exception {
		logger.debug("Canceling reservation for book ID: " + bookId);
		List<Book> books = bookDataService.getBooks();
		
		for (Book book : books) {
			if (book.getId() == bookId) {
				int newStock = book.getStock() + quantity;
				book.setStock(newStock);
				bookDataService.updateBook(book);
				logger.debug("Cancelled reservation for " + book.getTitle() + ". Stock restored to: " + newStock);
				break;
			}
		}
	}
	
	public void convertReservationToPurchase(int bookId, int quantity) throws Exception {
		logger.debug("Converting reservation to purchase for book ID: " + bookId);
		// Stock was already reduced during reservation, so no need to reduce it again
		// This method exists for logging and potential future business logic
		List<Book> books = bookDataService.getBooks();
		
		for (Book book : books) {
			if (book.getId() == bookId) {
				logger.debug("Converted reservation to purchase for " + book.getTitle() + ". Quantity: " + quantity);
				break;
			}
		}
	}
}
