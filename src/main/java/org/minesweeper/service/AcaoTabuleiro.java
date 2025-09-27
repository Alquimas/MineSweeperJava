package org.minesweeper.service;

import org.minesweeper.model.*;

public abstract class AcaoTabuleiro {
    Localizacao localizacao;

    public abstract <R> R visitTabuleiro(TabuleiroInterface tabuleiro);
}
