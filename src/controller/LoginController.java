package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;

/**
 * Controlador per gestionar el procés d'autenticació d'usuari.
 * 
 * @author Pol_Planas
 */
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private Connection connection;
    private Stage stage;
    private Main mainApp;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Inicialitza el controlador i estableix la connexió amb la base de dades.
     */
    @FXML
    private void initialize() {
        connectToDatabase();
    }

    /**
     * Estableix la connexió amb la base de dades SQLite.
     */
    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:noted.db");
        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("No s'ha pogut connectar a la base de dades.");
        }
    }

    /**
     * Gestiona l'acció d'inici de sessió.
     * Valida que els camps no estiguin buits i comprova les credencials a la base de dades.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Tots els camps són obligatoris.");
            return;
        }

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Login correcte
                errorLabel.setText("");

                if (mainApp != null) {
                    try {
                        mainApp.showMainView(username); // ✅ Passa el nom d'usuari
                    } catch (Exception e) {
                        e.printStackTrace();
                        errorLabel.setText("No s'ha pogut carregar la vista.");
                    }
                }

            } else {
                errorLabel.setText("Credencials incorrectes.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Error de connexió.");
        }
    }
}