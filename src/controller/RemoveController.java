package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.sql.*;

/**
 * Controlador per eliminar un compte d'usuari.
 * 
 * @author Pol_Planas
 */
public class RemoveController {

    @FXML private Button deleteButton;
    @FXML private Button cancelButton;

    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Fa la connexio a la base de dades i fa simplement un delete per eliminar l'usuari seleccionat.
     */
    @FXML
    private void handleDelete() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:noted.db");
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM user WHERE username = ?")) {

            stmt.setString(1, username);
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Compte eliminat correctament.");
            closeWindow();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error en eliminar el compte.");
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    /**
     * Tenca la finestre
     */
    private void closeWindow() {
        ((Stage) deleteButton.getScene().getWindow()).close();
    }

    /**
     * Mostra el missatge de validacio.
     */
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Eliminar Compte");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}