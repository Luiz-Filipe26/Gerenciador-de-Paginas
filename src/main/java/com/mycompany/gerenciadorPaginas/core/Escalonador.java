package com.mycompany.gerenciadorPaginas.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mycompany.gerenciadorPaginas.controle.GerenciadorPaginasController;

public class Escalonador extends Thread {

    private GerenciadorPaginasController gerenciadorPaginasController;

    private List<Processo> listaProcessosExecutando = new ArrayList<>();
    private List<Processo> filaProcessosProntos = new ArrayList<>();
    private List<Processo> processosFuturos;
    private Map<Processo, List<Integer>> mapaDeInstantes = new HashMap<>();
    private int instanteInicio;
    private int instanteAtual;
    private static Escalonador escalonador;
    private final int numProcessadores;
    private int quantumPorProcesso;
    private float unidadeQuantum;
    private boolean continuarEscalonador;
    private static boolean fecharAoAcabar;
    private boolean dormir;
    private static String tipoEscalonador;
    private int numTrocasContexto;
    private int processosRemovidos;
    private int numProcessosIngressados;
    private int tempoExecucaoTotal;
    private int tempoEsperaTotal;

    public static Escalonador getInstancia(String _tipoEscalonador, int _numProcessadores, float _unidadeQuantum, int _quantumPorProcesso) {
        if (escalonador == null) {
            escalonador = new Escalonador(_tipoEscalonador, _numProcessadores, _unidadeQuantum, _quantumPorProcesso);
        }
        return escalonador;
    }

    public static Escalonador getInstancia(String _tipoEscalonador, int _numProcessadores, float _unidadeQuantum) {
        if (escalonador == null) {
            if (!_tipoEscalonador.equals("Round-Robin")) {
                escalonador = new Escalonador(_tipoEscalonador, _numProcessadores, _unidadeQuantum, 0);
            }
        }
        return escalonador;
    }

    public static Escalonador getInstancia() {
        return escalonador;
    }

    private Escalonador(String _tipoEscalonador, int _numProcessadores, float _unidadeQuantum, int _quantumPorProcesso) {
        this.gerenciadorPaginasController = GerenciadorPaginasController.getInstancia();

        Escalonador.tipoEscalonador = _tipoEscalonador;
        this.numProcessadores = _numProcessadores;
        this.unidadeQuantum = _unidadeQuantum;
        this.quantumPorProcesso = _quantumPorProcesso;
        processosFuturos = new ArrayList<>(Processo.listaDeProcessos);

        for (Processo processo : Processo.listaDeProcessos) {
            mapaDeInstantes.put(processo, new ArrayList<>());
        }

        dormir = false;
        fecharAoAcabar = false;
        continuarEscalonador = true;
    }

    public void parar() {
        continuarEscalonador = false;
        synchronized (this) {
            notify();
        }
    }

    public void acorda() {
        dormir = false;
        synchronized (this) {
            notify();
        }
    }

    public void setFecharAoAcabar(boolean _fecharAoAcabar) {
        Escalonador.fecharAoAcabar = _fecharAoAcabar;
    }

    public boolean isDormindo() {
        return dormir;
    }

    public int getInstanteAtual() {
        return instanteAtual;
    }

    public Map<Processo, List<Integer>> getMapaInstantes() {
        return mapaDeInstantes;
    }

    public Map<Processo, List<Integer>> getMapaInstantesLimpo() {
        Iterator<Map.Entry<Processo, List<Integer>>> iterator = mapaDeInstantes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Processo, List<Integer>> entry = iterator.next();
            Processo processo = entry.getKey();
            List<Integer> instantesDeExecucao = entry.getValue();
            if (processo.getTempoRestante() == 0) {
                iterator.remove();
            } else {
                instantesDeExecucao.removeIf(instante -> instante < instanteAtual);
            }
        }
        return mapaDeInstantes;
    }

    public static void atualizaListaDeProcessos(Processo p) {

        Processo.listaDeProcessos.add(p);
        escalonador.mapaDeInstantes.put(p, new ArrayList<>());

        if (escalonador != null) {
            GerenciadorPaginasController.getInstancia().repintarGrafico(escalonador.mapaDeInstantes, escalonador.instanteAtual);
            escalonador.processosFuturos.add(p);
            /*if (!fecharAoAcabar && escalonador.isDormindo()) {
                escalonador.acorda();
            }*/
        }
    }

