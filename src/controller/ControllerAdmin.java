package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;

import model.Song;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;

/**
 * Aquests es el controlador del usuari administrador a l'aplicació, fa el mateix que l'altre excepte veure els usuaris i no permet veure elminar la compte.
 * 
 * @author Pol_Planas
 */
public class ControllerAdmin implements Initializable {

    @FXML private ListView<String> genreListView;
    @FXML private TableView<Song> songTableView;
    @FXML private TableColumn<Song, String> titleColumn;
    @FXML private TableColumn<Song, String> artistColumn;
    @FXML private TableColumn<Song, Double> ratingColumn;
    @FXML private TableColumn<Song, Void> rateColumn;
    @FXML private TableColumn<Song, Void> infoColumn;
    @FXML private ListView<String> top5ListView;
    @FXML private ListView<String> actualUsers;

    private Connection connection;
    private Map<String, Integer> genreMap = new HashMap<>();
    private ObservableList<Song> songList = FXCollections.observableArrayList();

    /**
     * Inicialitza el controlador, connectant a la BD i carregant els gèneres i cançons.
     * 
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connectToDatabase();
        setupMusicTable();
        loadGenres();
        loadTop5Songs();
        handleShowUsers();

        // Assigna una acció al seleccionar un gènere
        genreListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Integer genreId = genreMap.get(newVal);
                if (genreId != null) {
                    loadSongsByGenre(genreId);
                }
            }
        });
    }

    // Estableix la connexió amb la base de dades SQLite
    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:noted.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Configura la taula de cançons i afegeix les columnes i botons
    private void setupMusicTable() {
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        artistColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getArtist()));
        ratingColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAverageR()).asObject());

        songTableView.setItems(songList);
        addRateButtonToTable();
        addInfoButtonToTable();
    }

    // Carrega els gèneres disponibles a la ListView i a l'array map
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

    /**
     * Carrega les cançons associades al gènere concret i mostra algunes dades de la canço.
     *
     * @param genreId
     */
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

    // Carrega les 5 cançons més ben valorades i li fiquem les dades basicas
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

    // Afegeix un botó per puntuar a cada fila de la taula amb el nom "Puntuar".
    private void addRateButtonToTable() {
        rateColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Puntuar");

            {
                btn.setOnAction(event -> {
                    Song song = getTableView().getItems().get(getIndex());
                    openRatingView(song);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    // Afegeix un botó per veure informació a cada fila amb el nom "Més info".
    private void addInfoButtonToTable() {
        infoColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Més Info");

            {
                btn.setOnAction(event -> {
                    Song song = getTableView().getItems().get(getIndex());
                    openInfoView(song);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    /**
     * Ens obre la finestra RatingView per puntuar la cançó seleccionada.
     * 
     * @param song
     */
    private void openRatingView(Song song) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/RatingView.fxml"));
            Parent root = loader.load();

            RatingController controller = loader.getController();
            controller.setSong(song);

            Stage stage = new Stage();
            stage.setTitle("Puntuar Cançó");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ens obre la finestre InfoView amb la informació detallada de la cançó.
     * 
     * @param song
     */
    private void openInfoView(Song song) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/InfoView.fxml"));
            Parent root = loader.load();

            InfoController controller = loader.getController();
            controller.setSong(song);

            Stage stage = new Stage();
            stage.setTitle("Informació de la Cançó");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obre la vista per canviar la contrasenya connectanse a la finestre ChangePasswordView.
     */
    @FXML
    private void handleChangePassword() {
        String username = "keofan123";

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ChangePasswordView.fxml"));
            Parent root = loader.load();

            ChangePasswordController controller = loader.getController();
            controller.setUsername(username);

            Stage stage = new Stage();
            stage.setTitle("Canviar Contrasenya");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Gestiona els usaris del sistema.
     */
    @FXML
    private void handleShowUsers() {
        ObservableList<String> usersList = FXCollections.observableArrayList();
        String sql = "SELECT username, password FROM users";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:noted.db");
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                usersList.add("Usuari: " + username + " | Contrasenya: " + password);
            }

            actualUsers.setItems(usersList);

        } catch (SQLException e) {
            e.printStackTrace();
            usersList.clear();
            usersList.add("Error carregant usuaris.");
            actualUsers.setItems(usersList);
        }
    }
}
