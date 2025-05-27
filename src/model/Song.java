package model;

public class Song {
	private int song_id;
    private String title;
    private String artist;
    private String album;
    private int durationSeconds;
    private String releaseDate;
    private int genre_id;
    private double averageR;

    public Song(int song_id, String title, String artist, double averageR) {
        this.song_id = song_id;
        this.title = title;
        this.artist = artist;
        this.averageR = averageR;
    }


	public int getSongId() { return song_id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public int getDurationSeconds() { return durationSeconds; }
    public int getGenreId() { return genre_id; }
    public double getAverageR() { return averageR; }
}
