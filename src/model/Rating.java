package model;

public class Rating {
    private int ratingId;
    private int rating;
    private String reviewText;
    private int userId;
    private int songId;

    public Rating(int ratingId, int rating, String reviewText, int userId, int songId) {
        this.ratingId = ratingId;
        this.rating = rating;
        this.reviewText = reviewText;
        this.userId = userId;
        this.songId = songId;
    }

    public int getRatingId() { return ratingId; }
    public int getRating() { return rating; }
    public String getReviewText() { return reviewText; }
    public int getUserId() { return userId; }
    public int getSongId() { return songId; }
}
