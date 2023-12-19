package com.mycompany.gerenciadorPaginas.core;

import java.util.ArrayList;

import com.mycompany.gerenciadorPaginas.tarefa.TarefaAbstrata;
import com.mycompany.gerenciadorPaginas.tarefa.TarefaNumeroAleatorio;
import com.mycompany.gerenciadorPaginas.tarefa.TarefaStringAleatoria;

public class Processo {

    public static final ArrayList<Processo> listaDeProcessos = new ArrayList<>();
    public static int ultimo_id;
    private int id;
    private String nome;
    private int ingresso;
    private int duracao;
    private int prioridade;
    private int quantum_restante;
    private int tempo_restante;
    private TarefaAbstrata tarefa;
    private boolean novo;
    private boolean pronto;
    private boolean iniciado;
    private boolean encerrado;
    private int tipoTarefa;

    public static void addProcesso(String _nome, int _tipoTarefa, int _ingresso, int _duracao, int _prioridade) {
        Escalonador.atualizaListaDeProcessos(
                new Processo(_nome, _tipoTarefa, _ingresso, _duracao, _prioridade));
    }

    private Processo(String _nome, int _tipoTarefa, int _ingresso, int _duracao, int _prioridade) {
        this.nome = _nome;
        this.tipoTarefa = _tipoTarefa;
        this.ingresso = _ingresso;
        this.duracao = _duracao;
        this.tempo_restante = duracao;
        this.prioridade = _prioridade;
        if (Processo.ultimo_id == 0) {
            Processo.ultimo_id = 1;
        }
        this.id = Processo.ultimo_id;
        this.tarefa = new TarefaStringAleatoria(this.id, _nome);
        novo = true;
        iniciado = false;
        pronto = false;
        encerrado = false;
        Processo.ultimo_id++;

        switch (tipoTarefa) {
            case 1:
                tarefa = new TarefaStringAleatoria(id, _nome);
                break;
            case 2:
                tarefa = new TarefaNumeroAleatorio(id, _nome);
                break;
            default:
                tarefa = null;
                break;
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public int getIngresso() {
        return ingresso;
    }

    public void setIngresso(int _ingresso) {
        this.ingresso = _ingresso;
    }

    public int getDuracao() {
        return duracao;
    }

    public void setDuracao(int _duracao) {
        this.duracao = _duracao;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int _prioridade) {
        this.prioridade = _prioridade;
    }

    public TarefaAbstrata getTarefa() {
        return tarefa;
    }

    public int getQuantumRestante() {
        return quantum_restante;
    }

    public int getTempoRestante() {
        return tempo_restante;
    }

    public void diminuiQuantum(int quantumUsado) {
        this.quantum_restante -= quantumUsado;
    }

    public void diminuiTempoRestante(int tempo) {
        this.tempo_restante -= tempo;
    }

    public void adicionaQuantum(int quantum) {
        this.quantum_restante += quantum;
    }

    public boolean isNovo() {
        return novo;
    }

    public boolean isIniciado() {
        return iniciado;
    }

    public boolean isPronto() {
        return pronto;
    }

    public boolean isEncerrado() {
        return encerrado;
    }

    public void criarNovaTarefa() {
        novo = false;
        pronto = true;
        tarefa.start();
    }

    public void iniciarTarefa() {
        pronto = false;
        iniciado = true;
        tarefa.iniciar();
    }

    public void preemptarTarefa() {
        pronto = true;
        tarefa.preemptar();
    }

    public void rodarTarefa() {
        pronto = false;
        tarefa.rodar();
    }

    public void encerrarTarefa() {
        encerrado = true;
        tarefa.encerrarTarefa();
        tarefa = null;
    }
}
