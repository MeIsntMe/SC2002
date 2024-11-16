package hospitalsystem.model;

import hospitalsystem.enums.RequestStatus;

public class ReplenishmentRequest {
    private final int requestID;
    private final Medicine medicine; // Changed from String to Medicine
    private final int requestedQuantity;
    private RequestStatus status;

    // Constructor
    public ReplenishmentRequest(int requestID, Medicine medicine, int requestedQuantity) {
        this.requestID = requestID;
        this.medicine = medicine; // Store the Medicine object
        this.requestedQuantity = requestedQuantity;
        this.status = RequestStatus.PENDING; // Default status to pending approval
    }

    // Getters and setters
    public int getRequestID() {
        return requestID;
    }

    public Medicine getMedicine() {
        return medicine; // Return the Medicine object
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus newStatus) {
        this.status = newStatus;
    }

    @Override
    public String toString() {
        return "ReplenishmentRequest{" +
                "requestID=" + requestID +
                ", medicine=" + medicine.getMedicineName() + // Use Medicine object to get its name
                ", requestedQuantity=" + requestedQuantity +
                ", status=" + status +
                '}';
    }
}
