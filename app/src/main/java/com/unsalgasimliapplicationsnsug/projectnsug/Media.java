package com.unsalgasimliapplicationsnsug.projectnsug;

public class Media {
    private String id;
    private String name;
    private String url;
    private String userId;
    private String type; // e.g. "image", "audio"

    // Firestore requires an empty constructor
    public Media() { }

    public Media(String id, String name, String url, String userId, String type) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.userId = userId;
        this.type = type;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
