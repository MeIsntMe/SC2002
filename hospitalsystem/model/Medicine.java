package hospitalsystem.model;

public class Medicine {
    private String medicineName;
    private int initialStock;
    private int lowStockAlert;

    public Medicine(String medicineName, int initialStock, int lowStockAlert) {
        this.medicineName = medicineName;
        this.initialStock = initialStock;
        this.lowStockAlert = lowStockAlert;
    }

    public String getMedicineName() { return medicineName; }
    public int getInitialStock() { return initialStock; }
    public int getLowStockAlert() { return lowStockAlert; }

    public void setInitialStock(int newStockLevel) {
        this.initialStock = newStockLevel;
    }
    public void setLowStockAlert(int newAlert) {
        this.lowStockAlert = newAlert;
    }
}
