package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;

import model.Song;

import java.net.URL;
import java.sql.*;
import java.util.*;

public class Controller implements Initializable {

    // Components Música
    @FXML private ListView<String> genreListView;
    @FXML private TableView<Song> songTableView;
    @FXML private TableColumn<Song, String> titleColumn;
    @FXML private TableColumn<Song, String> artistColumn;
    @FXML private TableColumn<Song, Double> ratingColumn;
    @FXML private ListView<String> top5ListView;  // 👈 Top 5 cançons

    private Connection connection;
    private Map<String, Integer> genreMap = new HashMap<>();
    private ObservableList<Song> songList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connectToDatabase();
        setupMusicTable();
        loadGenres();
        loadTop5Songs(); // 👈 Carreguem el rànquing top 5

        genreListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Integer genreId = genreMap.get(newVal);
                if (genreId != null) {
                    loadSongsByGenre(genreId);
                }
            }
        });
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:noted.db");
        } catch (SQLException e) {
            e.printStackTrace();
            // Aquí pots afegir una alerta si ho vols
        }
    }

    private void setupMusicTable() {
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        artistColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getArtist()));
        ratingColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAverageR()).asObject());

        songTableView.setItems(songList);
    }

    private void loadGenres() {
        String sql = "SELECT genre_id, name FROM genre";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            ObservableList<String> genres = FXCollections.observableArrayList();
            while (rs.next()) {
                String name = rs.getString("name");
                int id = rs.getInt("genre_id");
                genreMap.put(name, id);
                genres.add(name);
            }
            genreListView.setItems(genres);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadSongsByGenre(int genreId) {
        songList.clear();
        String sql = """
            SELECT s.song_id, s.title, s.artist, IFNULL(AVG(r.rating), 0) AS avg_rating
            FROM song s
            LEFT JOIN ratings r ON s.song_id = r.song_id
            WHERE s.genre_id = ?
            GROUP BY s.song_id
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, genreId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int songId = rs.getInt("song_id");
                String title = rs.getString("title");
                String artist = rs.getString("artist");
                double avgRating = rs.getDouble("avg_rating");

                songList.add(new Song(songId, title, artist, avgRating));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTop5Songs() {
        String sql = """
            SELECT s.title, s.artist, IFNULL(AVG(r.rating), 0) AS avg_rating
            FROM song s
            LEFT JOIN ratings r ON s.song_id = r.song_id
            GROUP BY s.song_id
            ORDER BY avg_rating DESC
            LIMIT 5
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            ObservableList<String> top5 = FXCollections.observableArrayList();
            while (rs.next()) {
                String title = rs.getString("title");
                String artist = rs.getString("artist");
                double rating = rs.getDouble("avg_rating");

                top5.add(String.format("%s - %s (⭐ %.1f)", title, artist, rating));
            }
            top5ListView.setItems(top5);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}