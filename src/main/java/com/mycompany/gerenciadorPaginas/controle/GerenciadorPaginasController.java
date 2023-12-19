package com.mycompany.gerenciadorPaginas.controle;

import java.util.List;
import java.util.Map;

import com.mycompany.gerenciadorPaginas.core.DesenhoGrafico;
import com.mycompany.gerenciadorPaginas.core.Processo;
import com.mycompany.gerenciadorPaginas.corePaginas.GerenciadorLogic;
import com.mycompany.gerenciadorPaginas.corePaginas.GerenciadorPaginasDeProcessos;
import com.mycompany.gerenciadorPaginas.corePaginas.Pagina;
import com.mycompany.gerenciadorPaginas.fxmlController.View;

public class GerenciadorPaginasController {

    private static GerenciadorPaginasController gerenciadorPaginasController;
    private View view;
    private DesenhoGrafico desenhoGrafico;
    private GerenciadorPaginasDeProcessos gerenciadorPaginasDeProcessos;
    private GerenciadorLogic gerenciadorLogic;

    public synchronized static GerenciadorPaginasController getInstancia() {

        if (gerenciadorPaginasController == null) {
            gerenciadorPaginasController = new GerenciadorPaginasController();
        }

        return gerenciadorPaginasController;
    }

    private GerenciadorPaginasController() {

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

    public void setGerenciadorLogic(GerenciadorLogic gerenciadorLogic) {
        this.gerenciadorLogic = gerenciadorLogic;
    }

    public void setTipoAlocao(String tipoAlocacao) {
        gerenciadorLogic.setTipoAlocacao(tipoAlocacao);
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
        return gerenciadorLogic.criarPagina(enderecoPagina, processo);
    }

    public void acessarPagina(int enderecoPagina, Processo processo) {
        gerenciadorLogic.acessarPagina(enderecoPagina, processo);
    }

    public void acessarPagina(Pagina pagina, Processo processo) {
        gerenciadorLogic.acessarPagina(pagina, processo);
    }

    public void removerPagina(Pagina pagina) {
        gerenciadorLogic.removerPagina(pagina);
    }

    public void adicionarSequenciaPaginas(List<Integer> sequenciasPaginas) {
        gerenciadorPaginasDeProcessos.adicionarSequenciaPaginas(sequenciasPaginas);
    }

    public void desenharPaginas(List<List<Pagina>> sequenciaPaginasNaMemoria, Map<Integer, Pagina> paginaFalhadaPorInstante) {
        desenhoGrafico.desenharPaginas(sequenciaPaginasNaMemoria, paginaFalhadaPorInstante);
    }

    public List<Pagina> getPaginasMemoria() {
        return gerenciadorLogic.getPaginasMemoria();
    }

    public void atualizarPaginas(Processo processo, int instante) {
        gerenciadorPaginasDeProcessos.atualizarPaginas(processo, instante);
    }

    public Pagina getPaginaFalhada() {
        return gerenciadorLogic.getPaginaFalhada();
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
        gerenciadorLogic.setNumMolduras(numMolduras);
    }
}
