package com.gautamjain.techobyte.Modal;

public class Comment {

    private String id;
    private String comments;
    private String Publisher;

    public Comment() { }

    public Comment(String id, String comments, String Publisher) {
        this.id = id;
        this.comments = comments;
        this.Publisher = Publisher;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getComment() {
        return comments;
    }

    public void setComment(String comments) {
        this.comments = comments;
    }

    public void setPublisher(String Publisher) {
        this.Publisher = Publisher;
    }

    public String getPublisher() {
        return Publisher;
    }

}
