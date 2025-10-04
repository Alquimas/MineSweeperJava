package org.minesweeper.navigator;

import org.minesweeper.controller.ControllerTabuleiro;
import org.minesweeper.coordinator.CoordenadorListener;
import org.minesweeper.model.Localizacao;
import org.minesweeper.model.TabuleiroFront;
import org.minesweeper.view.ViewTelaJogo;

import javax.swing.*;
import java.util.ArrayList;

public class NavegadorTelaJogo implements NavegadorTelaJogoListener{
    private ViewTelaJogo view;
    private ControllerTabuleiro controller;
    private TabuleiroFront tabuleiro;
    private ArrayList<CoordenadorListener> listeners;
    private JFrame tela;

    @Override
    public void confirmouErro() {}

    @Override
    public void onBotaoDireito(Localizacao localizacao) {}

    @Override
    public void onBotaoEsquerdo(Localizacao localizacao) {}

    public void iniciar(JFrame tela){}
    private void destruir(){}
    private void criaJogo(){}
    public void subscribe(CoordenadorListener coordenadorListener){}
    public void unsubscribe(CoordenadorListener coordenadorListener){}

}
