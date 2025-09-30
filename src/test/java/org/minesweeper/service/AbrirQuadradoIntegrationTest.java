package org.minesweeper.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.minesweeper.exceptions.ForaDoTabuleiroException;
import org.minesweeper.model.Localizacao;
import org.minesweeper.model.Quadrado;
import org.minesweeper.model.QuadradoFront;
import org.minesweeper.model.Tabuleiro;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AbrirQuadradoIntegrationTest {

    private Tabuleiro tabuleiro;

    // Reseta o singleton do Tabuleiro antes de cada teste para garantir isolamento
    @BeforeEach
    void setUp() throws Exception {
        Field instanceField = Tabuleiro.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
        tabuleiro = Tabuleiro.getInstance();
    }

    /**
     * Cria um tabuleiro 3x3 previsível para os testes.
     * Layout:
     * [ S(1) | B | S(1) ]
     * [ S(1) | S(1)| S(1) ]
     * [ S(0) | S(0)| S(0) ]
     * Onde B é uma bomba em (0,1) e S(n) é um quadrado seguro com n vizinhos perigosos.
     */
    private void criarTabuleiroPrevisivel() throws ForaDoTabuleiroException {
        tabuleiro.inicializaTabuleiroVazio(3, 3);
        boolean[][] layoutBomba = {
                {false, true, false},
                {false, false, false},
                {false, false, false}
        };

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Quadrado q = new Quadrado(layoutBomba[i][j], false, false);
                q.setLocalizacao(new Localizacao(i, j));
                tabuleiro.adicionaQuadrado(q);
            }
        }
    }

    @Test
    @DisplayName("Caso onde localizacao não é válida: retorna null e nada ocorre")
    void visitTabuleiro_comLocalizacaoInvalida_retornaNull() throws ForaDoTabuleiroException {
        criarTabuleiroPrevisivel();
        AbrirQuadrado acao = new AbrirQuadrado(new Localizacao(-1, -1));
        ArrayList<QuadradoFront> resultado = (ArrayList<QuadradoFront>) acao.visitTabuleiro(tabuleiro);
        assertNull(resultado);
    }

    @Test
    @DisplayName("Caso onde localizacao aponta para um quadrado aberto: retorna null")
    void visitTabuleiro_comQuadradoAberto_retornaNull() throws ForaDoTabuleiroException {
        criarTabuleiroPrevisivel();
        Localizacao loc = new Localizacao(2, 2);
        tabuleiro.setAberto(loc); // Pré-condição: abre o quadrado

        AbrirQuadrado acao = new AbrirQuadrado(loc);
        ArrayList<QuadradoFront> resultado = (ArrayList<QuadradoFront>) acao.visitTabuleiro(tabuleiro);

        assertNull(resultado);
        assertTrue(tabuleiro.isAberto(loc)); // Garante que continua aberto
    }

    @Test
    @DisplayName("Caso onde localizacao aponta para um quadrado marcado: retorna null")
    void visitTabuleiro_comQuadradoMarcado_retornaNull() throws ForaDoTabuleiroException {
        criarTabuleiroPrevisivel();
        Localizacao loc = new Localizacao(2, 2);
        tabuleiro.setMarcado(loc); // Pré-condição: marca o quadrado

        AbrirQuadrado acao = new AbrirQuadrado(loc);
        ArrayList<QuadradoFront> resultado = (ArrayList<QuadradoFront>) acao.visitTabuleiro(tabuleiro);

        assertNull(resultado);
        assertFalse(tabuleiro.isAberto(loc)); // Garante que não abriu
        assertTrue(tabuleiro.isMarcado(loc)); // Garante que continua marcado
    }

    @Test
    @DisplayName("Caso onde localizacao aponta para uma bomba: abre o quadrado e retorna info da bomba")
    void visitTabuleiro_comBomba_abreQuadradoERetornaInfo() throws ForaDoTabuleiroException {
        criarTabuleiroPrevisivel();
        Localizacao locBomba = new Localizacao(0, 1);

        AbrirQuadrado acao = new AbrirQuadrado(locBomba);
        ArrayList<QuadradoFront> resultado = (ArrayList<QuadradoFront>) acao.visitTabuleiro(tabuleiro);

        assertTrue(tabuleiro.isAberto(locBomba), "O quadrado da bomba deveria estar aberto no tabuleiro.");
        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        QuadradoFront qf = resultado.get(0);
        assertTrue(qf.isAberto());
        assertTrue(qf.isBomba());
        assertEquals(-1, qf.getNumero());
    }

    @Test
    @DisplayName("Caso onde localizacao tem > 0 vizinhos perigosos: abre e retorna info")
    void visitTabuleiro_comVizinhosPerigosos_abreERetornaInfo() throws ForaDoTabuleiroException {
        criarTabuleiroPrevisivel();
        Localizacao loc = new Localizacao(1, 1); // Este quadrado tem 1 vizinho perigoso (a bomba)

        AbrirQuadrado acao = new AbrirQuadrado(loc);
        ArrayList<QuadradoFront> resultado = (ArrayList<QuadradoFront>) acao.visitTabuleiro(tabuleiro);

        assertTrue(tabuleiro.isAberto(loc), "O quadrado deveria estar aberto no tabuleiro.");
        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        QuadradoFront qf = resultado.get(0);
        assertTrue(qf.isAberto());
        assertFalse(qf.isBomba());
        assertEquals(1, qf.getNumero());
    }

    @Test
    @DisplayName("Caso onde localizacao tem 0 vizinhos perigosos: abre em cascata")
    void visitTabuleiro_comZeroVizinhosPerigosos_abreEmCascata() throws ForaDoTabuleiroException {
        criarTabuleiroPrevisivel();
        Localizacao locInicial = new Localizacao(2, 0); // Este quadrado tem 0 vizinhos perigosos

        AbrirQuadrado acao = new AbrirQuadrado(locInicial);
        ArrayList<QuadradoFront> resultado = (ArrayList<QuadradoFront>) acao.visitTabuleiro(tabuleiro);

        // No nosso tabuleiro previsível, abrir (2,0) deve abrir 6 quadrados seguros
        assertEquals(6, resultado.size(), "Deveria ter aberto 6 quadrados em cascata.");

        // Verifica o estado final no tabuleiro real
        assertTrue(tabuleiro.isAberto(new Localizacao(1,1)));
        assertTrue(tabuleiro.isAberto(new Localizacao(2,2)));
        assertFalse(tabuleiro.isAberto(new Localizacao(0,1))); // A bomba não deve ser aberta
    }
}