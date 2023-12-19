package com.mycompany.gerenciadorPaginas.corePaginas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.gerenciadorPaginas.controle.GerenciadorPaginasController;
import com.mycompany.gerenciadorPaginas.core.Processo;

public class GerenciadorPaginasDeProcessos {

    private final GerenciadorPaginasController gerenciadorPaginasController = GerenciadorPaginasController.getInstancia();

    List<List<Pagina>> sequenciaPaginasNaMemoria = new ArrayList<>();
    private List<Integer> sequenciaPaginas = new ArrayList<>();
    Map<Integer, Pagina> paginaFalhadaPorInstante = new HashMap<>();

    public void atualizarPaginas(Processo processo, int instante) {
        int enderecoPagina = sequenciaPaginas.get(instante);

        boolean paginaExistente = sequenciaPaginas.indexOf(enderecoPagina) < instante;

        if (!paginaExistente) {
            gerenciadorPaginasController.criarPagina(enderecoPagina, processo);
        } else {
            gerenciadorPaginasController.acessarPagina(enderecoPagina, processo);
        }

        List<Pagina> paginasNaMemoria = gerenciadorPaginasController.getPaginasMemoria();
        sequenciaPaginasNaMemoria.add(paginasNaMemoria);
        Pagina paginaFalhada = gerenciadorPaginasController.getPaginaFalhada();
        if (paginaFalhada != null) {
            paginaFalhadaPorInstante.put(instante, paginaFalhada);
        }

        gerenciadorPaginasController.desenharPaginas(sequenciaPaginasNaMemoria, paginaFalhadaPorInstante);
    }

    public void adicionarSequenciaPaginas(List<Integer> sequenciasPaginas) {
        this.sequenciaPaginas = sequenciasPaginas;
    }

    public List<Integer> getFuturasPaginas() {
        int instanteAtual = sequenciaPaginasNaMemoria.size();

        return sequenciaPaginas.subList(instanteAtual, sequenciaPaginas.size());
    }

    public int getNumFalhas() {
        return paginaFalhadaPorInstante.size();
    }

    public String getSequenciaFalhas() {
        String texto = "";
        int paginaFalhada;

        int ultimoInstante = sequenciaPaginasNaMemoria.size();
        for (int instante = 0; instante < ultimoInstante; instante++) {
            if (paginaFalhadaPorInstante.containsKey(instante)) {
                texto += paginaFalhadaPorInstante.get(instante).getEndereco() + ", ";
            }
        }

        texto = texto.substring(0, texto.length() - 2);

        return texto;
    }
}
