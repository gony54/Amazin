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

import com.miw.business.bookmanager.BookManagerService;
import com.miw.model.Book;
import com.miw.model.CartItem;
import com.miw.model.Reservation;

@Controller
@SessionAttributes({ "reservation" })
public class ReservationController {

	@Autowired
	private BookManagerService bookManagerService;

	public BookManagerService getBookManagerService() {
		return bookManagerService;
	}

	public void setBookManagerService(BookManagerService bookManagerService) {
		this.bookManagerService = bookManagerService;
	}

	@RequestMapping("private/showReservations")
	public String showReservations(@ModelAttribute("reservation") Reservation reservation, Model model) {
		try {
			// Refresh book data to get current prices
			List<Book> currentBooks = bookManagerService.getBooks();

			// Update reservation items with fresh book data
			for (CartItem item : reservation.getReservedItems()) {
				Book freshBook = Book.findById(currentBooks, item.getBook().getId());
				if (freshBook != null)
					item.setBook(freshBook);
			}
			model.addAttribute("reservedItems", reservation.getReservedItems());
			model.addAttribute("totalPaid", reservation.getTotalReservationCost());
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Error loading reservations.");
		}
		return "private/showReservations";
	}

	@RequestMapping(value = "private/cancelReservation", method = RequestMethod.POST)
	public String cancelReservation(@RequestParam("bookId") int bookId,
			@ModelAttribute("reservation") Reservation reservation, Model model) {
		try {
			CartItem reservedItem = reservation.getReservation(bookId);

			if (reservedItem != null) {
				int quantity = reservedItem.getQuantity();

				// Cancel reservation (restore stock)
				bookManagerService.cancelReservation(bookId, quantity);

				// Remove from reservation list
				reservation.removeReservation(bookId);

				model.addAttribute("message", "Reservation cancelled successfully. Stock restored.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Error cancelling reservation: " + e.getMessage());
		}

		return "redirect:showReservations";
	}

	@RequestMapping(value = "private/buyReservedBook", method = RequestMethod.POST)
	public String buyReservedBook(@RequestParam("bookId") int bookId,
			@ModelAttribute("reservation") Reservation reservation, Model model) {
		try {
			CartItem reservedItem = reservation.getReservation(bookId);

			if (reservedItem != null) {
				int quantity = reservedItem.getQuantity();
				double remainingAmount = reservedItem.getRemainingAmount();

				// Convert reservation to purchase (stock already reduced during checkout)
				bookManagerService.convertReservationToPurchase(bookId, quantity);

				// Remove from reservation list
				reservation.removeReservation(bookId);

				model.addAttribute("message", "Purchase completed successfully! " + "Amount paid: "
						+ String.format("%.2f", remainingAmount) + " €");
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Error buying reserved book: " + e.getMessage());
		}
		return "redirect:showReservations";
	}

	@ModelAttribute("reservation")
	public Reservation getReservation() {
		System.out.println("Initializing reservation");
		return new Reservation();
	}
}