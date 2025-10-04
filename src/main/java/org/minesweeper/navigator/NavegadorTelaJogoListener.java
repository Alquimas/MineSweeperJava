package org.minesweeper.navigator;

import org.minesweeper.model.Localizacao;

public interface NavegadorTelaJogoListener {
    void onBotaoDireito(Localizacao localizacao);
    void onBotaoEsquerdo(Localizacao localizacao);
    void confirmouErro();
}
