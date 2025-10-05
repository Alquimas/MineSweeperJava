package org.minesweeper.view;

import org.minesweeper.model.Icons;
import org.minesweeper.model.Localizacao;
import org.minesweeper.navigator.NavegadorTelaJogoListener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ViewTelaJogo extends JPanel {
    private ArrayList<NavegadorTelaJogoListener> listeners = new ArrayList<>();
    private ArrayList<ArrayList<JButton>> tabuleiro = new ArrayList<>();
    private JPanel painelGrid;

    public ViewTelaJogo() {
        setLayout(new BorderLayout());
        painelGrid = new JPanel();
        painelGrid.setName("painelGrid"); // for FEST-Swing lookup
        add(painelGrid, BorderLayout.CENTER);
    }

    public void mostraJogo(int linha_size, int coluna_size){
        painelGrid.removeAll();
        painelGrid.setLayout(new GridLayout(linha_size, coluna_size));

        tabuleiro.clear();

        for (int i = 0; i < linha_size; i++) {
            ArrayList<JButton> linha = new ArrayList<>();
            for (int j = 0; j < coluna_size; j++) {
                JButton botao = getJButton(i, j);
                botao.setFont(new Font("Arial", Font.BOLD, 20));
                botao.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                botao.setBackground(new Color(190, 190, 190));

                linha.add(botao);
                painelGrid.add(botao);
            }
            tabuleiro.add(linha);
        }

        painelGrid.revalidate();
        painelGrid.repaint();
    }

    private JButton getJButton(int i, int j) {
        JButton botao = new JButton("");
        botao.setName("btn-" + i + "-" + j);
        botao.setEnabled(true);

        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    notificaBotaoDireito(new Localizacao(i, j));
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    notificaBotaoEsquerdo(new Localizacao(i, j));
                }
            }
        });
        return botao;
    }

    public void limparRecursos(){
        tabuleiro.clear();
        painelGrid.removeAll();
        painelGrid.revalidate();
        painelGrid.repaint();

        Window janela = SwingUtilities.getWindowAncestor(this);
        if (janela != null) {
            janela.setVisible(false);
            janela.dispose();
        }
    }

    public void notificaBotaoDireito(Localizacao localizacao){
        for (NavegadorTelaJogoListener l : new ArrayList<>(listeners)) {
            l.onBotaoDireito(localizacao);
        }
    }
    
    public void notificaBotaoEsquerdo(Localizacao localizacao){
        for (NavegadorTelaJogoListener l : new ArrayList<>(listeners)) {
            l.onBotaoEsquerdo(localizacao);
        }
    }

    public void mostraQuadradoAberto(Localizacao localizacao, int numBombasVizinhos){
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();

        if (linha < 0 || linha >= tabuleiro.size()) return;
        if (coluna < 0 || coluna >= tabuleiro.get(linha).size()) return;

        JButton botao = tabuleiro.get(linha).get(coluna);
        botao.setBackground(new Color(255, 255, 255));

        if (numBombasVizinhos > 0) {
            switch (numBombasVizinhos) {
                case 1 -> botao.setForeground(Color.BLUE);
                case 2 -> botao.setForeground(new Color(0, 128, 0)); // green
                case 3 -> botao.setForeground(Color.RED);
                case 4 -> botao.setForeground(new Color(0, 0, 128)); // dark blue
                case 5 -> botao.setForeground(new Color(128, 0, 0)); // dark red
                default -> botao.setForeground(Color.BLACK);
            }
            botao.setText(String.valueOf(numBombasVizinhos));
        } else {
            botao.setText("");
        }
    }

    public void mostraQuadradoBomba(Localizacao localizacao){
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();

        if (linha < 0 || linha >= tabuleiro.size()) return;
        if (coluna < 0 || coluna >= tabuleiro.get(linha).size()) return;

        JButton botao = tabuleiro.get(linha).get(coluna);
        botao.setEnabled(false);

        botao.setText("B");
    }

    public void mostraQuadradoMarcado(Localizacao localizacao){
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();

        JButton botao = tabuleiro.get(linha).get(coluna);
        botao.setText("");
        botao.setIcon(Icons.FLAG_ICON);
        botao.setHorizontalTextPosition(SwingConstants.CENTER);
        botao.setVerticalTextPosition(SwingConstants.CENTER);
        botao.setEnabled(true);
    }

    public void mostraQuadradoDesmarcado(Localizacao localizacao){
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();

        JButton botao = tabuleiro.get(linha).get(coluna);
        botao.setText("");
        botao.setIcon(null);
        botao.setEnabled(true);
    }

    public void mostraErroCriacaoJogo(){
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Problemas na criação do jogo.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            notificaOkErroCriacaoJogo();
        });
    }

    public void notificaOkErroCriacaoJogo(){
        for (NavegadorTelaJogoListener l : new ArrayList<>(listeners)) {
            l.confirmouErro();
        }
    }
    public void subscribe(NavegadorTelaJogoListener navegadorTelaJogoListener){
        if (!listeners.contains(navegadorTelaJogoListener)) {
            listeners.add(navegadorTelaJogoListener);
        }
    }
    public void unsubscribe(NavegadorTelaJogoListener navegadorTelaJogoListener){
        listeners.remove(navegadorTelaJogoListener);
    }
}
