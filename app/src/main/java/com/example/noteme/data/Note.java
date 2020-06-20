package com.example.noteme.data;

public class Note {
    private long ID;
    private String title;
    private String content;
    private long currentTimestamp;
    private String color;

    Note() {
    }

    //For New Notes
    public Note(String title, String content, long timestamp, String color) {
        this.title = title;
        this.content = content;
        this.currentTimestamp = timestamp;
        this.color = color;
    }

    //For Notes updates
    public Note(long ID, String title, String content, long timestamp, String color) {
        this.ID = ID;
        this.title = title;
        this.content = content;
        this.currentTimestamp = timestamp;
        this.color = color;
    }

    /*
        public Note(long ID, String title, String content, String color){
            this.ID = ID;
            this.title = title;
            this.content = content;
            this.color = color;
        }

        public Note(String title, String content, String color){
            this.title = title;
            this.content = content;
            this.color = color;
        }
    */
    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCurrentTimestamp() {
        return currentTimestamp;
    }

    public void setCurrentTimestamp(long time) {
        this.currentTimestamp = time;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
