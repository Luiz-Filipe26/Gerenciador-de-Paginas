package com.mycompany.gerenciadorPaginas.corePaginas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.gerenciadorPaginas.controle.ApplicationController;
import com.mycompany.gerenciadorPaginas.core.Processo;

public class GerenciadorPaginasDeProcessos {

    private final ApplicationController applicationController = ApplicationController.getInstancia();

    List<List<Pagina>> sequenciaPaginasNaMemoria = new ArrayList<>();
    private List<Integer> sequenciaPaginas = new ArrayList<>();
    Map<Integer, Pagina> paginaFalhadaPorInstante = new HashMap<>();

    public void atualizarPaginas(Processo processo, int instante) {
        if(instante > sequenciaPaginas.size()-1 ) {
            return;
        }

        int enderecoPagina = sequenciaPaginas.get(instante);

        boolean paginaExistente = sequenciaPaginas.indexOf(enderecoPagina) < instante;

        if (!paginaExistente) {
            applicationController.criarPagina(enderecoPagina, processo);
        } else {
            applicationController.acessarPagina(enderecoPagina, processo);
        }

        List<Pagina> paginasNaMemoria = applicationController.getPaginasMemoria();
        sequenciaPaginasNaMemoria.add(paginasNaMemoria);
        Pagina paginaFalhada = applicationController.getPaginaFalhada();
        if (paginaFalhada != null) {
            paginaFalhadaPorInstante.put(instante, paginaFalhada);
        }

        applicationController.desenharPaginas(sequenciaPaginasNaMemoria, paginaFalhadaPorInstante);
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
        StringBuilder texto = new StringBuilder();
        int paginaFalhada;

        int ultimoInstante = sequenciaPaginasNaMemoria.size();
        for (int instante = 0; instante < ultimoInstante; instante++) {
            if (paginaFalhadaPorInstante.containsKey(instante)) {
            	paginaFalhada = paginaFalhadaPorInstante.get(instante).getEndereco();
                texto.append(paginaFalhada + ", ");
            }
        }

        texto.setLength(texto.length() - 2);

        return texto.toString();
    }
    
}
