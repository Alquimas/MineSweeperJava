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

import static org.junit.jupiter.api.Assertions.*;

class MarcarQuadradoIntegrationTest {

    private Tabuleiro tabuleiro;

    @BeforeEach
    void setUp() throws Exception {
        Field instanceField = Tabuleiro.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
        tabuleiro = Tabuleiro.getInstance();
        tabuleiro.inicializaTabuleiroVazio(5, 5);
        for (int i=0; i<5; i++) {
            for(int j=0; j<5; j++) {
                Quadrado q = new Quadrado(false, false, false);
                q.setLocalizacao(new Localizacao(i,j));
                tabuleiro.adicionaQuadrado(q);
            }
        }
    }

    @Test
    @DisplayName("Caso onde localizacao não é válida: retorna null")
    void visitTabuleiro_comLocalizacaoInvalida_retornaNull() {
        MarcarQuadrado acao = new MarcarQuadrado(new Localizacao(10, 10));
        assertNull(acao.visitTabuleiro(tabuleiro));
    }

    @Test
    @DisplayName("Caso onde localizacao aponta para um quadrado aberto: retorna null")
    void visitTabuleiro_comQuadradoAberto_retornaNull() throws ForaDoTabuleiroException {
        Localizacao loc = new Localizacao(2, 2);
        tabuleiro.setAberto(loc);

        MarcarQuadrado acao = new MarcarQuadrado(loc);
        assertNull(acao.visitTabuleiro(tabuleiro));
        assertFalse(tabuleiro.isMarcado(loc)); // Garante que não marcou
    }

    @Test
    @DisplayName("Caso onde localizacao aponta para um quadrado marcado: desmarca e retorna info")
    void visitTabuleiro_comQuadradoMarcado_desmarcaERetornaInfo() throws ForaDoTabuleiroException {
        Localizacao loc = new Localizacao(3, 3);
        tabuleiro.setMarcado(loc);
        assertTrue(tabuleiro.isMarcado(loc)); // Pré-condição

        MarcarQuadrado acao = new MarcarQuadrado(loc);
        QuadradoFront resultado = (QuadradoFront) acao.visitTabuleiro(tabuleiro);

        assertFalse(tabuleiro.isMarcado(loc), "O quadrado deveria estar desmarcado no tabuleiro.");
        assertNotNull(resultado);
        assertFalse(resultado.isMarcado());
    }

    @Test
    @DisplayName("Caso onde localizacao aponta para um quadrado não marcado: marca e retorna info")
    void visitTabuleiro_comQuadradoNaoMarcado_marcaERetornaInfo() throws ForaDoTabuleiroException {
        Localizacao loc = new Localizacao(4, 4);
        assertFalse(tabuleiro.isMarcado(loc)); // Pré-condição

        MarcarQuadrado acao = new MarcarQuadrado(loc);
        QuadradoFront resultado = (QuadradoFront) acao.visitTabuleiro(tabuleiro);

        assertTrue(tabuleiro.isMarcado(loc), "O quadrado deveria estar marcado no tabuleiro.");
        assertNotNull(resultado);
        assertTrue(resultado.isMarcado());
    }
}