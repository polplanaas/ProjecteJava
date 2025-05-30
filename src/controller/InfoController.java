package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.Song;

import java.sql.*;

/**
 * Mostra informació detallada d'una cançó.
 * 
 * @author Pol_Planas
 */
public class InfoController {

    @FXML private Label titleLabel, artistLabel, albumLabel, durationLabel, releaseDateLabel, genreLabel, ratingLabel;

    private Song song;
    private Connection connection;

    /**
     * Assigna la cançó i carrega les dades de la canço seleccionada.
     * 
     * @param song Cançó seleccionada
     */
    public void setSong(Song song) {
        this.song = song;
        connectToDatabase();
        loadSongDetails();
    }

    /**
     * Connecta a la base de dades.
     */
    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:noted.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Controla totes les dades de la canço amb la mitjana de retings que te i ho mostra per pantalla.
     */
    private void loadSongDetails() {
        String sql = """
            SELECT s.title, s.artist, s.album, s.duration_seconds, s.release_date, g.name AS genre_name,
                   IFNULL(AVG(r.rating), 0) AS avg_rating
            FROM song s
            LEFT JOIN genre g ON s.genre_id = g.genre_id
            LEFT JOIN ratings r ON s.song_id = r.song_id
            WHERE s.song_id = ?
            GROUP BY s.song_id
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, song.getSongId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                titleLabel.setText("Títol: " + rs.getString("title"));
                artistLabel.setText("Artista: " + rs.getString("artist"));
                albumLabel.setText("Àlbum: " + rs.getString("album"));
                durationLabel.setText("Duració: " + rs.getInt("duration_seconds") + " segons");
                releaseDateLabel.setText("Data de Llançament: " + rs.getString("release_date"));
                genreLabel.setText("Gènere: " + rs.getString("genre_name"));
                ratingLabel.setText(String.format("Puntuació Mitjana: %.1f ⭐", rs.getDouble("avg_rating")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}