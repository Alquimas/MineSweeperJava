package org.minesweeper.coordinator;

import org.minesweeper.navigator.NavegadorTelaFimJogo;
import org.minesweeper.navigator.NavegadorTelaJogo;

import javax.swing.*;

public class Coordenador implements CoordenadorListener {
    private NavegadorTelaJogo navegadorTelaJogo;
    private NavegadorTelaFimJogo navegadorTelaFimJogo;
    private JFrame frame;

    public Coordenador() {
    }

    public void IniciaJogo() {
        navegadorTelaJogo = new NavegadorTelaJogo();
        navegadorTelaFimJogo = new NavegadorTelaFimJogo();
        frame = new JFrame("Minesweeper");
        frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        navegadorTelaJogo.subscribe(this);
        navegadorTelaJogo.iniciar(frame);
    }

    public void fimJogo(boolean ganhou) {
        navegadorTelaJogo.unsubscribe(this);
        navegadorTelaFimJogo.subscribe(this);
        frame = new JFrame("Minesweeper");
        frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        navegadorTelaFimJogo.iniciar(frame, ganhou);
    }

    public void reiniciaJogo() {
        navegadorTelaJogo = null;
        navegadorTelaFimJogo = null;
        IniciaJogo();
    }

    public void encerraAplicacao() {
        System.exit(0);
    }

    public void encerraAplicacaoErroNaoCriouJogo() {
        System.exit(1);
    }
}