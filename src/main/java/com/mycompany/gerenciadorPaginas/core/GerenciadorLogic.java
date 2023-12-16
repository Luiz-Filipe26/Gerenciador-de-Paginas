package com.mycompany.gerenciadorPaginas.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

public class GerenciadorLogic {

	private List<Integer> enderecosPagina = new ArrayList<>();

	private List<Pagina> paginasDisco = new ArrayList<>();
	private Queue<Pagina> paginasMemoria = new LinkedList<>();

	private Map<Integer, Integer> moldurasPorPagina = new HashMap<>();
	private List<Integer> moldurasDisponiveis = new ArrayList<>();
	
	public GerenciadorLogic() {
		for(int i=0; i<6; i++) {
			moldurasDisponiveis.add(i);
		}
	}
	
	public void adicionarPagina(String nome) {
		int enderecoPagina = gerarEnderecoPagina();
		enderecosPagina.add(enderecoPagina);
		Pagina pagina = new Pagina(enderecoPagina, nome);
		
		alocarPagina(pagina);
	}

	
	public void acessarPagina(Pagina pagina) {
		if(paginasDisco.remove(pagina)) {
			alocarPagina(pagina);
		}
	}
	
	public void removerPagina(Pagina pagina) {
		int enderecoPagina = pagina.getEndereco();
		enderecosPagina.remove(enderecoPagina);
		
		if(!paginasDisco.remove(pagina)) {
			paginasMemoria.remove(pagina);
			int molduraEndereco = moldurasPorPagina.remove(enderecoPagina);
			moldurasDisponiveis.add(molduraEndereco);
		}
	}
	
	private void alocarPagina(Pagina pagina) {
		int enderecoPagina = pagina.getEndereco();
		
		if(moldurasDisponiveis.size() > 0) {
			paginasMemoria.add(pagina);
			
			int molduraEndereco = moldurasDisponiveis.get(0);
			moldurasDisponiveis.remove(0);
			
			moldurasPorPagina.put(enderecoPagina, molduraEndereco);
		}
		else {
			Pagina paginaMover = paginasMemoria.poll();
			int enderecoMover = paginaMover.getEndereco();
			paginasDisco.add(paginaMover);

			int enderecoMoldura = moldurasPorPagina.remove(enderecoMover);
			
			paginasMemoria.add(pagina);
			moldurasDisponiveis.add(enderecoMoldura);
			moldurasPorPagina.put(enderecoPagina, enderecoMoldura);
		}
	}
	
	private int gerarEnderecoPagina() {
		Random r = new Random();
		int endereco = 0;
		
		do {
			endereco = r.nextInt(30);
		} while(enderecosPagina.contains(endereco));
		
		return endereco;
	}
}
