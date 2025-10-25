package com.miw.presentation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import jakarta.servlet.http.HttpSession;

import com.miw.business.bookmanager.BookManagerService;
import com.miw.model.Book;
import com.miw.model.CartItem;
import com.miw.model.Reservation;
import com.miw.model.ShoppingCart;

@Controller
@SessionAttributes({ "shoppingCart" })
public class ShoppingCartController {

	@Autowired
	private BookManagerService bookManagerService;

	public BookManagerService getBookManagerService() {
		return bookManagerService;
	}

	public void setBookManagerService(BookManagerService bookManagerService) {
		this.bookManagerService = bookManagerService;
	}

	@RequestMapping(value = "private/shoppingCart", method = RequestMethod.POST)
	public String addToCart(@RequestParam("bookId") int bookId,
			@RequestParam(value = "quantity", defaultValue = "1") int quantity,
			@ModelAttribute("shoppingCart") ShoppingCart cart, Model model) {

		try {
			List<Book> books = bookManagerService.getBooks();
			Book selectedBook = Book.findById(books, bookId);

			if (selectedBook == null) {
				model.addAttribute("error", "Book not found.");
				return "redirect:showBooks";
			}

			// Check current quantity in cart (only non-reserved items)
			int currentQuantityInCart = 0;
			for (CartItem item : cart.getItems()) {
				if (item.getBook().getId() == bookId && !item.isReserved()) {
					currentQuantityInCart = item.getQuantity();
					break;
				}
			}
			
			int availableToAdd = selectedBook.getStock() - currentQuantityInCart;

			// Check if already has maximum stock in cart
			if (availableToAdd <= 0) {
				model.addAttribute("error", 
						"You already have the maximum available stock of " +
						selectedBook.getTitle() + " in your cart (" + currentQuantityInCart + " units).");
				return "redirect:showBooks";
			}

			// Adjust quantity to maximum available
			int actualQuantityToAdd = Math.min(quantity, availableToAdd);

			// Add to cart (not reserved)
			cart.addBook(selectedBook, actualQuantityToAdd, false);
			
			int newTotal = currentQuantityInCart + actualQuantityToAdd;
			
			String message;
			if (actualQuantityToAdd < quantity) {
				// Added less than requested
				message = "Only " + availableToAdd + " unit(s) were available. " +
						  "Added " + actualQuantityToAdd + " unit(s). " +
						  "Total in cart: " + newTotal + " of " + selectedBook.getTitle();
			} else if (currentQuantityInCart > 0) {
				// Added to existing quantity
				message = actualQuantityToAdd + " unit(s) added. " +
						  "Total in cart: " + newTotal + " of " + selectedBook.getTitle();
			} else {
				// First time adding this book
				message = actualQuantityToAdd + " unit(s) of " + selectedBook.getTitle() + 
						  " added to cart successfully!";
			}
			model.addAttribute("message", message);

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Error adding book to cart.");
		}

		return "redirect:showBooks";
	}
	
	@RequestMapping(value = "private/reserveBook", method = RequestMethod.POST)
	public String reserveBook(@RequestParam("bookId") int bookId,
			@RequestParam(value = "quantity", defaultValue = "1") int quantity,
			@ModelAttribute("shoppingCart") ShoppingCart cart,
			Model model) {

		try {
			List<Book> books = bookManagerService.getBooks();
			Book selectedBook = Book.findById(books, bookId);

			if (selectedBook == null) {
				model.addAttribute("error", "Book not found.");
				return "redirect:showBooks";
			}

			// Check current quantity in cart (reserved items)
			int currentReservedInCart = 0;
			for (CartItem item : cart.getItems()) {
				if (item.getBook().getId() == bookId && item.isReserved()) {
					currentReservedInCart = item.getQuantity();
					break;
				}
			}
			
			int availableToReserve = selectedBook.getStock() - currentReservedInCart;

			// Check if already has maximum stock reserved in cart
			if (availableToReserve <= 0) {
				model.addAttribute("error", 
						"You already have the maximum available stock of " +
						selectedBook.getTitle() + " reserved in your cart (" + 
						currentReservedInCart + " units).");
				return "redirect:showBooks";
			}

			// Adjust quantity to maximum available
			int actualQuantityToReserve = Math.min(quantity, availableToReserve);

			// Add to cart with reserved flag (price will be 5% automatically)
			cart.addBook(selectedBook, actualQuantityToReserve, true); // true = reserved
			
			int newTotal = currentReservedInCart + actualQuantityToReserve;
			double reservationCost = selectedBook.getPrice() * newTotal * 0.05;
			
			String message;
			if (actualQuantityToReserve < quantity) {
				// Reserved less than requested
				message = "Only " + availableToReserve + " unit(s) were available to reserve. " +
						  "Added " + actualQuantityToReserve + " unit(s) to reservation. " +
						  "Total reserved in cart: " + newTotal + " of " + selectedBook.getTitle() + 
						  " (5% = " + String.format("%.2f", reservationCost) + " €). " +
						  "Complete checkout to confirm reservation.";
			} else if (currentReservedInCart > 0) {
				// Added to existing reservation
				message = actualQuantityToReserve + " unit(s) added to reservation. " +
						  "Total reserved in cart: " + newTotal + " of " + selectedBook.getTitle() + 
						  " (5% = " + String.format("%.2f", reservationCost) + " €)";
			} else {
				// First time reserving this book
				message = actualQuantityToReserve + " unit(s) of " + selectedBook.getTitle() + 
						  " added to cart as RESERVATION (5% = " + 
						  String.format("%.2f", reservationCost) + " €). " +
						  "Complete checkout to confirm reservation.";
			}
			model.addAttribute("message", message);

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Error reserving book.");
		}

		return "redirect:showBooks";
	}

