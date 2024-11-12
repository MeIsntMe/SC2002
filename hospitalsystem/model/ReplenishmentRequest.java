package hospitalsystem.model;

import hospitalsystem.enums.RequestStatus;
import java.time.LocalDate;

public class ReplenishmentRequest {
    private String medicineName;
    private int requestedQuantity;
    private LocalDate expirationDate; // New attribute to track the expiration date of the requested batch
    private RequestStatus status; 

    // Constructor with expiration date
    public ReplenishmentRequest(String medicineName, int requestedQuantity, LocalDate expirationDate) {
        this.medicineName = medicineName;
        this.requestedQuantity = requestedQuantity;
        this.expirationDate = expirationDate;
        this.status = RequestStatus.PENDING; // Default status set to pending approval
    }

    // Getters
    public String getMedicineName() {
        return medicineName;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public LocalDate getExpirationDate() {
        return expirationDate; // New getter for expiration date
    }

    public RequestStatus getStatus() {
        return status;
    }

    // Setters for status changes
    public void accept() {
        this.status = RequestStatus.APPROVED;
    }

    public void reject() {
        this.status = RequestStatus.REJECTED;
    }
}
