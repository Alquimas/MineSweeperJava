package org.minesweeper.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.minesweeper.exceptions.ForaDoTabuleiroException;
import org.minesweeper.model.Localizacao;
import org.minesweeper.model.Tabuleiro;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MontarTabuleiroIntegrationTest {

    private Tabuleiro tabuleiro;

    @BeforeEach
    void setUp() throws Exception {
        Field instanceField = Tabuleiro.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
        tabuleiro = Tabuleiro.getInstance();
    }

    @Test
    @DisplayName("Deve configurar o tabuleiro com as dimensões e o total de quadrados corretos")
    void visitTabuleiro_criaComDimensoesCorretas() {
        int linhas = 8;
        int colunas = 12;
        MontarTabuleiro acao = new MontarTabuleiro(linhas, colunas, 10);
        acao.visitTabuleiro(tabuleiro);

        assertEquals(linhas, tabuleiro.getLinha_size());
        assertEquals(colunas, tabuleiro.getColuna_size());
    }

    @Test
    @DisplayName("Deve popular o tabuleiro com a quantidade de bombas especificada")
    void visitTabuleiro_criaComBombasCorretas() throws ForaDoTabuleiroException {
        int linhas = 10;
        int colunas = 10;
        int bombas = 20;
        MontarTabuleiro acao = new MontarTabuleiro(linhas, colunas, bombas);
        acao.visitTabuleiro(tabuleiro);

        long contagemDeBombas = 0;
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                if (tabuleiro.isBomba(new Localizacao(i, j))) {
                    contagemDeBombas++;
                }
            }
        }
        assertEquals(bombas, contagemDeBombas);
    }

    @Test
    @DisplayName("Deve gerar tabuleiros diferentes em chamadas diferentes (aleatoriedade)")
    void visitTabuleiro_geraComAleatoriedade() throws ForaDoTabuleiroException, Exception {
        // Gera o primeiro tabuleiro e armazena a posição das bombas
        MontarTabuleiro acao1 = new MontarTabuleiro(10, 10, 40);
        acao1.visitTabuleiro(tabuleiro);
        Set<Localizacao> bombas1 = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Localizacao loc = new Localizacao(i,j);
                if (tabuleiro.isBomba(loc)) {
                    bombas1.add(loc);
                }
            }
        }

        // Reseta o tabuleiro e gera um segundo
        setUp(); // Chama o @BeforeEach manualmente para resetar
        MontarTabuleiro acao2 = new MontarTabuleiro(10, 10, 40);
        acao2.visitTabuleiro(tabuleiro);
        Set<Localizacao> bombas2 = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Localizacao loc = new Localizacao(i,j);
                if (tabuleiro.isBomba(loc)) {
                    bombas2.add(loc);
                }
            }
        }

        // É estatisticamente muito improvável que os dois conjuntos de bombas sejam idênticos
        assertNotEquals(bombas1, bombas2, "Dois tabuleiros gerados não deveriam ter a mesma distribuição de bombas.");
    }
}