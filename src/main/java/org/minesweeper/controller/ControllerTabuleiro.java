package org.minesweeper.controller;

import org.minesweeper.model.*;
import org.minesweeper.service.AbrirQuadrado;
import org.minesweeper.service.AcaoTabuleiro;
import org.minesweeper.service.MarcarQuadrado;
import org.minesweeper.service.MontarTabuleiro;

import java.util.ArrayList;

public class ControllerTabuleiro {
    private TabuleiroInterface tabuleiro;
    private static ControllerTabuleiro instance;

    private ControllerTabuleiro(){
        tabuleiro = Tabuleiro.getInstance();
    }

    public TabuleiroFront iniciarNovoJogo(int linha_size, int coluna_size, int bomba){
        AcaoTabuleiro acao = new MontarTabuleiro(linha_size, coluna_size, bomba);
        return tabuleiro.accept(acao);
    }

    public ArrayList<QuadradoFront> clicarBotaoEsquerdo(Localizacao localizacao){
        AcaoTabuleiro acao = new AbrirQuadrado(localizacao);
        return tabuleiro.accept(acao);
    }

    public QuadradoFront clicarBotaoDireito(Localizacao localizacao){
        AcaoTabuleiro acao = new MarcarQuadrado(localizacao);
        return tabuleiro.accept(acao);
    }

    public static ControllerTabuleiro getInstance() {
        if (instance == null)
            instance = new ControllerTabuleiro();

        return instance;
    }
}