	@RequestMapping("private/showShoppingCart")
	public String showShoppingCart(@ModelAttribute("shoppingCart") ShoppingCart cart, Model model) {
		try {
			// Refresh book data to get current stock
			List<Book> currentBooks = bookManagerService.getBooks();
			boolean hasStockIssues = false;

			// Update cart items with fresh book data
			for (CartItem item : cart.getItems()) {
				Book freshBook = Book.findById(currentBooks, item.getBook().getId());

				if (freshBook != null) {
					item.setBook(freshBook);
					if (freshBook.getStock() < item.getQuantity())
						hasStockIssues = true;
				}
			}
			model.addAttribute("cartItems", cart.getItems());
			model.addAttribute("total", cart.getTotal());

			if (hasStockIssues) {
				model.addAttribute("warning",
						"Some items in your cart have insufficient stock. Please review before checkout.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Error loading shopping cart.");
		}

		return "private/showShoppingCart";
	}

	@RequestMapping(value = "private/checkout", method = RequestMethod.POST)
	public String checkout(@ModelAttribute("shoppingCart") ShoppingCart cart,
			HttpSession session,
			Model model) {

		if (cart.isEmpty()) {
			model.addAttribute("error", "Your cart is empty.");
			return "redirect:showBooks";
		}

		try {
			List<Book> books = bookManagerService.getBooks();
			boolean stockAvailable = true;
			StringBuilder errorMessages = new StringBuilder();

			for (CartItem cartItem : cart.getItems()) {
				Book currentBook = Book.findById(books, cartItem.getBook().getId());

				if (currentBook != null && currentBook.getStock() < cartItem.getQuantity()) {
					stockAvailable = false;
					errorMessages.append("Insufficient stock for '").append(currentBook.getTitle())
							.append("'. Available: ").append(currentBook.getStock()).append(", Requested: ")
							.append(cartItem.getQuantity()).append(". ");
				}
			}

			if (!stockAvailable) {
				model.addAttribute("error", errorMessages.toString() + "Please update your cart.");
				return "redirect:showShoppingCart";
			}

			// Separate reserved and regular items
			ShoppingCart regularCart = new ShoppingCart();
			Reservation reservation = null;
			
			for (CartItem item : cart.getItems()) {
				if (item.isReserved()) {
					// Reserved item - move to Reservation
					if (reservation == null) {
						reservation = (Reservation) session.getAttribute("reservation");
						if (reservation == null) {
							reservation = new Reservation();
							session.setAttribute("reservation", reservation);
						}
					}
					reservation.addReservation(item.getBook(), item.getQuantity());
				} else {
					// Regular item - add to regular cart for processing
					regularCart.addBook(item.getBook(), item.getQuantity(), false);
				}
			}
			
			// Process regular purchases
			if (!regularCart.isEmpty()) {
				bookManagerService.processPurchase(regularCart);
			}
			
			// Process reservations (reduce stock)
			if (reservation != null && !reservation.isEmpty()) {
				bookManagerService.processReservation(reservation);
			}
			
			// Clear cart
			cart.clear();
			
			// Build success message
			StringBuilder successMessage = new StringBuilder("Checkout completed successfully! ");
			if (!regularCart.isEmpty()) {
				successMessage.append("Regular items purchased. ");
			}
			if (reservation != null && !reservation.isEmpty()) {
				successMessage.append("Reservations confirmed (5% paid). Check 'My Reservations' to complete purchases.");
			}
			
			model.addAttribute("message", successMessage.toString());
			return "redirect:showBooks";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Error processing checkout: " + e.getMessage());
			return "redirect:showShoppingCart";
		}
	}

	@RequestMapping(value = "private/removeFromCart", method = RequestMethod.POST)
	public String removeFromCart(@RequestParam("bookId") int bookId,
			@ModelAttribute("shoppingCart") ShoppingCart cart) {
		cart.removeBook(bookId);
		return "redirect:showShoppingCart";
	}

	@ModelAttribute("shoppingCart")
	public ShoppingCart getShoppingCart() {
		System.out.println("Initializing shopping cart");
		return new ShoppingCart();
	}
}