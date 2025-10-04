package org.minesweeper.model;

import org.minesweeper.service.AcaoTabuleiro;

public interface TabuleiroInterface {
    public abstract <R> R accept(AcaoTabuleiro acaoTabuleiro);
    public abstract boolean ganhou();
}
