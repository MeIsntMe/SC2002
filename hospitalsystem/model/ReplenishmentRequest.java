package hospitalsystem.model;

import hospitalsystem.enums.RequestStatus;
import java.time.LocalDate;

public class ReplenishmentRequest {
    private final Medicine medicine;
    private final int requestedQuantity;
    private final LocalDate expirationDate;  // New field for expiration date
    private RequestStatus status;

    // Constructor with expiration date
    public ReplenishmentRequest(Medicine medicine, int requestedQuantity, LocalDate expirationDate) {
        this.medicine = medicine;
        this.requestedQuantity = requestedQuantity;
        this.expirationDate = expirationDate;
        this.status = RequestStatus.PENDING; // Default status to pending approval
    }

    // Getters and setters
    public Medicine getMedicine() {
        return medicine;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus newStatus) {
        this.status = newStatus;
    }
}
