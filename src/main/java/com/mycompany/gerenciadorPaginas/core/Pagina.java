package com.mycompany.gerenciadorPaginas.core;

public class Pagina {
	private int endereco;
	private String nome;
	private int tempo;
	
	public Pagina(int endereco, String nome) {
		this.endereco = endereco;
		this.nome = nome;
		tempo = 0;
	}
	
	public int getEndereco() {
		return endereco;
	}
	
	public String getNome() {
		return nome;
	}
	public int getTempo() {
		return tempo;
	}
	
	public void adicionarTempo(int tempo) {
		this.tempo += tempo;
	}
	
	
}
