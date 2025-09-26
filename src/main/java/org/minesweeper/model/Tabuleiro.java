package org.minesweeper.model;

import org.minesweeper.service.AcaoTabuleiro;

import java.util.ArrayList;
import java.util.Random;

public class Tabuleiro implements TabuleiroInterface {
    private ArrayList<ArrayList<Quadrado>> tabuleiro;
    private int linha_size;
    private int coluna_size;
    private static Tabuleiro instance;

    private Tabuleiro(){
        this.tabuleiro = new ArrayList<>();
        this.linha_size = 0;
        this.coluna_size = 0;
    }

    @Override
    public <R> R accept(AcaoTabuleiro acaoTabuleiro) {
        return acaoTabuleiro.visitTabuleiro();
    }

    /**
     * Cria um novo tabuleiro para o jogo.
     *
     * @param linha A quantidade de linhas do tabuleiro.
     * @param coluna A quantidade de colunas do tabuleiro.
     * @param bombas A quantidade de bombas do tabuleiro.
     */
    public void criaTabuleiro(int linha, int coluna, int bombas){
        int numQuadrados = linha * coluna;
        Random r = new Random();
        ArrayList<Quadrado> quadrados = new ArrayList<>();

        // Cria quadrados sem bomba
        for (int i = 0; i < numQuadrados - bombas; ++i){
            quadrados.add(new Quadrado(false, false, false));
        }

        // Cria quadrados com bomba
        for (int i = 0; i < bombas; ++i){
            quadrados.add(new Quadrado(true, false, false));
        }

        // Conta quantos quadrados foram adicionados
        int cont = 0;

        // Adiciona os quadrados de forma aletória no tabuleiro
        for (int i = 0; i < linha; ++i){
            tabuleiro.add(new ArrayList<>());

            for (int j = 0; j < coluna; ++j){
                // Reduz o limite de geração para ficar de acordo com o novo
                // tamanho do array após a remoção do elemento sorteado
                int num = r.nextInt(numQuadrados - cont++);
                Quadrado atual = quadrados.remove(num);

                atual.setLocalizacao(new Localizacao(i, j));
                tabuleiro.get(i).add(atual);
            }
        }
    }

    public int getLinha_size() {
        return linha_size;
    }

    public int getColuna_size() {
        return coluna_size;
    }

    public static Tabuleiro getInstance(){
        if (instance == null)
            instance = new Tabuleiro();

        return instance;
    }
}
