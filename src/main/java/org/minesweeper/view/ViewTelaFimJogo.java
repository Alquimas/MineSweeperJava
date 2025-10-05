package org.minesweeper.view;

import org.minesweeper.navigator.NavegadorTelaFimJogoListener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ViewTelaFimJogo extends JPanel {
    private ArrayList<NavegadorTelaFimJogoListener> listeners = new ArrayList<>();
    private JPanel painelGrid;

    public ViewTelaFimJogo() {
        setLayout(new BorderLayout());
        painelGrid = new JPanel();
        painelGrid.setName("painelGrid");
        add(painelGrid, BorderLayout.CENTER);
    }

    public void limparRecursos(){
        painelGrid.removeAll();
        painelGrid.revalidate();
        painelGrid.repaint();

        Window janela = SwingUtilities.getWindowAncestor(this);
        if (janela != null) {
            janela.setVisible(false);
            janela.dispose();
        }
    }

    public void mostraTelaPerdeuJogo(){
        constroiTelaFinal("Você Perdeu!");
    }

    public void mostraTelaGanhouJogo(){
        constroiTelaFinal("Você Ganhou!");
    }

    private void constroiTelaFinal(String mensagem){
        painelGrid.removeAll();
        painelGrid.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel lblMensagem = new JLabel(mensagem);
        lblMensagem.setName("lblMensagem");
        lblMensagem.setFont(new Font("Arial", Font.BOLD, 36));

        JButton btnNovoJogo = new JButton("Novo Jogo");
        btnNovoJogo.setName("btnNovoJogo");

        JButton btnFinalizarJogo = new JButton("Finalizar Jogo");
        btnFinalizarJogo.setName("btnFinalizarJogo");

        btnNovoJogo.addActionListener(e -> notificaReiniciarJogo());
        btnFinalizarJogo.addActionListener(e -> notificaFinalizarJogo());

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        painelBotoes.add(btnNovoJogo);
        painelBotoes.add(btnFinalizarJogo);

        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 20, 10);
        painelGrid.add(lblMensagem, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        painelGrid.add(painelBotoes, gbc);

        painelGrid.revalidate();
        painelGrid.repaint();
    }

    public void notificaReiniciarJogo(){
        for (NavegadorTelaFimJogoListener listener : listeners) {
            listener.reiniciarJogo();
        }
    }

    public void notificaFinalizarJogo(){
        for (NavegadorTelaFimJogoListener listener : listeners) {
            listener.finalizarJogo();
        }
    }

    public void subscribe(NavegadorTelaFimJogoListener navegadorTelaFimJogoListener){
        if (!listeners.contains(navegadorTelaFimJogoListener))
            listeners.add(navegadorTelaFimJogoListener);
    }

    public void unsubscribe(NavegadorTelaFimJogoListener navegadorTelaFimJogoListener){
        listeners.remove(navegadorTelaFimJogoListener);
    }
}
