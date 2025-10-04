package org.minesweeper.view;

import org.minesweeper.model.Localizacao;
import org.minesweeper.navigator.NavegadorTelaJogoListener;

import javax.swing.*;
import java.util.ArrayList;

public class ViewTelaJogo extends JFrame{
    private ArrayList<NavegadorTelaJogoListener> listeners;
    private ArrayList<ArrayList<JButton>> tabuleiro;

    public void mostraJogo(int linha_size, int coluna_size){}
    public void limparRecursos(){}
    public void notificaBotaoDireito(Localizacao localizacao){}
    public void notificaBotaoEsquerdo(Localizacao localizacao){}
    public void mostraQuadradoAberto(Localizacao localizacao, int numBombasVizinhos){}
    public void mostraQuadradoBomba(Localizacao localizacao){}
    public void mostraQuadradoMarcado(Localizacao localizacao){}
    public void mostraQuadradoDesmarcado(Localizacao localizacao){}
    public void mostraErroCriacaoJogo(){}
    public void notificaOkErroCriacaoJogo(){}
    public void subscribe(NavegadorTelaJogoListener navegadorTelaJogoListener){}
    public void unsubscribe(NavegadorTelaJogoListener navegadorTelaJogoListener){}
}