//MÉTODO RUN =========================================================================
    @Override
    public void run() {
        do {
            adicionarNovosProcessos();
            while (continuarEscalonador && !processosFuturos.isEmpty() || !listaProcessosExecutando.isEmpty() || !filaProcessosProntos.isEmpty()) {
                if (prosseguirProcessosEmFila()) {
                    mostrarStatusProcessos();
                    gerenciadorPaginasController.atualizarPaginas(listaProcessosExecutando.get(0), instanteAtual - instanteInicio);
                    esperarUmQuantum();
                }
                limparProcessosAcabados();
                adicionarNovosProcessos();
                ordernarFilaProcessos();
            }

            float tempoMedioExecucao = tempoExecucaoTotal * unidadeQuantum / numProcessosIngressados;
            float tempoMedioEspera = tempoEsperaTotal * unidadeQuantum / numProcessosIngressados;
            gerenciadorPaginasController.mostrarResultadoEscalonador(numTrocasContexto, tempoMedioExecucao, tempoMedioEspera);
            tempoMedioExecucao = tempoMedioEspera = tempoExecucaoTotal = tempoEsperaTotal = numTrocasContexto = numProcessosIngressados = 0;
            instanteInicio = instanteAtual;

            if (!fecharAoAcabar) {
                dormir = true;
                try {
                    synchronized (this) {
                        if (dormir && continuarEscalonador) {
                            wait();
                        }
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(Escalonador.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } while (continuarEscalonador && !fecharAoAcabar);
    }

    private void adicionarNovosProcessos() {
        if (processosFuturos.isEmpty() || processosFuturos.get(0).getIngresso() > instanteAtual) {
            return;
        }

        List<Processo> novosProcessos = new ArrayList<>();

        for (Processo processo : processosFuturos) {
            if (instanteAtual >= processo.getIngresso()) {
                processo.criarNovaTarefa();
                novosProcessos.add(processo);
                numProcessosIngressados++;
            } else {
                break;
            }
        }

        if (tipoEscalonador.equals("Round-Robin")) {
            novosProcessos.sort(Comparator.comparing(Processo::getPrioridade).thenComparing(Processo::getDuracao));
        } else if (tipoEscalonador.equals("Shortest Remaining Time First")
                || tipoEscalonador.equals("Shortest Job First")) {
            novosProcessos.sort(Comparator.comparing(Processo::getDuracao).thenComparing(Processo::getPrioridade));
        } else if (tipoEscalonador.equals("Escalonamento por Prioridade Cooperativo")) {
            novosProcessos.sort(Comparator.comparing(Processo::getPrioridade).reversed().thenComparing(Processo::getDuracao));
        }

        processosFuturos.removeAll(novosProcessos);
        filaProcessosProntos.addAll(novosProcessos);
    }

    private boolean prosseguirProcessosEmFila() {
        boolean controlaQuantum = tipoEscalonador.equals("Round-Robin");

        if (listaProcessosExecutando.isEmpty() && filaProcessosProntos.isEmpty()) {
            return true;
        }

        for (int i = listaProcessosExecutando.size(); i < numProcessadores && !filaProcessosProntos.isEmpty(); i++) {
            Processo processo = filaProcessosProntos.get(0);
            listaProcessosExecutando.add(processo);
            filaProcessosProntos.remove(processo);
            if (controlaQuantum) {
                processo.adicionaQuantum(Math.min(processo.getTempoRestante(), quantumPorProcesso));
            }
        }

        for (Processo processo : listaProcessosExecutando) {
            if (!processo.isIniciado()) {
                processo.iniciarTarefa();
            } else if (processo.isPronto()) {
                processo.rodarTarefa();
            }
            if (controlaQuantum) {
                processo.diminuiQuantum(1);
            }
            processo.diminuiTempoRestante(1);
            mapaDeInstantes.get(processo).add(instanteAtual);
        }
        return true;
    }

    private void mostrarStatusProcessos() {
        gerenciadorPaginasController.atualizarGrafico(mapaDeInstantes, instanteAtual);
    }

    public void limparProcessosAcabados() {
        int pRemovidos = 0;
        for (int i = 0; i < listaProcessosExecutando.size(); i++) {
            Processo processo = listaProcessosExecutando.get(i);
            if (processo.getTempoRestante() == 0) {
                processo.encerrarTarefa();
                listaProcessosExecutando.remove(i--);
                pRemovidos++;
                tempoExecucaoTotal += (instanteAtual - instanteInicio - processo.getIngresso());
                tempoEsperaTotal += (instanteAtual - instanteInicio - processo.getIngresso() - processo.getDuracao());
            }
        }
        processosRemovidos = pRemovidos;
    }

    public void ordernarFilaProcessos() {

        if (filaProcessosProntos.isEmpty()) {
            return;
        }

        if (tipoEscalonador.equals("Round-Robin")) {
            if (filaProcessosProntos.isEmpty()) {
                return;
            }
            for (int i = 0, j = listaProcessosExecutando.size() + filaProcessosProntos.size() - (numProcessadores * 2); i < listaProcessosExecutando.size() && j > 0; i++) {
                Processo processo = listaProcessosExecutando.get(i);
                if (processo.getQuantumRestante() <= 0) {
                    processo.preemptarTarefa();
                    filaProcessosProntos.add(processo);
                    listaProcessosExecutando.remove(i--);
                    j--;
                    processosRemovidos++;
                }
            }
            numTrocasContexto += Math.min(processosRemovidos, filaProcessosProntos.size());
            return;
        } else if (tipoEscalonador.equals("Shortest Remaining Time First")) {

            int execMaiorTempoRestante;
            int pronMenorTempoRestante;

            if (!listaProcessosExecutando.isEmpty() && !filaProcessosProntos.isEmpty()) {
                do {
                    Processo processoExecMaiorTempoRestante = Collections.max(listaProcessosExecutando, Comparator.comparing(Processo::getTempoRestante));
                    execMaiorTempoRestante = processoExecMaiorTempoRestante.getTempoRestante();
                    Processo processoPronMenorTempoRestante = Collections.min(filaProcessosProntos, Comparator.comparing(Processo::getTempoRestante));
                    pronMenorTempoRestante = processoPronMenorTempoRestante.getTempoRestante();
                    if (execMaiorTempoRestante > pronMenorTempoRestante) {
                        processoExecMaiorTempoRestante.preemptarTarefa();
                        listaProcessosExecutando.remove(processoExecMaiorTempoRestante);
                        processosRemovidos++;
                        filaProcessosProntos.add(processoExecMaiorTempoRestante);
                    }
                } while (execMaiorTempoRestante > pronMenorTempoRestante && !listaProcessosExecutando.isEmpty());
            }
        }
        numTrocasContexto += Math.min(processosRemovidos, filaProcessosProntos.size());

        int numInsercoes = Math.min(filaProcessosProntos.size(), numProcessadores - listaProcessosExecutando.size());
        if (numInsercoes > 0) {
            List<Processo> processosAInserir = new ArrayList<>();

            for (int i = 0; i < numInsercoes; i++) {
                Processo processoAInserir
                        = tipoEscalonador.equals("Shortest Remaining Time First")
                        ? Collections.min(filaProcessosProntos, Comparator.comparing(Processo::getTempoRestante))
                        : tipoEscalonador.equals("Shortest Job First")
                        ? Collections.min(filaProcessosProntos, Comparator.comparing(Processo::getDuracao))
                        : tipoEscalonador.equals("Escalonamento por Prioridade Cooperativo")
                        ? Collections.max(filaProcessosProntos, Comparator.comparing(Processo::getPrioridade))
                        : null;
                filaProcessosProntos.remove(processoAInserir);
                processosAInserir.add(processoAInserir);
            }

            filaProcessosProntos.addAll(0, processosAInserir);
        }
    }

    private void esperarUmQuantum() {
        instanteAtual++;
        gerenciadorPaginasController.adicionarTexto("Instante atual: " + instanteAtual + "\n");
        try {
            Thread.sleep((long) (unidadeQuantum * 1000));
        } catch (InterruptedException ex) {
            Logger.getLogger(Escalonador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
