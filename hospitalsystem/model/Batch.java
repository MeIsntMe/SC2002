package hospitalsystem.model;

import java.time.LocalDate;

public class Batch {
    private int quantity;
    private LocalDate expirationDate;

    // Constructor
    public Batch(int quantity, LocalDate expirationDate) {
        this.quantity = quantity;
        this.expirationDate = expirationDate;
    }

    // Getters
    public int getQuantity() {
        return quantity;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    // Setter for quantity (used when dispensing medication)
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Check if the batch is expired
    public boolean isExpired() {
        return expirationDate.isBefore(LocalDate.now());
    }

    // Check if the batch is nearing expiration (e.g., within 2 weeks)
    public boolean isNearingExpiration() {
        return expirationDate.isBefore(LocalDate.now().plusWeeks(2));
    }
}

