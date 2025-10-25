package com.miw.persistence.book;

import java.util.List;

import jakarta.persistence.EntityManager;

import org.apache.logging.log4j.*;

import com.miw.model.Book;
import com.miw.persistence.Dba;

public class BookDAO implements BookDataService {
	protected Logger logger = LogManager.getLogger(getClass());

	public List<Book> getBooks() throws Exception {
		List<Book> resultList = null;

		Dba dba = new Dba();
		try {
			EntityManager em = dba.getActiveEm();

			resultList = em.createQuery("Select a From Book a", Book.class).getResultList();

			logger.debug("Result list: " + resultList.toString());
			for (Book next : resultList) {
				logger.debug("next book: " + next);
			}

		} finally {
			dba.closeEm();
		}
		return resultList;
	}

	public Book newBook(Book book) throws Exception {
		Dba dba = new Dba();
		try {
			EntityManager em = dba.getActiveEm();
			em.persist(book);
			em.getTransaction().commit();

			logger.debug("Alta libro: " + book.toString());
		} finally {
			dba.closeEm();
		}
		return book;
	}

	public void updateBook(Book book) throws Exception {
		Dba dba = new Dba();
		EntityManager em = null;
		try {
			em = dba.getActiveEm();
			Book managedBook = em.find(Book.class, book.getId());

			if (managedBook != null) {
				managedBook.setStock(book.getStock());
				logger.debug("Found book to update: " + managedBook.toString());
			} else {
				logger.error("Book with id " + book.getId() + " not found for update.");
				throw new Exception("Book with id " + book.getId() + " not found for update.");
			}
			em.getTransaction().commit();
			logger.debug("Updated stock for book: " + managedBook.toString());

		} catch (Exception e) {
			if (em != null && em.getTransaction().isActive())
				em.getTransaction().rollback();
			logger.error("Error updating book: " + book.toString(), e);
			throw e;
		} finally {
			if (em != null && em.isOpen())
				dba.closeEm();
		}
	}
}