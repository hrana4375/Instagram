package com.example.instaclone;

public class Post {
    private String author;
    private String photoUrl;

    public Post() {

    }

    public Post(String author,String photoUrl) {
        this.author = author;
        this.photoUrl = photoUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

}
