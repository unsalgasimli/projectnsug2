package com.unsalgasimliapplicationsnsug.projectnsug;

public class MediaItem {
    public String id;
    public String userId;
    public String type;    // "image","audio","voice","text","nudge"
    public String url;     // storage URL for image/audio/voice
    public String text;    // for "text" type
    public long timestamp;

    // Required no-arg constructor
    public MediaItem() {}
}
