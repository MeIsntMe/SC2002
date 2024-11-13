package hospitalsystem.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Medicine {
    private final String medicineName;
    private final String instructions;
    private List<Batch> batches;
    private int minimumStockLevel;  // Low stock alert threshold
    

    // Constructor
    public Medicine(String medicineName, int minimumStockLevel, String instructions) {
        this.medicineName = medicineName;
        this.batches = new ArrayList<>(); //This should be retrieve from csv
        this.minimumStockLevel = minimumStockLevel;
        this.instructions = instructions;
        //Get rid of expired batches
        Iterator<Batch> iterator = batches.iterator();
        while (iterator.hasNext()) {
            Batch batch = iterator.next();
            if (batch.isExpired()) {
                System.out.println("Removing expired batch of " + medicineName + " with expiration date: " + batch.getExpirationDate());
                iterator.remove();
            }
        }
    }

    // Getters
    public String getMedicineName() {
        return medicineName;
    }

    public List<Batch> getBatches() {
        return batches;
    }

    public int getLowStockAlert() {
        return minimumStockLevel;
    }

    public String getInstructions(){
        return instructions;
    }

    public void setMinimumSttockLevel(int lowStockAlert) {
        this.minimumStockLevel = lowStockAlert;
    }

    public void setBatch(List<Batch> newBatches){
        this.batches = newBatches;
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
    public boolean getIsLowStock() {
        return getTotalQuantity() < minimumStockLevel;
    }

    @Override
    public String toString(){
        return medicineName;
    }


    //Shift to pharmacistControl
    // Method to add a new batch to the medicine
    public void addBatch(int quantity, LocalDate expirationDate) {
        batches.add(new Batch(quantity, expirationDate));
        batches.sort(Comparator.comparing(Batch::getExpirationDate));
        System.out.println("Added batch of " + quantity + " units for " + medicineName + ", expires on " + expirationDate);
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
        int batchQuantity;
        while (iterator.hasNext() && quantity > 0) {
            Batch batch = iterator.next();
            batchQuantity = batch.getQuantity();
            if (batchQuantity <= quantity) {
                quantity -= batchQuantity;
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

    // Nested Batch class within Medicine
    public class Batch implements Comparable<Batch>{
        private int quantity;
        private final LocalDate expirationDate;

        // Constructor
        public Batch(int quantity, LocalDate expirationDate) {
            this.quantity = quantity;
            this.expirationDate = expirationDate;
        }

        @Override
        public int compareTo(Batch otherBatch){
            return this.expirationDate.compareTo(otherBatch.expirationDate);
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
}
