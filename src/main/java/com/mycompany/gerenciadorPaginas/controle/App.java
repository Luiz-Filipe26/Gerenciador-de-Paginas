package com.mycompany.gerenciadorPaginas.controle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashSet;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

/**
 * Classe principal que inicia a aplicação JavaFX Gerenciador Paginas.
 */
public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Carrega o arquivo FXML e configura a cena
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/mycompany/gerenciadorPaginas/view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Gerenciador Pagians");
        
        // Exibe o palco
        stage.show();
    }
}
 