package com.mycompany.gerenciadorPaginas.corePaginas;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ResultadoTipoAlocacao {
	private String tipoAlocacao;
	
	private Map<Integer, Integer> numFalhasPorNumMolduras = new HashMap<>();
	
	public ResultadoTipoAlocacao(String tipoAlocacao) {
		this.tipoAlocacao = tipoAlocacao;
	}
	
	public String getTipoAlocacao() {
		return tipoAlocacao;
	}
	
	public void adicionarFalha(int numMolduras, int numFalhas) {
		numFalhasPorNumMolduras.put(numMolduras, numFalhas);
	}
	
	public int[][] getNumFalhasPorNumMolduras() {
		int tam = numFalhasPorNumMolduras.size();
		int[][] retorno = new int[tam][2];
		
		int[] index = {0};
		
		numFalhasPorNumMolduras.forEach((numMolduras, numFalhas) -> {
	       retorno[index[0]][0] = numMolduras;
	       retorno[index[0]][1] = numFalhas;
	       index[0]++;
	    });
		
		Arrays.sort(retorno, (a, b) -> Integer.compare(a[0], b[0]));
		
		return retorno;
	}
}
