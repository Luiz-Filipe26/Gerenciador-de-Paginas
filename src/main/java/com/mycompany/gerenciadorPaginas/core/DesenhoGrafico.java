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

    private final Color[] CORES = new Color[]{
    		Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.PINK, Color.CYAN,
            Color.MAGENTA, Color.LIGHTGRAY, Color.DARKGRAY, Color.GRAY, Color.BLACK, Color.WHITE,
            Color.LIGHTGRAY, Color.web("#FF5733"), Color.web("#4B0082")};;
    
    private final int X_INICIAl = 20;
    private final int Y_INICIAL = 20;
    private final int UNIDADE_LARGURA = 30;
    private final int ALTURA_LINHA = 30;
    private final int MARGEM_DAS_LINHAS = 10;
    private final int TAMANHO_FONTE = 14;
    private double zoom;
    private final GraphicsContext gc;
    private final GraphicsContext gcPaginas;
    private int xOffset;
    private int yOffset;
    private int instanteInicio;

    public DesenhoGrafico(GraphicsContext gc, GraphicsContext gcPaginas) {
        this.gc = gc;
        this.gcPaginas = gcPaginas;

        xOffset = 0;
        yOffset = 0;
        instanteInicio = 0;
        zoom = 1;

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
        gc.setFont(new Font("Serif", zoom * TAMANHO_FONTE));

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
                desenharUnidadeDeBarra(CORES[i % CORES.length], numProcesso, instante - instanteInicio);
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
                desenharUnidadeDeBarra(CORES[i % CORES.length], numProcesso, instanteAtual - instanteInicio);
                if (instantesDeExecucao.get(0) == instanteAtual) {
                    desenharTexto(Color.BLACK, processo.getNome(), numProcesso);
                }
            }
        }
    }

    private void desenharUnidadeDeBarra(Color barColor, int processo, int instante) {
        gc.setFill(barColor);
        gc.fillRect((X_INICIAl + zoom * (xOffset + UNIDADE_LARGURA * instante)),
                (Y_INICIAL + zoom * (yOffset + processo * (MARGEM_DAS_LINHAS + ALTURA_LINHA))),
                UNIDADE_LARGURA * zoom,
                ALTURA_LINHA * zoom);
    }

    private void desenharTexto(Color barColor, String nomeProcesso, int processo) {
        gc.setFill(barColor);
        gc.fillText(nomeProcesso, X_INICIAl - 15, Y_INICIAL + zoom * (yOffset + (processo * (MARGEM_DAS_LINHAS + ALTURA_LINHA) + ALTURA_LINHA / 2)));
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
        gcPaginas.fillRect((X_INICIAl + zoom * (xOffset + UNIDADE_LARGURA * instante)),
                (Y_INICIAL + zoom * (yOffset + pagina * (MARGEM_DAS_LINHAS + ALTURA_LINHA))),
                UNIDADE_LARGURA * zoom,
                ALTURA_LINHA * zoom);
    }

    private void desenharTextoMoldura(Color barColor, int numMoldura) {
        double x = X_INICIAl - 18;
        double y = Y_INICIAL + zoom * (yOffset + ALTURA_LINHA / 2 + numMoldura * (MARGEM_DAS_LINHAS + ALTURA_LINHA));
        gcPaginas.setFill(Color.BLACK);
        gcPaginas.fillText("M" + (numMoldura + 1) + ": ", x, y);
    }

    private void desenharTextoTarefa(Color barColor, String nomeProcesso, int instante, int pagina, int enderecoPagina) {
        double x = X_INICIAl + zoom * (xOffset + UNIDADE_LARGURA * instante + 5);
        double y = Y_INICIAL + zoom * (yOffset + ALTURA_LINHA / 2 + pagina * (MARGEM_DAS_LINHAS + ALTURA_LINHA));
        gcPaginas.setFill(barColor);
        gcPaginas.fillText(nomeProcesso + ": " + enderecoPagina, x, y);
    }
}
