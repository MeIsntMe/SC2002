package hospitalsystem.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Medicine {
    private String medicineName;
    private List<Batch> batches;
    private int lowStockAlert;  // Low stock alert threshold

    // Constructor
    public Medicine(String medicineName, int lowStockAlert) {
        this.medicineName = medicineName;
        this.batches = new ArrayList<>();
        this.lowStockAlert = lowStockAlert;
    }

    // Getters
    public String getMedicineName() {
        return medicineName;
    }

    public List<Batch> getBatches() {
        return batches;
    }

    public int getLowStockAlert() {
        return lowStockAlert;
    }

    public void setLowStockAlert(int lowStockAlert) {
        this.lowStockAlert = lowStockAlert;
    }

    // Method to add a new batch to the medicine
    public void addBatch(int quantity, LocalDate expirationDate) {
        batches.add(new Batch(quantity, expirationDate));
        System.out.println("Added batch of " + quantity + " units for " + medicineName + ", expires on " + expirationDate);
    }

    // Get total quantity across all batches
    public int getTotalQuantity() {
        int total = 0;
        for (Batch batch : batches) {
            total += batch.getQuantity();
        }
        return total;
    }

    // Method to check if the total stock is below the low stock alert threshold
    public boolean isLowStock() {
        return getTotalQuantity() < lowStockAlert;
    }

    // Remove expired batches
    public void removeExpiredBatches() {
        Iterator<Batch> iterator = batches.iterator();
        while (iterator.hasNext()) {
            Batch batch = iterator.next();
            if (batch.isExpired()) {
                System.out.println("Removing expired batch of " + medicineName + " with expiration date: " + batch.getExpirationDate());
                iterator.remove();
            }
        }
    }

    // Get batches nearing expiration
    public List<Batch> getNearingExpirationBatches(int weeksBeforeExpiration) {
        List<Batch> nearingExpiration = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Batch batch : batches) {
            if (batch.getExpirationDate().isBefore(today.plusWeeks(weeksBeforeExpiration))) {
                nearingExpiration.add(batch);
            }
        }
        return nearingExpiration;
    }

    // Dispense a specified quantity, prioritizing batches closest to expiration
    public boolean dispense(int quantity) {
        Iterator<Batch> iterator = batches.iterator();
        while (iterator.hasNext() && quantity > 0) {
            Batch batch = iterator.next();
            int batchQuantity = batch.getQuantity();
            if (batchQuantity <= quantity) {
                quantity -= batchQuantity;
                batch.setQuantity(0);
                iterator.remove();
                System.out.println("Used up batch of " + medicineName + " with expiration date: " + batch.getExpirationDate());
            } else {
                batch.setQuantity(batchQuantity - quantity);
                System.out.println("Dispensed " + quantity + " units from batch of " + medicineName + " with expiration date: " + batch.getExpirationDate());
                quantity = 0;
            }
        }

        if (quantity > 0) {
            System.out.println("Insufficient stock to dispense " + quantity + " units of " + medicineName);
            return false;
        }
        return true;
    }
}
