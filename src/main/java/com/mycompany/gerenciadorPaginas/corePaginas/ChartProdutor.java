
package com.mycompany.gerenciadorPaginas.corePaginas;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.gerenciadorPaginas.controle.ApplicationController;
import com.mycompany.gerenciadorPaginas.core.Processo;


public class ChartProdutor {
    private ApplicationController applicationController = ApplicationController.getInstancia();
    
    public List<ResultadoTipoAlocacao> obterResultadosGrafico() {
    	String tiposAlocacao[] = {"FIFO", "LRU", "ÓTIMO"};

    	int numMoldurasAnterior = applicationController.getNumMolduras();
    	String tipoAlocacaoAnterior = applicationController.getTipoAlocao();
    	
    	int numInstantes = applicationController.getNumPaginas();
    	
    	List<ResultadoTipoAlocacao> resultados = new ArrayList<>();
    	
    	
    	for(String tipoAlocacao : tiposAlocacao) {
    		ResultadoTipoAlocacao resultado = new ResultadoTipoAlocacao(tipoAlocacao);
	    		
    		for(int numMolduras=1; numMolduras<=5; numMolduras++) {
	    		int numFalhas = getNumFalhas(tipoAlocacao, numMolduras, numInstantes);
	    		resultado.adicionarFalha(numMolduras, numFalhas);
    		}
    		resultados.add(resultado);
    		
    	}
    	

		applicationController.resetarDados(numMoldurasAnterior);
    	applicationController.setTipoAlocao(tipoAlocacaoAnterior);
    	
    	
    	return resultados;
    }
    
    private int getNumFalhas(String tipoAlocacao, int numMolduras, int numInstantes) {

    	Processo processoNulo = new Processo();
    	
    	applicationController.resetarDados(numMolduras);
    	applicationController.setTipoAlocao(tipoAlocacao);
    	
    	for(int instante=0; instante<numInstantes; instante++) {
    		applicationController.atualizarPaginas(processoNulo, instante);
    	}
    	
    	return applicationController.getNumFalhas();
    }
}
