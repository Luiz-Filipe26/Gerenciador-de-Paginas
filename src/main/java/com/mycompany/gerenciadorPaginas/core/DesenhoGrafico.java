package com.mycompany.gerenciadorPaginas.core;

import java.util.List;
import java.util.Map;

import com.mycompany.gerenciadorPaginas.corePaginas.Pagina;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DesenhoGrafico {
	
	private List<List<Pagina>> sequenciaPaginasNaMemoria;
    private Map<Integer, Pagina> paginaFalhadaPorInstante;
	
    private int xInicial;
    private int yInicial;
    private int xOffset;
    private int yOffset;
    private int unidadeLargura;
    private int alturaLinha;
    private int margemDasLinhas;
    private int instanteInicio;
    private int tamanhoFonte;
    private GraphicsContext gc;
    private GraphicsContext gcPaginas;
    private Color[] cores;
    private double zoom;

    public DesenhoGrafico(GraphicsContext gc, GraphicsContext gcPaginas) {
        this.gc = gc;
        this.gcPaginas = gcPaginas;

        xInicial = 20;
        yInicial = 20;
        xOffset = 0;
        yOffset = 0;
        unidadeLargura = 30;
        alturaLinha = 30;
        margemDasLinhas = 10;
        tamanhoFonte = 16;
        instanteInicio = 0;
        zoom = 1;
        cores = new Color[]{Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.PINK, Color.CYAN,
            Color.MAGENTA, Color.LIGHTGRAY, Color.DARKGRAY, Color.GRAY, Color.BLACK, Color.WHITE,
            Color.LIGHTGRAY, Color.web("#FF5733"), Color.web("#4B0082")};

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        gcPaginas.setFill(Color.WHITE);
        gcPaginas.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

    }

    public void adicionarOffset(int xOffset, int yOffset) {
        this.xOffset += xOffset;
        this.yOffset += yOffset;
        repintarGrafico(Escalonador.getInstancia().getMapaInstantes(), Escalonador.getInstancia().getInstanteAtual());
        desenharPaginas();
    }

    public void darZoom(double fatorZoom) {
        this.zoom *= fatorZoom;
        repintarGrafico(Escalonador.getInstancia().getMapaInstantes(), Escalonador.getInstancia().getInstanteAtual());
        desenharPaginas();
    }

    public void resetarGrafico() {
        zoom = 1;
        instanteInicio = Escalonador.getInstancia().getInstanteAtual();
        repintarGrafico(Escalonador.getInstancia().getMapaInstantesLimpo(), instanteInicio);
        desenharPaginas();
    }

    public void repintarGrafico(Map<Processo, List<Integer>> processosExecutando, int instanteAtual) {
        gc.setFont(new Font("Serif", zoom * 14));

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        for (int i = Processo.listaDeProcessos.size() - 1, numProcesso = 0; i >= 0; i--, numProcesso++) {
            Processo processo = Processo.listaDeProcessos.get(i);
            List<Integer> instantesDeExecucao = processosExecutando.get(processo);

            if (instantesDeExecucao == null) {
                continue;
            }
            for (int instante = 0; instante <= instanteAtual; instante++) {
                if (!instantesDeExecucao.contains(instante)) {
                    continue;
                }
                desenharUnidadeDeBarra(cores[i % cores.length], numProcesso, instante - instanteInicio);
                if (instantesDeExecucao.get(0) == instante) {
                    desenharTexto(Color.BLACK, processo.getNome(), numProcesso);
                }
            }
        }
    }

    public void atualizarGrafico(Map<Processo, List<Integer>> processosExecutando, int instanteAtual) {
        
        for (int i = Processo.listaDeProcessos.size() - 1, numProcesso = 0; i >= 0; i--, numProcesso++) {
            Processo processo = Processo.listaDeProcessos.get(i);
            List<Integer> instantesDeExecucao = processosExecutando.get(processo);

            if (instantesDeExecucao != null && instantesDeExecucao.contains(instanteAtual)) {
                desenharUnidadeDeBarra(cores[i % cores.length], numProcesso, instanteAtual - instanteInicio);
                if (instantesDeExecucao.get(0) == instanteAtual) {
                    desenharTexto(Color.BLACK, processo.getNome(), numProcesso);
                }
            }
        }
    }

    private void desenharUnidadeDeBarra(Color barColor, int processo, int instante) {
        gc.setFill(barColor);
        gc.fillRect((xInicial + zoom * (xOffset + unidadeLargura * instante)),
                (yInicial + zoom * (yOffset + processo * (margemDasLinhas + alturaLinha))),
                unidadeLargura * zoom,
                alturaLinha * zoom);
    }

    private void desenharTexto(Color barColor, String nomeProcesso, int processo) {
        gc.setFill(barColor);
        gc.fillText(nomeProcesso, xInicial - 15, yInicial + zoom * (yOffset + (processo * (margemDasLinhas + alturaLinha) + alturaLinha / 2)));
    }
    
    
    public void desenharPaginas(List<List<Pagina>> sequenciaPaginasNaMemoria, Map<Integer, Pagina> paginaFalhadaPorInstante) {
    	this.sequenciaPaginasNaMemoria = sequenciaPaginasNaMemoria;
    	this.paginaFalhadaPorInstante = paginaFalhadaPorInstante;
    	desenharPaginas();
    }
    
    public void desenharPaginas() {
        gcPaginas.setFill(Color.WHITE);
        gcPaginas.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        int ultimaMolduraDaSequencia = sequenciaPaginasNaMemoria.size() - 1;
        int quantidadeMolduras = sequenciaPaginasNaMemoria.get(ultimaMolduraDaSequencia).size();
        for (int i = 0; i < quantidadeMolduras; i++) {
            desenharTextoMoldura(Color.BLACK, i);
        }

        for (int instanteIndice = 0; instanteIndice < sequenciaPaginasNaMemoria.size(); instanteIndice++) {
            List<Pagina> paginas = sequenciaPaginasNaMemoria.get(instanteIndice);

            for (int indicePagina = 0; indicePagina < paginas.size(); indicePagina++) {
                int enderecoMoldura = paginas.get(indicePagina).getMolduraEndereco();
                Color cor = obterCorPagina(paginaFalhadaPorInstante, instanteIndice, paginas.get(indicePagina));

                desenharUnidadeDeBarraPagina(cor, instanteIndice, enderecoMoldura);
                desenharTextoTarefa(Color.BLACK, paginas.get(indicePagina).getProcesso().getNome(), instanteIndice, enderecoMoldura, paginas.get(indicePagina).getEndereco());
            }
        }
    }

    private Color obterCorPagina(Map<Integer, Pagina> paginaFalhadaPorInstante, int instanteIndice, Pagina pagina) {
        return paginaFalhadaPorInstante.containsKey(instanteIndice)
                && paginaFalhadaPorInstante.get(instanteIndice).equals(pagina)
                ? Color.RED : Color.LIGHTGREEN;
    }

    private void desenharUnidadeDeBarraPagina(Color barColor, int instante, int pagina) {
        gcPaginas.setFill(barColor);
        gcPaginas.fillRect((xInicial + zoom * (xOffset + unidadeLargura * instante)),
                (yInicial + zoom * (yOffset + pagina * (margemDasLinhas + alturaLinha))),
                unidadeLargura * zoom,
                alturaLinha * zoom);
    }

    private void desenharTextoMoldura(Color barColor, int numMoldura) {
        double x = xInicial - 18;
        double y = yInicial + zoom * (yOffset + alturaLinha / 2 + numMoldura * (margemDasLinhas + alturaLinha));
        gcPaginas.setFill(Color.BLACK);
        gcPaginas.fillText("M" + (numMoldura + 1) + ": ", x, y);
    }

    private void desenharTextoTarefa(Color barColor, String nomeProcesso, int instante, int pagina, int enderecoPagina) {
        double x = xInicial + zoom * (xOffset + unidadeLargura * instante + 5);
        double y = yInicial + zoom * (yOffset + alturaLinha / 2 + pagina * (margemDasLinhas + alturaLinha));
        gcPaginas.setFill(barColor);
        gcPaginas.fillText(nomeProcesso + ": " + enderecoPagina, x, y);
    }
}
