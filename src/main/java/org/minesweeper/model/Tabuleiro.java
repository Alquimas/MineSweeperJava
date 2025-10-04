package org.minesweeper.model;

import org.minesweeper.exceptions.ForaDoTabuleiroException;
import org.minesweeper.service.AcaoTabuleiro;

import java.util.ArrayList;
import java.util.Random;

public class Tabuleiro implements TabuleiroInterface {
    private ArrayList<ArrayList<Quadrado>> tabuleiro;
    private int linha_size;
    private int coluna_size;
    private int bombas;
    private int quadradosAbertos;
    private static Tabuleiro instance;

    private Tabuleiro(){
        this.linha_size = 0;
        this.coluna_size = 0;
    }

    @Override
    public <R> R accept(AcaoTabuleiro acaoTabuleiro) {
        return acaoTabuleiro.visitTabuleiro(this);
    }

    public boolean ganhou(){
        return linha_size * coluna_size - bombas == quadradosAbertos;
    }

    public void inicializaTabuleiroVazio(int linha, int coluna){
        this.linha_size = linha;
        this.coluna_size = coluna;
        this.quadradosAbertos = 0;
        this.bombas = 0;

        this.tabuleiro = new ArrayList<>();

        for (int i = 0; i < linha_size; ++i) {
            tabuleiro.add(new ArrayList<>());
            for (int j = 0; j < coluna_size; j++)
                tabuleiro.get(i).add(new Quadrado(new Localizacao(i, j)));
        }
    }

    public void adicionaQuadrado(Quadrado quadrado) throws ForaDoTabuleiroException {
        int linha = quadrado.getLocalizacao().getLinha();
        int coluna = quadrado.getLocalizacao().getColuna();

        if (quadradoExiste(linha, coluna)) {
            tabuleiro.get(linha).set(coluna, quadrado);
            if (quadrado.isBomba())
                ++bombas;
        } else
            throw new ForaDoTabuleiroException();
    }

    /**
     * Função responsável por contar quantos vizinhos de um quadrado
     * contém bombas.
     *
     * @param localizacao O quadrado que terá seus vizinhos consultados.
     * @return Um valor entre 0 e 8 que diz a quantidade de vizinhos ao
     * redor do quadrado inicial que contém bombas.
     */
    public int quantVizinhosPerigosos(Localizacao localizacao){
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();

        int cont = 0;

        for(int i = -1; i <= 1; ++i)
            for(int j = -1; j <= 1; ++j)
                if ((i != 0 || j != 0) && quadradoExiste(linha + i, coluna + j)
                        && tabuleiro.get(linha + i).get(coluna + j).isBomba())
                    cont++;

        return cont;
    }

    private boolean quadradoExiste(int linha, int coluna){
        if (coluna < 0 || coluna >= coluna_size) return false;
        if (linha < 0 || linha >= linha_size) return false;

        return true;
    }

    public boolean isBomba(Localizacao localizacao) throws ForaDoTabuleiroException{
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();

        if (!quadradoExiste(linha, coluna)) throw new ForaDoTabuleiroException();

        return tabuleiro.get(linha).get(coluna).isBomba();
    }

    public void setAberto(Localizacao localizacao) throws ForaDoTabuleiroException{
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();

        if (!quadradoExiste(linha, coluna)) throw new ForaDoTabuleiroException();

        tabuleiro.get(linha).get(coluna).setAberto(true);
        ++quadradosAbertos;
    }

    public boolean isAberto(Localizacao localizacao) throws ForaDoTabuleiroException{
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();

        if (!quadradoExiste(linha, coluna)) throw new ForaDoTabuleiroException();

        return tabuleiro.get(linha).get(coluna).isAberto();
    }

    public void setMarcado(Localizacao localizacao) throws ForaDoTabuleiroException{
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();

        if (!quadradoExiste(linha, coluna)) throw new ForaDoTabuleiroException();

        tabuleiro.get(linha).get(coluna).setMarcado(true);
    }

    public void setDesmarcado(Localizacao localizacao) throws ForaDoTabuleiroException{
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();

        if (!quadradoExiste(linha, coluna)) throw new ForaDoTabuleiroException();

        tabuleiro.get(linha).get(coluna).setMarcado(false);
    }

    public boolean isMarcado(Localizacao localizacao) throws ForaDoTabuleiroException{
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();

        if (!quadradoExiste(linha, coluna)) throw new ForaDoTabuleiroException();

        return tabuleiro.get(linha).get(coluna).isMarcado();
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
