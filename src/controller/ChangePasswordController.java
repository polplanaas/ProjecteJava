package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

/**
 * Controlador per canviar la contrasenya de l'usuari.
 * 
 * @author Pol_Planas
 */
public class ChangePasswordController {

    @FXML private PasswordField currentPasswordField, newPasswordField, confirmPasswordField;
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

    /**
     * Assigna l'usuari actual.
     * 
     * @param username
     */
    public void setUsername(String username) {
        this.currentUsername = username;
    }
    
    /**
     * Revisa que les dades siguin correctas i fa el update a la base de dades.
     */
    @FXML
    private void handlePasswordChange() {
        String current = currentPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            show("Tots els camps són obligatoris.", "red");
            return;
        }
        if (!newPass.equals(confirm)) {
            show("Les noves contrasenyes no coincideixen.", "red");
            return;
        }

        String checkSql = "SELECT 1 FROM users WHERE username = ? AND password = ?";
        String updateSql = "UPDATE users SET password = ? WHERE username = ?";

        try (PreparedStatement check = connection.prepareStatement(checkSql)) {
            check.setString(1, currentUsername);
            check.setString(2, current);
            if (!check.executeQuery().next()) {
                show("Contrasenya actual incorrecta.", "red");
                return;
            }

            try (PreparedStatement update = connection.prepareStatement(updateSql)) {
                update.setString(1, newPass);
                update.setString(2, currentUsername);
                update.executeUpdate();
                show("Contrasenya canviada correctament.", "green");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            show("Error en actualitzar la contrasenya.", "red");
        }
    }

    private void show(String msg, String color) {
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-text-fill: " + color + ";");
    }
}