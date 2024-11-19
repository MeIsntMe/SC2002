package hospitalsystem.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a medicine in the Hospital Management System.
 * A medicine has a name, instructions, batches, and minimum stock level.
 *
 * @author Gracelynn, Shaivi
 * @version 1.0
 * @since 2024-11-19
 */
public class Medicine {
    /**
     * The name of the medicine.
     */
    private final String medicineName;

    /**
     * The instructions for the medicine.
     */
    private final String instructions;

    /**
     * The list of batches for the medicine.
     */
    private List<Batch> batches;

    /**
     * The minimum stock level for the medicine.
     */
    private int minimumStockLevel;

    /**
     * Constructs a Medicine object with the given parameters.
     *
     * @param medicineName The name of the medicine.
     * @param minimumStockLevel The minimum stock level for the medicine.
     * @param instructions The instructions for the medicine.
     */
    public Medicine(String medicineName, int minimumStockLevel, String instructions) {
        this.medicineName = medicineName;
        this.minimumStockLevel = minimumStockLevel;
        this.instructions = instructions;
    }

    // Getters
    public String getMedicineName() {
        return medicineName;
    }

    public List<Batch> getBatches() {
        if (batches == null) {
            batches = new ArrayList<>();
        }
        return batches;
    }

    public int getMinStockLevel() {
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

    // Nested Batch class within Medicine
    public class Batch implements Comparable<Batch>{
        /**
         * The quantity of the batch.
         */
        private int quantity;
        /**
         * The expiration date of the batch.
         */
        private final LocalDate expirationDate;

        /**
         * Constructs a Batch object with the given parameters.
         *
         * @param quantity The quantity of the batch.
         * @param expirationDate The expiration date of the batch.
         */
        public Batch(int quantity, LocalDate expirationDate) {
            this.quantity = quantity;
            this.expirationDate = expirationDate;
        }

        /**
         * Compares this batch with another batch based on their expiration dates.
         *
         * @param otherBatch The batch to compare with.
         * @return A negative integer, zero, or a positive integer as this batch is less than, equal to, or greater than the specified batch.
         */
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
