package hospitalsystem.model;

import hospitalsystem.enums.RequestStatus;

/**
 * Represents a replenishment request for a medicine in the Hospital Management System.
 *
 * @author An Xian, Shaivi
 * @version 1.0
 * @since 2024-11-19
 */
public class ReplenishmentRequest {
    /**
     * The unique identifier of the replenishment request.
     */
    private final int requestID;

    /**
     * The medicine for which the replenishment is requested.
     */
    private final Medicine medicine;

    /**
     * The requested quantity of the medicine.
     */
    private final int requestedQuantity;

    /**
     * The status of the replenishment request.
     */
    private RequestStatus status;

    /**
     * Constructs a ReplenishmentRequest object with the given parameters.
     *
     * @param requestID The unique identifier of the replenishment request.
     * @param medicine The medicine for which the replenishment is requested.
     * @param requestedQuantity The requested quantity of the medicine.
     */
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

    /**
     * Returns a string representation of the replenishment request.
     *
     * @return A string representation of the replenishment request.
     */
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
