package org.minesweeper.model;

import org.minesweeper.exceptions.ForaDoTabuleiroException;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TabuleiroFront {
    private ArrayList<ArrayList<QuadradoFront>> tabuleiro;
    private int linha_size;
    private int coluna_size;

    public TabuleiroFront(int linha_size, int coluna_size){
        this.linha_size = linha_size;
        this.coluna_size = coluna_size;

        for (int i = 0; i < linha_size; i++){
            tabuleiro = new ArrayList<>();
            for (int j = 0; j < coluna_size; j++){
                Localizacao localizacao = new Localizacao(i, j);
                tabuleiro.get(i).add(criaQuadrado(localizacao));
            }
        }
    }

    private QuadradoFront criaQuadrado(Localizacao localizacao){
        return new QuadradoFront(localizacao);
    }

    public void atualizaQuadrado(QuadradoFront quadrado){
        int linha = quadrado.getLocalizacao().getLinha();
        int coluna = quadrado.getLocalizacao().getColuna();

        tabuleiro.get(linha).set(coluna, quadrado);
    }
    
    public boolean isAberto(Localizacao localizacao) throws ForaDoTabuleiroException {
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();

        if (!quadradoExiste(linha, coluna)) throw new ForaDoTabuleiroException();

        return tabuleiro.get(linha).get(coluna).isAberto();
    }

    public boolean isMarcado(Localizacao localizacao) throws ForaDoTabuleiroException {
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();

        if (!quadradoExiste(linha, coluna)) throw new ForaDoTabuleiroException();

        return tabuleiro.get(linha).get(coluna).isMarcado();
    }

    public boolean isBomba(Localizacao localizacao) throws ForaDoTabuleiroException {
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();

        if (!quadradoExiste(linha, coluna)) throw new ForaDoTabuleiroException();

        return tabuleiro.get(linha).get(coluna).isBomba();
    }

    public int getVizinhosPerigosos(Localizacao localizacao) throws ForaDoTabuleiroException {
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();

        if (!quadradoExiste(linha, coluna)) throw new ForaDoTabuleiroException();

        return tabuleiro.get(linha).get(coluna).getNumero();
    }

    private boolean quadradoExiste(int linha, int coluna){
        if (coluna < 0 || coluna >= coluna_size) return false;
        if (linha < 0 || linha >= linha_size) return false;

        return true;
    }

    public int getLinha_size() {
        return linha_size;
    }

    public int getColuna_size() {
        return coluna_size;
    }
}
