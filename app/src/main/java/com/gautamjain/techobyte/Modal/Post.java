package com.gautamjain.techobyte.Modal;

public class Post {

    private String Description;
    private String ImageUrl;
    private String PostId;
    private String Publisher;

    public Post(){}

    public Post(String description, String imageUrl, String postId, String publisher) {
        Description = description;
        ImageUrl = imageUrl;
        PostId = postId;
        Publisher = publisher;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getPostId() {
        return PostId;
    }

    public void setPostId(String postId) {
        PostId = postId;
    }

    public String getPublisher() {
        return Publisher;
    }

    public void setPublisher(String publisher) {
        Publisher = publisher;
    }
}
