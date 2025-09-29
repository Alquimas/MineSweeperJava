package org.minesweeper.service;

import org.minesweeper.exceptions.ForaDoTabuleiroException;
import org.minesweeper.model.Localizacao;
import org.minesweeper.model.Quadrado;
import org.minesweeper.model.Tabuleiro;
import org.minesweeper.model.TabuleiroFront;

import java.util.ArrayList;
import java.util.Random;

public class MontarTabuleiro extends AcaoTabuleiro{
    private int linha_size;
    private int coluna_size;
    private int numQuadrados;
    private int bombas;
    private Random rand = new Random();

    public MontarTabuleiro(int linha_size, int coluna_size, int bombas) {
        this.linha_size = linha_size;
        this.coluna_size = coluna_size;
        this.bombas = bombas;

        this.numQuadrados = linha_size * coluna_size;
    }

    @Override
    public TabuleiroFront visitTabuleiro(Tabuleiro tabuleiro) {
        ArrayList<Quadrado> quadrados = new ArrayList<>();
        tabuleiro.inicializaTabuleiroVazio(linha_size, coluna_size);

        // Cria quadrados sem bomba
        for (int i = 0; i < numQuadrados - bombas; ++i)
            quadrados.add(new Quadrado(false, false, false));

        // Cria quadrados com bomba
        for (int i = 0; i < bombas; ++i)
            quadrados.add(new Quadrado(true, false, false));

        // Conta quantos quadrados foram adicionados
        int cont = 0;
        try {
            // Adiciona os quadrados de forma aletória no tabuleiro
            for (int i = 0; i < linha_size; ++i)
                for (int j = 0; j < coluna_size; ++j) {
                    // Reduz o limite de geração para ficar de acordo com o novo
                    // tamanho do array após a remoção do elemento sorteado
                    int num = rand.nextInt(numQuadrados - cont++);
                    Quadrado atual = quadrados.remove(num);

                    atual.setLocalizacao(new Localizacao(i, j));
                    tabuleiro.adicionaQuadrado(atual);
                }
        } catch (ForaDoTabuleiroException e){
            return null;
        }

        return new TabuleiroFront(linha_size, coluna_size);
    }
}
