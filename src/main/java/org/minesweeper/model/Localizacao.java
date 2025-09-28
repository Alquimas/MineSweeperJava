package org.minesweeper.model;

import java.util.Objects;

public class Localizacao {
    private int linha;
    private int coluna;

    public Localizacao(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
    }

    public int getLinha() {
        return linha;
    }

    public void setLinha(int linha) {
        this.linha = linha;
    }

    public int getColuna() {
        return coluna;
    }

    public void setColuna(int coluna) {
        this.coluna = coluna;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Localizacao that = (Localizacao) o;
        return linha == that.linha && coluna == that.coluna;
    }

    @Override
    public int hashCode() {
        return Objects.hash(linha, coluna);
    }
}
