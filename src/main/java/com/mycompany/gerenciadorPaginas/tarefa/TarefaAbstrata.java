package com.mycompany.gerenciadorPaginas.tarefa;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TarefaAbstrata extends Thread {

    protected int idProcesso;
    protected String nomeProcesso;
    protected boolean novo;
    protected boolean pronto;
    protected boolean encerrado;
    protected Lock lock;

    public TarefaAbstrata(int _idProcesso, String _nomeProcesso) {
        this.idProcesso = _idProcesso;
        this.nomeProcesso = _nomeProcesso;
        this.novo = true;
        this.pronto = false;
        this.encerrado = false;
        this.lock = new ReentrantLock();
    }

    public void preemptar() {
        pronto = true;
    }

    public void encerrarTarefa() {
        encerrado = true;
    }

    public void iniciar() {
        novo = false;
        pronto = false;
        synchronized (lock) {
            lock.notify();
        }
    }

    public void rodar() {
        pronto = false;
        synchronized (lock) {
            lock.notify();
        }
    }
}
