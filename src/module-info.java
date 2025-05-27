module PlanasPol_ProjecteFinal {
    requires javafx.controls;
    requires javafx.fxml;
	requires java.sql;

    opens controller to javafx.fxml;
    exports controller;
}
