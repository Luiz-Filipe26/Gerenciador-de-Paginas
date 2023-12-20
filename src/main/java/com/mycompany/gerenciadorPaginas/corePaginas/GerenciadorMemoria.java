package com.mycompany.gerenciadorPaginas.corePaginas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.mycompany.gerenciadorPaginas.controle.ApplicationController;
import com.mycompany.gerenciadorPaginas.core.Processo;

public class GerenciadorMemoria {
	private static GerenciadorMemoria gerenciadorMemoria;
	
    private String tipoAlocacao;

    private Pagina paginaFalhada;

    private List<Integer> enderecosPagina = new ArrayList<>();

    private List<Pagina> paginasDisco = new ArrayList<>();
    private Queue<Pagina> paginasMemoriaFila = new LinkedList<>();
    private List<Pagina> paginasMemoriaLista = new ArrayList<>();

    private Map<Integer, Integer> moldurasPorPagina = new HashMap<>();
    private List<Integer> moldurasDisponiveis = new ArrayList<>();

    
    public synchronized static GerenciadorMemoria getInstancia() {
    	if(gerenciadorMemoria == null) {
    		gerenciadorMemoria = new GerenciadorMemoria();
    	}
    	
    	return gerenciadorMemoria;
    }
    
    private GerenciadorMemoria() {
        
    }

    public void setTipoAlocacao(String tipoAlocacao) {
        this.tipoAlocacao = tipoAlocacao;
    }
    
    public String getTipoAlocacao() {
        return tipoAlocacao;
    }
    
    public void setNumMolduras(int numMolduras) {
        for (int i = 0; i < numMolduras; i++) {
            moldurasDisponiveis.add(i);
        }
    }
    
    public void resetarDados() {
    	resetarDados(getNumMolduras());
    }

	public int getNumMolduras() {
		return moldurasPorPagina.size() + moldurasDisponiveis.size();
	}
    
    public void resetarDados(int numMolduras) {
        
    	paginasDisco.clear();
        paginasMemoriaFila.clear();
        paginasMemoriaLista.clear();
        moldurasPorPagina.clear();
        moldurasDisponiveis.clear();
        
        setNumMolduras(numMolduras);
    }

    public List<Pagina> getPaginasMemoria() {
        List<Pagina> paginasMemoria = new ArrayList<>();

        if (tipoAlocacao.equals("FIFO")) {
            for (Pagina pagina : paginasMemoriaFila) {
                paginasMemoria.add(pagina.clone());
            }
        } else if (tipoAlocacao.equals("LRU") || tipoAlocacao.equals("ÓTIMO")) {
            for (Pagina pagina : paginasMemoriaLista) {
                paginasMemoria.add(pagina.clone());
            }
        }
        return paginasMemoria;
    }

    public Pagina getPaginaFalhada() {
        return paginaFalhada;
    }

    public void acessarPagina(int enderecoPagina, Processo processo) {
        Pagina pagina = buscarPagina(enderecoPagina);
        acessarPagina(pagina, processo);
    }
    
    private Pagina buscarPagina(int enderecoPagina) {
    	List<Pagina>[] colecoesPagina = new List[]{paginasDisco, new ArrayList<>(paginasMemoriaFila), paginasMemoriaLista};

        for (List<Pagina> colecaoPagina : colecoesPagina) {
            for (Pagina pagina : colecaoPagina) {
                if (pagina.getEndereco() == enderecoPagina) {
                    return pagina;
                }
            }
        }
        return null;
    }

    public Pagina criarPagina(int enderecoPagina, Processo processo) {
        enderecosPagina.add(enderecoPagina);
        Pagina pagina = new Pagina(enderecoPagina, processo);
        paginaFalhada = pagina;

        alocarPagina(pagina);

        return pagina;
    }

    public void acessarPagina(Pagina pagina, Processo processo) {
        pagina.setProcesso(processo);
        if (paginasDisco.remove(pagina)) {
            alocarPagina(pagina);
            paginaFalhada = pagina;
        } else {
            paginaFalhada = null;
            if (tipoAlocacao.equals("LRU")) {
                paginasMemoriaLista.remove(pagina);
                paginasMemoriaLista.add(0, pagina);
            }
        }
    }

    public void removerPagina(Pagina pagina) {
        int enderecoPagina = pagina.getEndereco();
        enderecosPagina.remove(enderecoPagina);

        if (!paginasDisco.remove(pagina)) {
            paginaFalhada = null;
            if (tipoAlocacao.equals("FIFO")) {
                paginasMemoriaFila.remove(pagina);
            } else {
                paginasMemoriaLista.remove(pagina);
            }
            int molduraEndereco = moldurasPorPagina.remove(enderecoPagina);
            moldurasDisponiveis.add(molduraEndereco);
            pagina.setMolduraEndereco(-1);
        } else {
            paginaFalhada = pagina;
        }
    }

    private void alocarPagina(Pagina pagina) {
        int enderecoPagina = pagina.getEndereco();

        if (!moldurasDisponiveis.isEmpty()) {
            if (tipoAlocacao.equals("FIFO")) {
                paginasMemoriaFila.add(pagina);
            } else {
                paginasMemoriaLista.add(0, pagina);
            }

            int molduraEndereco = moldurasDisponiveis.get(0);
            moldurasDisponiveis.remove(0);

            moldurasPorPagina.put(enderecoPagina, molduraEndereco);
            pagina.setMolduraEndereco(molduraEndereco);
        } else {
            Pagina paginaMoverParaDisco = null;
            if (tipoAlocacao.equals("FIFO")) {
                paginaMoverParaDisco = paginasMemoriaFila.poll();
            } else if (tipoAlocacao.equals("LRU")) {
                paginaMoverParaDisco = paginasMemoriaLista.remove(paginasMemoriaLista.size() - 1);
            } else if (tipoAlocacao.equals("ÓTIMO")) {
                paginaMoverParaDisco = getPaginaMoverOtimo();
                paginasMemoriaLista.remove(paginaMoverParaDisco);
            }
            int enderecoMover = paginaMoverParaDisco.getEndereco();
            paginasDisco.add(paginaMoverParaDisco);

            int molduraEndereco = moldurasPorPagina.remove(enderecoMover);
            if (tipoAlocacao.equals("FIFO")) {
                paginasMemoriaFila.add(pagina);
            } else {
                paginasMemoriaLista.add(0, pagina);
            }
            moldurasPorPagina.put(enderecoPagina, molduraEndereco);
            pagina.setMolduraEndereco(molduraEndereco);
        }
    }

    private Pagina getPaginaMoverOtimo() {
        List<Integer> futurasPaginas = ApplicationController.getInstancia().getFuturasPaginas();
        Pagina paginaMaisDistante = null;

        int maiorDistancia = -1;
        int distancia = 0;

        for (Pagina pagina : paginasMemoriaLista) {
            if (!futurasPaginas.contains(pagina.getEndereco())) {
                paginaMaisDistante = pagina;
                break;
            }
            distancia = futurasPaginas.indexOf(pagina.getEndereco());
            if (distancia > maiorDistancia) {
                maiorDistancia = distancia;
                paginaMaisDistante = pagina;
            }
        }

        return paginaMaisDistante;
    }
}
