package com.miw.presentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.miw.business.bookmanager.BookManagerService;
import com.miw.model.Book;
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
			Book selectedBook = null;

			for (Book book : books) {
				if (book.getId() == bookId) {
					selectedBook = book;
					break;
				}
			}

			if (selectedBook == null) {
				model.addAttribute("error", "Book not found.");
				return "redirect:showBooks";
			}

			// Check current quantity in cart
			int currentQuantityInCart = cart.getQuantity(bookId);
			int totalRequestedQuantity = currentQuantityInCart + quantity;

			// Validate stock availability
			if (totalRequestedQuantity > selectedBook.getStock()) {
				int availableToAdd = selectedBook.getStock() - currentQuantityInCart;
				if (availableToAdd > 0) {
					model.addAttribute("error",
							"Only " + availableToAdd + " more unit(s) available for " + selectedBook.getTitle());
				} else {
					model.addAttribute("error", "You already have the maximum available stock of "
							+ selectedBook.getTitle() + " in your cart.");
				}
				return "redirect:showBooks";
			}

			// Add to cart
			cart.addBook(bookId, quantity);
			model.addAttribute("message",
					quantity + " unit(s) of " + selectedBook.getTitle() + " added to cart successfully!");

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Error adding book to cart.");
		}

		return "redirect:showBooks";
	}

	@RequestMapping("private/showShoppingCart")
	public String showShoppingCart(@ModelAttribute("shoppingCart") ShoppingCart cart, Model model) {

		try {
			List<Book> books = bookManagerService.getBooks();
			List<Map<String, Object>> cartItems = new ArrayList<>();
			double total = 0.0;
			boolean hasStockIssues = false;

			for (Map.Entry<Integer, Integer> entry : cart.getItems().entrySet()) {
				int bookId = entry.getKey();
				int quantityInCart = entry.getValue();

				for (Book book : books) {
					if (book.getId() == bookId) {
						// Create a map with cart item information
						Map<String, Object> cartItem = new java.util.HashMap<>();
						cartItem.put("id", book.getId());
						cartItem.put("title", book.getTitle());
						cartItem.put("author", book.getAuthor());
						cartItem.put("price", book.getPrice());
						cartItem.put("quantityInCart", quantityInCart);
						cartItem.put("availableStock", book.getStock());
						cartItem.put("subtotal", book.getPrice() * quantityInCart);

						cartItems.add(cartItem);
						total += book.getPrice() * quantityInCart;

						// Check if there's a stock issue
						if (book.getStock() < quantityInCart) {
							hasStockIssues = true;
						}
						break;
					}
				}
			}

			model.addAttribute("cartItems", cartItems);
			model.addAttribute("total", total);

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
	public String checkout(@ModelAttribute("shoppingCart") ShoppingCart cart, Model model) {

		if (cart.isEmpty()) {
			model.addAttribute("error", "Your cart is empty.");
			return "redirect:showBooks";
		}

		try {
			List<Book> books = bookManagerService.getBooks();
			boolean stockAvailable = true;
			StringBuilder errorMessages = new StringBuilder();

			// Double-check stock availability (in case another user bought meanwhile)
			for (Map.Entry<Integer, Integer> entry : cart.getItems().entrySet()) {
				int bookId = entry.getKey();
				int quantity = entry.getValue();

				for (Book book : books) {
					if (book.getId() == bookId) {
						if (book.getStock() < quantity) {
							stockAvailable = false;
							errorMessages.append("Insufficient stock for '").append(book.getTitle())
									.append("'. Available: ").append(book.getStock()).append(", Requested: ")
									.append(quantity).append(". ");
						}
						break;
					}
				}
			}

			// Process purchase if stock is still available
			if (stockAvailable) {
				bookManagerService.processPurchase(cart);
				cart.clear();
				model.addAttribute("message", "Purchase completed successfully! Thank you for your order.");
				return "redirect:showBooks";
			} else {
				model.addAttribute("error", errorMessages.toString() + "Please update your cart.");
				return "redirect:showShoppingCart";
			}

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
