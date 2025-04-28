package com.unsalgasimliapplicationsnsug.projectnsug;

public class PartnerRequest {
    private String id;
    private String fromUserId;
    private String toUserId;
    private String status;

    // Required no‐arg constructor for Firestore
    public PartnerRequest() { }

    /** Convenience constructor for sending a new request */
    public PartnerRequest(String fromUserId, String toUserId) {
        this.fromUserId = fromUserId;
        this.toUserId   = toUserId;
        this.status     = "pending";
    }

    // -- getters & setters --

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getFromUserId() {
        return fromUserId;
    }
    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }
    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
