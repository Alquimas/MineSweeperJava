package org.minesweeper.model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TabuleiroFront {
    private ArrayList<ArrayList<QuadradoFront>> tabuleiro;
    private int linha_size;
    private int coluna_size;

    public int getLinha_size() {
        return linha_size;
    }

    public int getColuna_size() {
        return coluna_size;
    }
}
