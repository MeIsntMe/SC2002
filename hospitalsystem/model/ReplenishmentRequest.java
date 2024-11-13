package hospitalsystem.model;

import hospitalsystem.enums.RequestStatus;
import java.time.LocalDate;

public class ReplenishmentRequest {
    private String medicineName;
    private int requestedQuantity;
    private LocalDate expirationDate;  // New field for expiration date
    private RequestStatus status;

    // Constructor with expiration date
    public ReplenishmentRequest(String medicineName, int requestedQuantity, LocalDate expirationDate) {
        this.medicineName = medicineName;
        this.requestedQuantity = requestedQuantity;
        this.expirationDate = expirationDate;
        this.status = RequestStatus.PENDING; // Default status to pending approval
    }

    // Overloaded constructor without expiration date
    public ReplenishmentRequest(String medicineName, int requestedQuantity) {
        this(medicineName, requestedQuantity, null);  // Set expirationDate as null
    }
    
    // Getters and setters
    public String getMedicineName() {
        return medicineName;
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

    public void accept() {
        this.status = RequestStatus.APPROVED;
    }

    public void reject() {
        this.status = RequestStatus.REJECTED;
    }
}
