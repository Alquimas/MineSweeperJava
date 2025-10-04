package org.minesweeper.view;

import org.minesweeper.navigator.NavegadorTelaFimJogoListener;

import javax.swing.*;
import java.util.ArrayList;

public class ViewTelaFimJogo extends JPanel {
    private ArrayList<NavegadorTelaFimJogoListener> listeners;

    public void limparRecursos(){}
    public void mostraTelaPerdeuJogo(){}
    public void mostraTelaGanhouJogo(){}
    public void notificaReiniciarJogo(){}
    public void notificaFinalizarJogo(){}
    public void subscribe(NavegadorTelaFimJogoListener navegadorTelaFimJogoListener){}
    public void unsubscribe(NavegadorTelaFimJogoListener navegadorTelaFimJogoListener){}
}
