package org.minesweeper.navigator;

import org.minesweeper.controller.ControllerTabuleiro;
import org.minesweeper.coordinator.CoordenadorListener;
import org.minesweeper.exceptions.ForaDoTabuleiroException;
import org.minesweeper.model.Localizacao;
import org.minesweeper.model.QuadradoFront;
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

    public NavegadorTelaJogo(){
        listeners = new ArrayList<>();
        view = new ViewTelaJogo();
        controller = ControllerTabuleiro.getInstance();
    }

    @Override
    public void confirmouErro() {
        destruir();
        for (CoordenadorListener l : new ArrayList<>(listeners)) {
            l.encerraAplicacaoErroNaoCriouJogo();
        }
    }

    @Override
    public void onBotaoDireito(Localizacao localizacao) {
        QuadradoFront qf = controller.clicarBotaoDireito(localizacao);

        if (qf == null) {
            return;
        }

        if (qf.isMarcado()) {
            view.mostraQuadradoMarcado(localizacao);
        } else {
            view.mostraQuadradoDesmarcado(localizacao);
        }
    }

    @Override
    public void onBotaoEsquerdo(Localizacao localizacao) {
        ArrayList<QuadradoFront> resultado = controller.clicarBotaoEsquerdo(localizacao);

        if (resultado == null || resultado.isEmpty()) {
            return;
        }

        if (resultado.get(0).isBomba()) {
            view.mostraQuadradoBomba(localizacao);
            destruir();
            for (CoordenadorListener l : new ArrayList<>(listeners)) {
                l.fimJogo(false); // derrota
            }
        } else {
            for (QuadradoFront qf : resultado) {
                view.mostraQuadradoAberto(qf.getLocalizacao(), qf.getNumero());
            }
            if(controller.ganhou()) {
                destruir();
                for (CoordenadorListener l : new ArrayList<>(listeners)) {
                    l.fimJogo(true);
                }
            }
        }
    }

    public void iniciar(JFrame tela){
        this.tela = tela;
        view.subscribe(this);
        tela.add(view);
        criaJogo();
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

    private void criaJogo(){
        int linhas = 20;
        int colunas = 20;
        int bombas = 99;

        tabuleiro = controller.iniciarNovoJogo(linhas, colunas, bombas);

        if (tabuleiro == null) {
            view.mostraErroCriacaoJogo();
            return;
        }

        view.mostraJogo(tabuleiro.getLinha_size(), tabuleiro.getColuna_size());
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
