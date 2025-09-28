package org.minesweeper.service;

import org.minesweeper.exceptions.ForaDoTabuleiroException;
import org.minesweeper.model.Localizacao;
import org.minesweeper.model.QuadradoFront;
import org.minesweeper.model.Tabuleiro;

public class MarcarQuadrado extends AcaoTabuleiro{

    public MarcarQuadrado(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    @Override
    public QuadradoFront visitTabuleiro(Tabuleiro tabuleiro) {
        try {
            if (tabuleiro.isAberto(localizacao))
                return null;

            if (tabuleiro.isMarcado(localizacao)) {
                tabuleiro.setDesmarcado(localizacao);
                return new QuadradoFront(false, -1, false, localizacao, false);
            } else {
                tabuleiro.setMarcado(localizacao);
                return new QuadradoFront(false, -1, true, localizacao, false);
            }
        } catch (ForaDoTabuleiroException e) {
            return null;
        }
    }
}
