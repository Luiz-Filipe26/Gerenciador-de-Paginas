package com.mycompany.gerenciadorPaginas.fxmlController;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JOptionPane;

import com.mycompany.gerenciadorPaginas.controle.GerenciadorPaginasController;
import com.mycompany.gerenciadorPaginas.core.Escalonador;
import com.mycompany.gerenciadorPaginas.core.Leitor;
import com.mycompany.gerenciadorPaginas.core.Processo;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class View implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button buttonAdicionarProcesso;

    @FXML
    private Button buttonAdicionarProcessosDeArquivo;

    @FXML
    private Button buttonBaixo;

    @FXML
    private Button buttonCima;

    @FXML
    private Button buttonDefinirEscalonamento;

    @FXML
    private Button buttonDireita;

    @FXML
    private Button buttonEscalonar;

    @FXML
    private Button buttonEsquerda;

    @FXML
    private Button buttonResetarGrafico;

    @FXML
    private Button buttonZoomIn;

    @FXML
    private Button buttonZoomOut;

    @FXML
    private Canvas canvasGrafico;

    @FXML
    private Canvas canvasGraficoPaginas;

    @FXML
    private ComboBox<String> comboBoxTipoAlocacao;

    @FXML
    private ComboBox<String> comboBoxTipoEscalonador;

    @FXML
    private ComboBox<String> comboBoxTipoTarefa;

    @FXML
    private Label labelMensagens;

    @FXML
    private Button buttonSequenciasPaginas;

    @FXML
    private ScrollPane scrollPaneConsole;

    @FXML
    private TextArea textAreaConsole;

    @FXML
    private TextArea textAreaConsolePaginas;

    @FXML
    private TextField textFieldDuracao;

    @FXML
    private TextField textFieldIngresso;

    @FXML
    private TextField textFieldNome;

    @FXML
    private TextField textFieldNumMolduras;
    
    @FXML
    private TextField textFieldNumProcessadores;
    
    @FXML
    private TextField textFieldPrioridade;

    @FXML
    private TextField textFieldQuantumPorProcesso;

    @FXML
    private TextField textFieldUnidadeQuantum;

    private final String CAMINHO_SOM_BOTAO_PRESSIONADO = "/com/mycompany/gerenciadorPaginas/botao_pressionado.wav";
    private final String CAMINHO_SOM_NOTIFICACAO = "/com/mycompany/gerenciadorPaginas/notificacao.wav";

    private String tipoEscalonador;

    private Node[] nodesDefinir;
    private Node[] nodesTarefa;
    private Node[] nodesBotaoGrafico;
    private Map<TextField, String> nomesProcessoPadrao = new HashMap<>();
    private Map<TextField, String> nomesEscalonamentoPadrao1 = new HashMap<>();
    private Map<TextField, String> nomesEscalonamentoPadrao2 = new HashMap<>();

    private Clip somBotaoPressionado;
    private Clip somNotificao;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        carregarAudios();

        inicializarColecoes();

        configurarVisibilidades();

        inicializarComboBoxes();

        criarFocusListeners();
    }

    private void carregarAudios() {
        try (AudioInputStream streamBotao = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(CAMINHO_SOM_BOTAO_PRESSIONADO)); AudioInputStream streamNotificacao = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(CAMINHO_SOM_NOTIFICACAO))) {

            somBotaoPressionado = AudioSystem.getClip();
            somBotaoPressionado.open(streamBotao);

            somNotificao = AudioSystem.getClip();
            somNotificao.open(streamNotificacao);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inicializarColecoes() {
        nodesDefinir = new Node[]{textFieldNumProcessadores, textFieldUnidadeQuantum, textFieldQuantumPorProcesso, textFieldNumMolduras, buttonDefinirEscalonamento};
        nodesTarefa = new Node[]{textFieldNome, textFieldIngresso, textFieldDuracao, textFieldPrioridade, buttonAdicionarProcesso, comboBoxTipoTarefa, buttonAdicionarProcessosDeArquivo};
        nodesBotaoGrafico = new Node[]{buttonCima, buttonBaixo, buttonEsquerda, buttonDireita, buttonZoomIn, buttonZoomOut};

        nomesProcessoPadrao.put(textFieldNome, textFieldNome.getText());
        nomesProcessoPadrao.put(textFieldIngresso, textFieldIngresso.getText());
        nomesProcessoPadrao.put(textFieldDuracao, textFieldDuracao.getText());
        nomesProcessoPadrao.put(textFieldPrioridade, textFieldPrioridade.getText());
        nomesProcessoPadrao.put(textFieldNumMolduras, textFieldNumMolduras.getText());

        nomesEscalonamentoPadrao1.put(textFieldNumProcessadores, textFieldNumProcessadores.getText());
        nomesEscalonamentoPadrao1.put(textFieldUnidadeQuantum, textFieldUnidadeQuantum.getText());
        nomesEscalonamentoPadrao1.put(textFieldQuantumPorProcesso, textFieldQuantumPorProcesso.getText());

        nomesEscalonamentoPadrao1.put(textFieldNumProcessadores, textFieldNumProcessadores.getText());
        nomesEscalonamentoPadrao1.put(textFieldUnidadeQuantum, textFieldUnidadeQuantum.getText());
    }

    private void configurarVisibilidades() {
        for (Node node : nodesTarefa) {
            node.setVisible(false);
        }

        for (Node node : nodesBotaoGrafico) {
            node.setDisable(true);
        }

        buttonEscalonar.setVisible(false);
        buttonResetarGrafico.setVisible(false);
        buttonSequenciasPaginas.setVisible(false);
    }

    private void inicializarComboBoxes() {
        ObservableList<String> listaTipoEscalonador = FXCollections.observableArrayList(
                "Round-Robin", "Shortest Remaining Time First", "Shortest Job First", "Escalonamento por Prioridade Cooperativo");
        ObservableList<String> listaTipoTarefa = FXCollections.observableArrayList(
                "Gerar String Aleatória", "Gerar Número Aleatório");
        ObservableList<String> listaTipoAlocacao = FXCollections.observableArrayList(
                "FIFO", "LRU", "ÓTIMO");

        comboBoxTipoEscalonador.setItems(listaTipoEscalonador);
        comboBoxTipoEscalonador.setValue(comboBoxTipoEscalonador.getItems().get(0));
        tipoEscalonador = comboBoxTipoEscalonador.getValue();

        comboBoxTipoTarefa.setItems(listaTipoTarefa);
        comboBoxTipoTarefa.setValue(comboBoxTipoTarefa.getItems().get(0));

        comboBoxTipoAlocacao.setItems(listaTipoAlocacao);
        comboBoxTipoAlocacao.setValue(comboBoxTipoAlocacao.getItems().get(0));

        comboBoxTipoEscalonador.valueProperty().addListener((o, ov, tipoEscalonador) -> {
            this.tipoEscalonador = tipoEscalonador;
            if (!tipoEscalonador.equals("Round-Robin")) {
                textFieldQuantumPorProcesso.setVisible(false);
            } else {
                textFieldQuantumPorProcesso.setVisible(true);
            }
        });
    }

    private void criarFocusListeners() {
        for (Node node : anchorPane.getChildren()) {
            if (node instanceof TextField) {
                ((TextField) node).setStyle("-fx-text-fill: lightgray;");
                criarFocusListener((TextField) node, ((TextField) node).getText());
            }
        }
    }

    private void criarFocusListener(TextField textField, String texto) {
        textField.focusedProperty().addListener((o, ov, isFocado) -> {
            if (isFocado) {
                if (textField.getText().equals(texto)) {
                    textField.setText("");
                }
                textField.setStyle("-fx-text-fill: black;");

            } else {
                if (textField.getText().equals("")) {
                    textField.setText(texto);
                    textField.setStyle("-fx-text-fill: lightgray;");
                }
            }
        });
    }

    public void adicionarTexto(String textoAdd) {
        Platform.runLater(() -> {
            textAreaConsole.appendText(textoAdd);

            if (!textAreaConsole.isFocused()) {
                scrollPaneConsole.setVvalue(1.0);
            }
        });
    }

    private void tocarSom(Clip som) {
        som.setFramePosition(0);
        som.start();
    }

    public void mostrarResultadoEscalonador(int numTrocasContexto, float tempoMedioExecucao, float tempoMedioEspera) {
        tocarSom(somNotificao);
        DecimalFormat formato = new DecimalFormat("0.00");

        String mensagem = "Número de trocas de contexto: " + numTrocasContexto
                + "\nTempo médio de execução: " + formato.format(tempoMedioExecucao) + "s"
                + "\nTempo médio de espera: " + formato.format(tempoMedioEspera) + "s"
                + "\nNúmero de falhas: " + GerenciadorPaginasController.getInstancia().getNumFalhas();

        Platform.runLater(() -> {
            textAreaConsolePaginas.appendText(mensagem + "\n Sequência de páginas falhadas: " + GerenciadorPaginasController.getInstancia().getSequenciaFalhas());
        });

        JOptionPane.showMessageDialog(null, mensagem, "Resultado do escalonamento", JOptionPane.INFORMATION_MESSAGE);
    }

    @FXML
    public void buttonAdicionarProcessosDeArquivoPressionado(ActionEvent event) {
        tocarSom(somBotaoPressionado);

        Leitor leitor = new Leitor();
        ArrayList<List<String>> processos = leitor.lerArquivos();
        if (processos == null || processos.isEmpty()) {
            labelMensagens.setText("Nenhum processo válido encontrado para adicionar!");
            return;
        }

        StringBuilder mensagem = new StringBuilder("Processos ");

        for (List<String> processo : processos) {
            Processo.addProcesso(processo.get(0), Integer.valueOf(processo.get(1)), Integer.valueOf(processo.get(2)), Integer.valueOf(processo.get(3)), Integer.valueOf(processo.get(4)));
            mensagem.append(processo.get(0) + ", ");
        }

        mensagem.setLength(mensagem.length() - 2);
        mensagem.append(" adicionados com sucesso!");

        labelMensagens.setText(mensagem.toString());
        buttonSequenciasPaginas.setVisible(true);
    }

    @FXML
    void buttonSequenciaPaginasPressionado(ActionEvent event) {
        tocarSom(somBotaoPressionado);

        Leitor leitor = new Leitor();
        List<Integer> sequenciasPaginas = leitor.lerArquivoPaginas();
        if (sequenciasPaginas == null || sequenciasPaginas.isEmpty()) {
            labelMensagens.setText("Nenhum página válida encontrada para adicionar!");
            return;
        }

        if (getDuracaoProcessosTotal() < sequenciasPaginas.size()) {
            int duracaoFaltante = sequenciasPaginas.size() - getDuracaoProcessosTotal();
            labelMensagens.setText("A duração dos processos é menor que a de páginas! Falta: " + duracaoFaltante + " de duração.");
            return;
        }

        labelMensagens.setText("Sequência de " + sequenciasPaginas.size() + " páginas adicionadas com sucesso!");
        buttonEscalonar.setVisible(true);

        GerenciadorPaginasController.getInstancia().adicionarSequenciaPaginas(sequenciasPaginas);
    }

    private int getDuracaoProcessosTotal() {
        int duracaoTotal = 0;

        List<Processo> processos = Processo.listaDeProcessos;

        for (Processo processo : processos) {
            duracaoTotal += processo.getDuracao();
        }

        return duracaoTotal;
    }

    @FXML
    public void buttonAdicionarProcessoPressionado(ActionEvent event) {
        tocarSom(somBotaoPressionado);

        if (!isEntradasValidas(nomesProcessoPadrao)) {
            resetarCampos(nomesProcessoPadrao);
            return;
        }

        adicionarProcesso();
        buttonSequenciasPaginas.setVisible(true);

        resetarCampos(nomesProcessoPadrao);
    }

    private void adicionarProcesso() {
        String nome = textFieldNome.getText().trim();
        int ingresso = Integer.parseInt(textFieldIngresso.getText());
        int duracao = Integer.parseInt(textFieldDuracao.getText());
        int prioridade = Integer.parseInt(textFieldPrioridade.getText());
        int tipoTarefa = comboBoxTipoTarefa.getValue().equals("Gerar String Aleatória") ? 1 : 2;

        Processo.addProcesso(nome, tipoTarefa, ingresso, duracao, prioridade);
        labelMensagens.setText("Processo " + nome + " inserido com sucesso!");
    }

    @FXML
    public void buttonDefinirEscalonamentoPressionado(ActionEvent event) {
        tocarSom(somBotaoPressionado);

        Map<TextField, String> entradas = tipoEscalonador.equals("Round-Robin") ? nomesEscalonamentoPadrao1 : nomesEscalonamentoPadrao2;
        if (!isEntradasValidas(entradas)) {
            resetarCampos(entradas);
            return;
        }

        definirEscalonamento();
        GerenciadorPaginasController.getInstancia().setTipoAlocao(comboBoxTipoAlocacao.getValue());
        GerenciadorPaginasController.getInstancia().setNumMolduras(Integer.parseInt(textFieldNumMolduras.getText()));

        atualizarVisibilidadesEscalonamento();
        labelMensagens.setText("");
    }

    private void definirEscalonamento() {
        int numProcessadores = Integer.parseInt(textFieldNumProcessadores.getText());
        float unidadeQuantum = Float.parseFloat(textFieldUnidadeQuantum.getText());

        if (tipoEscalonador.equals("Round-Robin")) {
            int quantumPorProcesso = Integer.parseInt(textFieldQuantumPorProcesso.getText());
            Escalonador.getInstancia(tipoEscalonador, numProcessadores, unidadeQuantum, quantumPorProcesso);
        } else {
            Escalonador.getInstancia(tipoEscalonador, numProcessadores, unidadeQuantum);
        }
    }

    private void atualizarVisibilidadesEscalonamento() {
        for (Node node : nodesDefinir) {
            node.setVisible(false);
        }
        for (Node node : nodesTarefa) {
            node.setVisible(true);
        }
    }

    private void resetarCampos(Map<TextField, String> entradas) {
        entradas.forEach((textField, nomeTexto) -> {
            textField.setText(nomeTexto);
            textField.requestFocus();
            anchorPane.requestFocus();
            textFieldNome.setStyle("-fx-text-fill: lightgray;");
        });
    }

    private boolean isEntradasValidas(Map<TextField, String> entradas) {
        List<String> camposInvalidos = new ArrayList<>();

        entradas.forEach((textField, nomeCampo) -> {
            if (textField == textFieldUnidadeQuantum) {
                if (!isNumeroFloatValido(textField, nomeCampo)) {
                    camposInvalidos.add(nomeCampo);
                }
            } else if (textField != textFieldNome && !isNumeroInteiroValido(textField, nomeCampo)) {
                camposInvalidos.add(nomeCampo);
            }
        });

        if (camposInvalidos.isEmpty()) {
            return true;
        } else {
            String mensagemErro = "Campos " + String.join(", ", camposInvalidos) + " inválidos!";
            labelMensagens.setText(mensagemErro);
            return false;
        }
    }

    private boolean isNumeroInteiroValido(TextField textField, String nomeCampo) {
        String texto = textField.getText().trim();

        if (!texto.isEmpty()) {
            try {
                Integer.parseInt(texto);
                return true;
            } catch (NumberFormatException e) {
                textField.setText(nomeCampo);
                return false;
            }
        } else {
            textField.setText(nomeCampo);
            return false;
        }
    }

    private boolean isNumeroFloatValido(TextField textField, String nomeCampo) {
        String texto = textField.getText().trim();

        if (!texto.isEmpty()) {
            try {
                Float.parseFloat(texto);
                return true;
            } catch (NumberFormatException e) {
                textField.setText(nomeCampo);
                return false;
            }
        } else {
            textField.setText(nomeCampo);
            return false;
        }
    }

    @FXML
    public void buttonEscalonarPressionado(ActionEvent event) {
        tocarSom(somBotaoPressionado);

        buttonResetarGrafico.setVisible(true);
        if (!Escalonador.getInstancia().isAlive()) {
            Escalonador.getInstancia().start();
        } else if (Escalonador.getInstancia().isDormindo()) {
            Escalonador.getInstancia().acorda();
        }
        if (nodesBotaoGrafico[0].isDisabled()) {
            for (Node node : nodesBotaoGrafico) {
                node.setDisable(false);
            }
        }
    }

    @FXML
    public void buttonBaixoPressionado(ActionEvent event) {
        GerenciadorPaginasController.getInstancia().adicionarOffset(0, -40);
    }

    @FXML
    public void buttonCimaPressionado(ActionEvent event) {
        GerenciadorPaginasController.getInstancia().adicionarOffset(0, 40);
    }

    @FXML
    public void buttonEsquerdaPressionado(ActionEvent event) {
        GerenciadorPaginasController.getInstancia().adicionarOffset(30, 0);
    }

    @FXML
    public void buttonDireitaPressionado(ActionEvent event) {
        GerenciadorPaginasController.getInstancia().adicionarOffset(-30, -0);
    }

    @FXML
    public void buttonResetarGrafico(ActionEvent event) {
        tocarSom(somBotaoPressionado);
        GerenciadorPaginasController.getInstancia().resetarGrafico();
    }

    @FXML
    public void buttonZoomInPressionado(ActionEvent event) {
        GerenciadorPaginasController.getInstancia().darZoom(1.1);
    }

    @FXML
    public void buttonZoomOutPressionado(ActionEvent event) {
        GerenciadorPaginasController.getInstancia().darZoom(1 / 1.1);
    }

}
