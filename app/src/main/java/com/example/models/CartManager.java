package com.example.models;

import java.util.ArrayList;

public class CartManager {
    private static CartManager instance;
    private ArrayList<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public ArrayList<CartItem> getCartItems() {
        return cartItems;
    }

    public void addToCart(Product product, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getProductId().equals(product.getProductId())) {
                int newQty = item.getQuantity() + quantity;
                // constrain to stock limit
                if (newQty > product.getStock()) {
                    newQty = product.getStock();
                }
                item.setQuantity(newQty);
                return;
            }
        }
        cartItems.add(new CartItem(product, quantity));
    }

    public void updateQuantity(String productId, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getProductId().equals(productId)) {
                item.setQuantity(quantity);
                return;
            }
        }
    }

    public void removeFromCart(String productId) {
        CartItem toRemove = null;
        for (CartItem item : cartItems) {
            if (item.getProduct().getProductId().equals(productId)) {
                toRemove = item;
                break;
            }
        }
        if (toRemove != null) {
            cartItems.remove(toRemove);
        }
    }

    public void clearCart() {
        cartItems.clear();
    }

    public double getTotalAmount() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getSubtotal();
        }
        return total;
    }
}
