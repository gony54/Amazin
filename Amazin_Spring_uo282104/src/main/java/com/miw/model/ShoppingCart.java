package com.miw.model;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private List<CartItem> items;
    
    public ShoppingCart() {
        this.items = new ArrayList<>();
    }
    
    public List<CartItem> getItems() {
        return items;
    }
    
    public void setItems(List<CartItem> items) {
        this.items = items;
    }
    
    public void addBook(Book book, int quantity) {
        addBook(book, quantity, false);
    }
    
    public void addBook(Book book, int quantity, boolean reserved) {
        CartItem existingItem = null;
        
        for (CartItem item : items) {
            if (item.getBook().getId() == book.getId() && item.isReserved() == reserved) {
                existingItem = item;
                break;
            }
        }
        
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            items.add(new CartItem(book, quantity, reserved));
        }
    }
    
    public void removeBook(int bookId) {
        CartItem itemToRemove = null;
        
        for (CartItem item : items) {
            if (item.getBook().getId() == bookId) {
                itemToRemove = item;
                break;
            }
        }
        
        if (itemToRemove != null) {
            items.remove(itemToRemove);
        }
    }
    
    public void updateQuantity(int bookId, int quantity) {
        if (quantity <= 0) {
            removeBook(bookId);
        } else {
            for (CartItem item : items) {
                if (item.getBook().getId() == bookId) {
                    item.setQuantity(quantity);
                    break;
                }
            }
        }
    }
    
    public int getQuantity(int bookId) {
        for (CartItem item : items) {
            if (item.getBook().getId() == bookId) {
                return item.getQuantity();
            }
        }
        return 0;
    }
    
    public void clear() {
        items.clear();
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    public int getTotalItems() {
        int total = 0;
        for (CartItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }
    
    public double getTotal() {
        double total = 0.0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }
    
    public List<CartItem> getReservedItems() {
        List<CartItem> reserved = new ArrayList<>();
        for (CartItem item : items) {
            if (item.isReserved())
                reserved.add(item);
        }
        return reserved;
    }
    
    public List<CartItem> getNonReservedItems() {
        List<CartItem> nonReserved = new ArrayList<>();
        for (CartItem item : items) {
            if (!item.isReserved())
                nonReserved.add(item);
        }
        return nonReserved;
    }
}