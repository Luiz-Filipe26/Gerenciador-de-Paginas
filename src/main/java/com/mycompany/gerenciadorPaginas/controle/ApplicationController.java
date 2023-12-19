package com.mycompany.gerenciadorPaginas.controle;

import java.util.List;
import java.util.Map;

import com.mycompany.gerenciadorPaginas.core.DesenhoGrafico;
import com.mycompany.gerenciadorPaginas.core.Processo;
import com.mycompany.gerenciadorPaginas.corePaginas.GerenciadorMemoria;
import com.mycompany.gerenciadorPaginas.corePaginas.GerenciadorPaginasDeProcessos;
import com.mycompany.gerenciadorPaginas.corePaginas.Pagina;
import com.mycompany.gerenciadorPaginas.fxmlController.View;

public class ApplicationController {

    private static ApplicationController applicationController;
    private View view;
    private DesenhoGrafico desenhoGrafico;
    private GerenciadorPaginasDeProcessos gerenciadorPaginasDeProcessos;
    private GerenciadorMemoria gerenciadorMemoria;

    public synchronized static ApplicationController getInstancia() {

        if (applicationController == null) {
            applicationController = new ApplicationController();
        }

        return applicationController;
    }

    private ApplicationController() {

    }

    public void setFXMLController(View view) {
        this.view = view;
    }

    public void setDesenhoGrafico(DesenhoGrafico desenhoGrafico) {
        this.desenhoGrafico = desenhoGrafico;
    }

    public void setGerenciadorPaginasDeProcessos(GerenciadorPaginasDeProcessos gerenciadorPaginasDeProcessos) {
        this.gerenciadorPaginasDeProcessos = gerenciadorPaginasDeProcessos;
    }

    public void setGerenciadorLogic(GerenciadorMemoria gerenciadorMemoria) {
        this.gerenciadorMemoria = gerenciadorMemoria;
    }

    public void setTipoAlocao(String tipoAlocacao) {
        gerenciadorMemoria.setTipoAlocacao(tipoAlocacao);
    }

    public void adicionarOffset(int xOffset, int yOffset) {
        desenhoGrafico.adicionarOffset(xOffset, yOffset);
    }

    public void darZoom(double fatorZoom) {
        desenhoGrafico.darZoom(fatorZoom);
    }

    public void resetarGrafico() {
        desenhoGrafico.resetarGrafico();
    }

    public void adicionarTexto(String texto_add) {
        view.adicionarTexto(texto_add);
    }

    public void mostrarResultadoEscalonador(int numTrocasContexto, float tempoMedioExecucao, float tempoMedioEspera) {
        view.mostrarResultadoEscalonador(numTrocasContexto, tempoMedioExecucao, tempoMedioEspera);
    }

    public void repintarGrafico(Map<Processo, List<Integer>> processosExecutando, int instanteAtual) {
        desenhoGrafico.repintarGrafico(processosExecutando, instanteAtual);
    }

    public void atualizarGrafico(Map<Processo, List<Integer>> mapaDeInstantes, int instanteAtual) {
        desenhoGrafico.atualizarGrafico(mapaDeInstantes, instanteAtual);
    }

    public Pagina criarPagina(int enderecoPagina, Processo processo) {
        return gerenciadorMemoria.criarPagina(enderecoPagina, processo);
    }

    public void acessarPagina(int enderecoPagina, Processo processo) {
        gerenciadorMemoria.acessarPagina(enderecoPagina, processo);
    }

    public void acessarPagina(Pagina pagina, Processo processo) {
        gerenciadorMemoria.acessarPagina(pagina, processo);
    }

    public void removerPagina(Pagina pagina) {
        gerenciadorMemoria.removerPagina(pagina);
    }

    public void adicionarSequenciaPaginas(List<Integer> sequenciasPaginas) {
        gerenciadorPaginasDeProcessos.adicionarSequenciaPaginas(sequenciasPaginas);
    }

    public void desenharPaginas(List<List<Pagina>> sequenciaPaginasNaMemoria, Map<Integer, Pagina> paginaFalhadaPorInstante) {
        desenhoGrafico.desenharPaginas(sequenciaPaginasNaMemoria, paginaFalhadaPorInstante);
    }

    public List<Pagina> getPaginasMemoria() {
        return gerenciadorMemoria.getPaginasMemoria();
    }

    public void atualizarPaginas(Processo processo, int instante) {
        gerenciadorPaginasDeProcessos.atualizarPaginas(processo, instante);
    }

    public Pagina getPaginaFalhada() {
        return gerenciadorMemoria.getPaginaFalhada();
    }

    public List<Integer> getFuturasPaginas() {
        return gerenciadorPaginasDeProcessos.getFuturasPaginas();
    }

    public int getNumFalhas() {
        return gerenciadorPaginasDeProcessos.getNumFalhas();
    }

    public String getSequenciaFalhas() {
        return gerenciadorPaginasDeProcessos.getSequenciaFalhas();
    }
    
    public void setNumMolduras(int numMolduras) {
        gerenciadorMemoria.setNumMolduras(numMolduras);
    }
}
