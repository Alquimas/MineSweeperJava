package org.minesweeper.model;

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
    
    public boolean isAberto(Localizacao localizacao) {
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();

        return tabuleiro.get(linha).get(coluna).isAberto();
    }

    public boolean isMarcado(Localizacao localizacao) {
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();

        return tabuleiro.get(linha).get(coluna).isMarcado();
    }

    public boolean isBomba(Localizacao localizacao){
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();

        return tabuleiro.get(linha).get(coluna).isBomba();
    }

    public int getVizinhosPerigosos(Localizacao localizacao){
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();

        return tabuleiro.get(linha).get(coluna).getNumero();
    }

    public int getLinha_size() {
        return linha_size;
    }

    public int getColuna_size() {
        return coluna_size;
    }
}
