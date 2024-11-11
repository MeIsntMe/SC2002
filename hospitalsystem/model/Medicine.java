package hospitalsystem.model;

import java.time.LocalDate;

public class Medicine {
    private String medicineName;
    private int initialStock;
    private int lowStockAlert;
    private LocalDate expirationDate;

    // Constructor
    public Medicine(String medicineName, int initialStock, int lowStockAlert, LocalDate expirationDate) {
        this.medicineName = medicineName;
        this.initialStock = initialStock;
        this.lowStockAlert = lowStockAlert;
        this.expirationDate = expirationDate;
    }

    // Getters
    public String getMedicineName() {
        return medicineName;
    }

    public int getInitialStock() {
        return initialStock;
    }

    public int getLowStockAlert() {
        return lowStockAlert;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    // Setters
    public void setInitialStock(int initialStock) {
        this.initialStock = initialStock;
    }

    public void setLowStockAlert(int lowStockAlert) {
        this.lowStockAlert = lowStockAlert;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    // Method to check if medication is nearing expiration (within 2 weeks)
    public boolean isNearingExpiration() {
        LocalDate today = LocalDate.now();
        return expirationDate.isBefore(today.plusWeeks(2));
    }
}
