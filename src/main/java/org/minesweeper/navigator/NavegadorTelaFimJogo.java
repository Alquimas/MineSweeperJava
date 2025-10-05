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
    public void finalizarJogo() {
        destruir();
        for (CoordenadorListener listener : new ArrayList<>(listeners)) {
            listener.encerraAplicacao();
        }
    }

    @Override
    public void reiniciarJogo() {
        destruir();
        for (CoordenadorListener listener : new ArrayList<>(listeners)) {
            listener.reiniciaJogo();
        }
    }

    public void iniciar(JFrame tela, boolean ganhou){
        this.tela = tela;
        this.view = new ViewTelaFimJogo();
        this.view.subscribe(this);
        tela.add(view);

        if (ganhou) {
            view.mostraTelaGanhouJogo();
        } else {
            view.mostraTelaPerdeuJogo();
        }
    }

    private void destruir(){
        if (view != null) {
            view.unsubscribe(this);
            view.limparRecursos();
            if (tela != null) {
                tela.remove(view);
            }
            view = null;
        }
    }

    public void subscribe(CoordenadorListener coordenadorListener){
        if (!listeners.contains(coordenadorListener)) {
            listeners.add(coordenadorListener);
        }
    }
    public void unsubscribe(CoordenadorListener coordenadorListener){
        listeners.remove(coordenadorListener);
    }
}
