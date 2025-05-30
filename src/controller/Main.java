package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principal que inicia l'aplicació.
 * 
 * 
 * @author Pol_Planas
 */
public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        showLogin();
    }

    /**
     * Carrega inicialment la vista login.
     */
    public void showLogin() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("Login - NOTED!");
        primaryStage.show();

        LoginController controller = loader.getController();
        controller.setMainApp(this);
        controller.setStage(primaryStage);
    }

    /**
     * Executa la vista principal segons si l'usuari és admin o no.
     *
     * @param username
     */
    public void showMainView(String username) throws Exception {
        FXMLLoader loader;

        if ("admin".equalsIgnoreCase(username)) {
            loader = new FXMLLoader(getClass().getResource("/view/AdminView.fxml"));
            primaryStage.setTitle("Admin - NOTED!");
        } else {
            loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
            primaryStage.setTitle("NOTED! - RATING EVERYTHING");
        }

        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}