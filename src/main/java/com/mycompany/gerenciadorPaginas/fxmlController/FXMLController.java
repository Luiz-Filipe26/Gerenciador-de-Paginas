package com.mycompany.gerenciadorPaginas.fxmlController;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextField;

public class FXMLController implements Initializable{
	
    @FXML
    private Canvas canvasGrafico;

    @FXML
    private TextField labelNomeProcesso;

    @FXML
    void adicionarPagina(ActionEvent event) {

    }
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

}
