package org.minesweeper.service;

import org.minesweeper.exceptions.ForaDoTabuleiroException;
import org.minesweeper.model.Localizacao;
import org.minesweeper.model.QuadradoFront;
import org.minesweeper.model.Tabuleiro;

import java.util.ArrayList;

public class AbrirQuadrado extends AcaoTabuleiro{

    public AbrirQuadrado(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    @Override
    public ArrayList<QuadradoFront> visitTabuleiro(Tabuleiro tabuleiro){
        try {
            if (tabuleiro.isAberto(localizacao)) return null;
            if (tabuleiro.isMarcado(localizacao)) return null;

            if (tabuleiro.isBomba(localizacao)) {
                tabuleiro.setAberto(localizacao);
                ArrayList<QuadradoFront> quadrados = new ArrayList<>();
                quadrados.add(new QuadradoFront(true, -1, false, localizacao, true));
            }

            int num = tabuleiro.quantVizinhosPerigosos(localizacao);

            if (num == 0) return abreVizinhos(tabuleiro);

            ArrayList<QuadradoFront> quadrados = new ArrayList<>();
            quadrados.add(new QuadradoFront(true, num, false, localizacao, false));
            return quadrados;
        } catch (ForaDoTabuleiroException e) {
            return null;
        }
    }

    private ArrayList<QuadradoFront> abreVizinhos(Tabuleiro tabuleiro){
        ArrayList<QuadradoFront> quadrados = new ArrayList<>();
        quadrados.add(new QuadradoFront(true, 0, false, localizacao, false));

        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();

        for(int i = -1; i <= 1; ++i)
            for(int j = -1; j <= 1; ++j)
                if ((i != 0 || j != 0)) {
                    this.localizacao.setLinha(linha + i);
                    this.localizacao.setColuna(coluna + j);

                    ArrayList<QuadradoFront> q = visitTabuleiro(tabuleiro);
                    if (q != null)
                        quadrados.addAll(visitTabuleiro(tabuleiro));
                }

        return quadrados;
    }
}
