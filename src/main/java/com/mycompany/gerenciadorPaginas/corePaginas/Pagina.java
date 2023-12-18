package com.mycompany.gerenciadorPaginas.corePaginas;

import java.util.Objects;

import com.mycompany.gerenciadorPaginas.core.Processo;

public class Pagina implements Cloneable {
	private int endereco;
	private Processo processo;
	private Integer molduraEndereco;
	
	public Pagina(int endereco, Processo processo) {
		this.endereco = endereco;
		this.processo = processo;
	}
	
	public Pagina(int endereco, Processo processo, int molduraEndereco) {
		this.endereco = endereco;
		this.processo = processo;
		this.molduraEndereco = molduraEndereco;
	}
	
	public void setProcesso(Processo processo) {
		this.processo = processo;
	}
	
	public Processo getProcesso() {
		return processo;
	}
	
	public int getEndereco() {
		return endereco;
	}

	public Integer getMolduraEndereco() {
		return molduraEndereco;
	}

	public void setMolduraEndereco(Integer molduraEndereco) {
		this.molduraEndereco = molduraEndereco;
	}

	@Override
	public int hashCode() {
		return Objects.hash(endereco);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pagina other = (Pagina) obj;
		return endereco == other.endereco;
	}
	
	@Override
    public Pagina clone() {
		return new Pagina(endereco, processo, molduraEndereco);
    }
}
