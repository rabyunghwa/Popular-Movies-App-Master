package com.awesome.byunghwa.app.popularmoviesapp2.data;

/**
 * Created by ByungHwa on 7/20/2015.
 */
public class Review {

    private int id;
    private String reviewAuthor;
    private String reviewContent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public void setReviewAuthor(String reviewAuthor) {
        this.reviewAuthor = reviewAuthor;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }
}
