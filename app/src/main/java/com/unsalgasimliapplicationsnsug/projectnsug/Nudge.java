package com.unsalgasimliapplicationsnsug.projectnsug;

/**
 * Data model for both template nudges and sent/received nudge messages.
 */
public class Nudge {
    private String id;
    private String fromUserId;
    private String toUserId;
    private String title;
    private String body;
    private long timestamp;

    public Nudge() {
        // Required empty constructor for Firestore
    }

    public Nudge(String title, String body, String fromUserId, String toUserId, long timestamp) {
        this.title = title;
        this.body = body;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.timestamp = timestamp;
    }

    // ID (document ID) -- not stored in Firestore fields by default
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    // Sender UID
    public String getFromUserId() {
        return fromUserId;
    }
    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    // Recipient UID (null for templates)
    public String getToUserId() {
        return toUserId;
    }
    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }

    // Timestamp in millis since epoch, used for sorting
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
