package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Song;

import java.sql.*;

public class RatingController {

    @FXML private Label songLabel;
    @FXML private TextField ratingField;
    @FXML private TextArea commentArea;

    private int songId;
    private int userId = 1; // Posa aquí la id de l'usuari que ha fet login o passa-la per setUserId si vols.

    private Connection connection;

    @FXML
    public void initialize() {
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:noted.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setSong(Song song) {
        this.songId = song.getSongId();
        songLabel.setText("Puntuar: " + song.getTitle());
    }

    @FXML
    private void handleSubmitRating() {
        String ratingText = ratingField.getText();
        String comment = commentArea.getText();

        int rating;
        try {
            rating = Integer.parseInt(ratingText);
            if (rating < 1 || rating > 5) {
                showAlert(Alert.AlertType.ERROR, "Error", "La puntuació ha de ser un número entre 1 i 5.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "La puntuació ha de ser un número vàlid.");
            return;
        }

        // Inserta la valoració a la base de dades
        String sql = "INSERT INTO ratings (song_id, user_id, rating, review_text) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, songId);
            stmt.setInt(2, userId);
            stmt.setInt(3, rating);
            stmt.setString(4, comment);
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Correcte", "Valoració enviada correctament!");
            closeWindow();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No s'ha pogut desar la valoració.");
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) songLabel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}