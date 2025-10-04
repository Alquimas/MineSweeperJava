package org.minesweeper.navigator;

import org.minesweeper.coordinator.CoordenadorListener;
import org.minesweeper.view.ViewTelaFimJogo;

import javax.swing.*;
import java.util.ArrayList;

public class NavegadorTelaFimJogo implements NavegadorTelaFimJogoListener {
    private ViewTelaFimJogo view;
    private ArrayList<CoordenadorListener> listeners;
    private JFrame tela;

    public NavegadorTelaFimJogo() {
        listeners = new ArrayList<>();
        view = new ViewTelaFimJogo();
    }

    @Override
    public void finalizarJogo() {}

    @Override
    public void reiniciarJogo() {}

    public void iniciar(JFrame tela, boolean ganhou){}
    private void destruir(){}
    private void ganhouJogo(){}
    private void perdeuJogo(){}
    public void subscribe(CoordenadorListener coordenadorListener){}
    public void unsubscribe(CoordenadorListener coordenadorListener){}
}
