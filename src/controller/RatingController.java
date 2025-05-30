package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Song;

import java.sql.*;

/**
 * Controlador per enviar puntuacions i comentaris sobre cançons.
 * 
 * @author Pol_Planas
 */
public class RatingController {

    @FXML private Label songLabel;
    @FXML private TextField ratingField;
    @FXML private TextArea commentArea;

    private int songId;
    private int userId = 1;
    private Connection connection;

    /**
     * Es conecta a la base de dades.
     */
    @FXML
    public void initialize() {
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

    /**
     * Comprova els errors i si les dades son correctes fa el insert amb les dades que ha introduit.
     */
    @FXML
    private void handleSubmitRating() {
        int rating;
        try {
            rating = Integer.parseInt(ratingField.getText());
            if (rating < 1 || rating > 5) {
                showAlert(Alert.AlertType.ERROR, "Error", "La puntuació ha de ser entre 1 i 5.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Introdueix un número vàlid.");
            return;
        }

        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO ratings (song_id, user_id, rating, review_text) VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, songId);
            stmt.setInt(2, userId);
            stmt.setInt(3, rating);
            stmt.setString(4, commentArea.getText());
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Correcte", "Valoració enviada!");
            ((Stage) songLabel.getScene().getWindow()).close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No s'ha pogut desar la valoració.");
        }
    }

    /**
     * Mostra el missarge de confirmacio.
     */
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
