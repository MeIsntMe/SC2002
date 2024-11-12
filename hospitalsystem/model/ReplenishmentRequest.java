package hospitalsystem.model;

import hospitalsystem.enums.RequestStatus;

public class ReplenishmentRequest {
    private String medicineName;
    private int requestedQuantity;
    private RequestStatus status; 
    //isPending == true if pending approval
    //isPending == truefalse if rejected
    //request deleted from request list if approved

    public ReplenishmentRequest(String medicineName, int requestedQuantity) {
        this.medicineName = medicineName;
        this.requestedQuantity = requestedQuantity;
        this.status = RequestStatus.PENDING; // default to not approved
    }

    public String getMedicineName() {return medicineName;}
    public int getRequestedQuantity() {return requestedQuantity;}
    public RequestStatus getStatus() {return status;}

    public void accept() {this.status = RequestStatus.APPROVED;}
    public void reject() {this.status = RequestStatus.REJECTED;}
}