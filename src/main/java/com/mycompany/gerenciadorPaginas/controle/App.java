package com.mycompany.gerenciadorPaginas.controle;

import java.io.IOException;

import com.mycompany.gerenciadorPaginas.core.DesenhoGrafico;
import com.mycompany.gerenciadorPaginas.core.Escalonador;
import com.mycompany.gerenciadorPaginas.corePaginas.GerenciadorMemoria;
import com.mycompany.gerenciadorPaginas.corePaginas.GerenciadorPaginasDeProcessos;
import com.mycompany.gerenciadorPaginas.fxmlController.View;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

/**
 * Classe principal que inicia a aplicação JavaFX Gerenciador Paginas.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/mycompany/gerenciadorPaginas/view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Gerenciador de Páginas");

        inicializarController(root, fxmlLoader.getController());
        
        adicionarListener(stage);

        stage.show();
    }
    
    private void inicializarController(Parent root, View view) {

        ApplicationController applicationController = ApplicationController.getInstancia();
        applicationController.setFXMLController(view);

        Canvas canvasGrafico = (Canvas) root.lookup("#canvasGrafico");
        GraphicsContext gc = canvasGrafico.getGraphicsContext2D();
        Canvas canvasGraficoPaginas = (Canvas) root.lookup("#canvasGraficoPaginas");
        GraphicsContext gcPaginas = canvasGraficoPaginas.getGraphicsContext2D();

        DesenhoGrafico desenhoGrafico = new DesenhoGrafico(gc, gcPaginas);
        applicationController.setDesenhoGrafico(desenhoGrafico);

        GerenciadorPaginasDeProcessos gerenciadorPaginasDeProcessos = new GerenciadorPaginasDeProcessos();
        applicationController.setGerenciadorPaginasDeProcessos(gerenciadorPaginasDeProcessos);

        GerenciadorMemoria gerenciadorMemoria = new GerenciadorMemoria();
        applicationController.setGerenciadorLogic(gerenciadorMemoria);

    }
    
    private void adicionarListener(Stage stage) {

        stage.setOnCloseRequest(event -> {
            if (Escalonador.getInstancia() != null && Escalonador.getInstancia().isAlive()) {
            	Escalonador.getInstancia().parar();
            }
            Platform.exit();
        });
    }
}
