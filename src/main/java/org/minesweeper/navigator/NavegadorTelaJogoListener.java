package org.minesweeper.navigator;

import org.minesweeper.model.Localizacao;

public interface NavegadorTelaJogoListener {
    public abstract void onBotaoDireito(Localizacao localizacao);
    public abstract void onBotaoEsquerdo(Localizacao localizacao);
    public abstract void confirmouErro();
}
