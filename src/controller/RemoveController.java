package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RemoveController {

    @FXML private Button deleteButton;
    @FXML private Button cancelButton;

    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    @FXML
    private void handleDelete() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:noted.db")) {
            String deleteSql = "DELETE FROM user WHERE username = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setString(1, username);
                deleteStmt.executeUpdate();
            }

            showAlert(Alert.AlertType.INFORMATION, "Compte eliminat correctament.");
            ((Stage) deleteButton.getScene().getWindow()).close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error en eliminar el compte.");
        }
    }

    @FXML
    private void handleCancel() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Eliminar Compte");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}