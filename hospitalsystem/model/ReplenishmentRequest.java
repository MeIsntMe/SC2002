package hospitalsystem.model;

import hospitalsystem.enums.RequestStatus;

public class ReplenishmentRequest {
    private final int requestID; 
    private final String medicineName;
    private final int requestedQuantity;
    private RequestStatus status;

    // Constructor with expiration date
    public ReplenishmentRequest(int requestID, String medicineName, int requestedQuantity) {
        this.requestID = requestID;
        this.medicineName = medicineName;
        this.requestedQuantity = requestedQuantity;
        this.status = RequestStatus.PENDING; // Default status to pending approval
    }

    // Getters and setters

    public int getRequestID(){
        return requestID;
    }

    public String getMedicineName() {
        return medicineName;
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
}
