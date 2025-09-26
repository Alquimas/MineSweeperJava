package org.minesweeper.model;

import org.minesweeper.service.AcaoTabuleiro;

import java.util.ArrayList;

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
