package com.mycompany.gerenciadorPaginas.core;

import java.util.Random;

import com.mycompany.gerenciadorPaginas.controle.GerenciadorPaginasController;

public class TarefaNumeroAleatorio extends TarefaAbstrata {
	private GerenciadorPaginasController gerenciadorPaginasController;

    public TarefaNumeroAleatorio(int _idProcesso, String _nomeProcesso) {
        super(_idProcesso, _nomeProcesso);
        this.gerenciadorPaginasController = GerenciadorPaginasController.getInstancia();
    }

    public void run() {
        pronto = true;
        if (novo) {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        
        gerenciadorPaginasController.adicionarTexto("Começo da execução da Thread " + idProcesso + "\n");

        Random random = new Random();

        while (!encerrado) {
            String randomNumberString = gerarNumeroAleatorio(random, 4);
            gerenciadorPaginasController.adicionarTexto(nomeProcesso + ": " + randomNumberString + "\n");
            
            if (pronto) {
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String gerarNumeroAleatorio(Random random, int length) {
        StringBuilder numeroAleatorio = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomNum = random.nextInt(10); // Gera números aleatórios entre 0 e 9
            numeroAleatorio.append(randomNum);
            if (i < length - 1) {
                numeroAleatorio.append(" ");
            }
        }

        return numeroAleatorio.toString();
    }
}