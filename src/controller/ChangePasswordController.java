package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class ChangePasswordController {

    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label statusLabel;

    private Connection connection;
    private String currentUsername;

    public ChangePasswordController() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:noted.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setUsername(String username) {
        this.currentUsername = username;
    }

    @FXML
    private void handlePasswordChange() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            statusLabel.setText("Tots els camps són obligatoris.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            statusLabel.setText("Les noves contrasenyes no coincideixen.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        String checkQuery = "SELECT password FROM users WHERE username = ? AND password = ?";
        String updateQuery = "UPDATE users SET password = ? WHERE username = ?";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setString(1, currentUsername);
            checkStmt.setString(2, currentPassword);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                statusLabel.setText("Contrasenya actual incorrecta.");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                updateStmt.setString(1, newPassword);
                updateStmt.setString(2, currentUsername);
                updateStmt.executeUpdate();
                statusLabel.setText("Contrasenya canviada correctament.");
                statusLabel.setStyle("-fx-text-fill: green;");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Error en actualitzar la contrasenya.");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
