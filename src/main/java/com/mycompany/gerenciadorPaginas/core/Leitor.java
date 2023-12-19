package com.mycompany.gerenciadorPaginas.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Leitor {

    public ArrayList<List<String>> lerArquivos() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos de Texto", "txt"));
        int escolha = fileChooser.showOpenDialog(null);

        if (escolha == JFileChooser.APPROVE_OPTION) {
            ArrayList<List<String>> processos = new ArrayList<>();
            String nomeArquivo = fileChooser.getSelectedFile().getPath();

            try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivo))) {
                String linha;
                while ((linha = br.readLine()) != null) {
                    String[] partes = linha.trim().split("\\s+");
                    if (partes.length == 5) {
                        String nome = partes[0];
                        if (isNumero(partes[1]) && isNumero(partes[2]) && isNumero(partes[3]) && isNumero(partes[4])) {
                            List<String> processo = new ArrayList<>();
                            processo.add(nome);
                            processo.add(partes[1]);
                            processo.add(partes[2]);
                            processo.add(partes[3]);
                            processo.add(partes[4]);
                            processos.add(processo);
                        }
                    }
                }
            } catch (IOException e) {
            }

            return processos;
        }

        return null;
    }

    public List<Integer> lerArquivoPaginas() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos de Texto", "txt"));
        int escolha = fileChooser.showOpenDialog(null);

        if (escolha == JFileChooser.APPROVE_OPTION) {
            List<Integer> paginas = new ArrayList<>();
            String nomeArquivo = fileChooser.getSelectedFile().getPath();

            try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivo))) {
                String linha;
                while ((linha = br.readLine()) != null) {
                    String[] partes = linha.trim().split("\\s+");
                    for (String parte : partes) {
                        if (isNumero(parte)) {
                            paginas.add(Integer.parseInt(parte));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return paginas;
        }

        return null;
    }

    private boolean isNumero(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
