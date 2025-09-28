package org.minesweeper.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.minesweeper.exceptions.ForaDoTabuleiroException;
import org.minesweeper.model.Localizacao;
import org.minesweeper.model.QuadradoFront;
import org.minesweeper.model.Tabuleiro;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Habilita a integração do Mockito com o JUnit 5
@ExtendWith(MockitoExtension.class)
class MarcarQuadradoTest {

    // Cria um mock (objeto falso) da classe Tabuleiro
    @Mock
    private Tabuleiro mockTabuleiro;

    @Test
    @DisplayName("visitTabuleiro: Caso onde quadrado está aberto - deve retornar null")
    void visitTabuleiro_QuandoQuadradoAberto_RetornaNull() throws ForaDoTabuleiroException {
        // Arrange (Preparação)
        Localizacao loc = new Localizacao(5, 5);

        // Configura o mock: "Quando isAberto for chamado com 'loc', retorne true"
        when(mockTabuleiro.isAberto(loc)).thenReturn(true);

        MarcarQuadrado acao = new MarcarQuadrado(loc);

        // Act (Ação)
        QuadradoFront resultado = acao.visitTabuleiro(mockTabuleiro);

        // Assert (Verificação)
        assertNull(resultado, "O resultado deveria ser nulo para um quadrado já aberto.");

        // Garante que nenhum método de alteração foi chamado
        verify(mockTabuleiro, never()).setMarcado(any(Localizacao.class));
        verify(mockTabuleiro, never()).setDesmarcado(any(Localizacao.class));
    }

    @Test
    @DisplayName("visitTabuleiro: Caso onde quadrado está marcado - deve desmarcar e retornar QuadradoFront correspondente")
    void visitTabuleiro_QuandoQuadradoMarcado_DesmarcaERetornaQuadradoFront() throws ForaDoTabuleiroException {
        // Arrange
        Localizacao loc = new Localizacao(3, 4);

        // Configura o mock para simular um quadrado fechado e marcado
        when(mockTabuleiro.isAberto(loc)).thenReturn(false);
        when(mockTabuleiro.isMarcado(loc)).thenReturn(true);

        MarcarQuadrado acao = new MarcarQuadrado(loc);

        // Act
        QuadradoFront resultado = acao.visitTabuleiro(mockTabuleiro);

        // Assert
        // 1. Verifica se a ação correta foi chamada no tabuleiro
        verify(mockTabuleiro).setDesmarcado(loc);
        verify(mockTabuleiro, never()).setMarcado(any(Localizacao.class));

        // 2. Verifica se o QuadradoFront retornado tem os valores corretos
        assertNotNull(resultado);
        assertFalse(resultado.isAberto(), "Resultado: 'aberto' deveria ser false.");
        assertEquals(-1, resultado.getNumero(), "Resultado: 'numero' deveria ser -1.");
        assertFalse(resultado.isMarcado(), "Resultado: 'marcado' deveria ser false.");
        assertFalse(resultado.isBomba(), "Resultado: 'bomba' deveria ser false.");
        assertSame(loc, resultado.getLocalizacao(), "A localização do resultado deve ser a mesma da ação.");
    }

    @Test
    @DisplayName("visitTabuleiro: Caso onde quadrado está desmarcado - deve marcar e retornar QuadradoFront correspondente")
    void visitTabuleiro_QuandoQuadradoDesmarcado_MarcaERetornaQuadradoFront() throws ForaDoTabuleiroException {
        // Arrange
        Localizacao loc = new Localizacao(8, 1);

        // Configura o mock para simular um quadrado fechado e desmarcado
        when(mockTabuleiro.isAberto(loc)).thenReturn(false);
        when(mockTabuleiro.isMarcado(loc)).thenReturn(false);

        MarcarQuadrado acao = new MarcarQuadrado(loc);

        // Act
        QuadradoFront resultado = acao.visitTabuleiro(mockTabuleiro);

        // Assert
        // 1. Verifica se a ação correta foi chamada no tabuleiro
        verify(mockTabuleiro).setMarcado(loc);
        verify(mockTabuleiro, never()).setDesmarcado(any(Localizacao.class));

        // 2. Verifica se o QuadradoFront retornado tem os valores corretos
        assertNotNull(resultado);
        assertFalse(resultado.isAberto());
        assertEquals(-1, resultado.getNumero());
        assertTrue(resultado.isMarcado(), "Resultado: 'marcado' deveria ser true.");
        assertFalse(resultado.isBomba());
        assertSame(loc, resultado.getLocalizacao());
    }

    @Test
    @DisplayName("visitTabuleiro: Caso onde localização está fora do tabuleiro - deve retornar null")
    void visitTabuleiro_QuandoLocalizacaoInvalida_RetornaNull() throws ForaDoTabuleiroException {
        // Arrange
        Localizacao locFora = new Localizacao(-1, -1);

        // Configura o mock: "Quando isAberto for chamado, lance uma ForaDoTabuleiroException"
        when(mockTabuleiro.isAberto(locFora)).thenThrow(new ForaDoTabuleiroException());

        MarcarQuadrado acao = new MarcarQuadrado(locFora);

        // Act
        QuadradoFront resultado = acao.visitTabuleiro(mockTabuleiro);

        // Assert
        assertNull(resultado, "O resultado deveria ser nulo quando uma exceção é capturada.");

        // Garante que, devido à exceção, nenhum método de alteração foi chamado
        verify(mockTabuleiro, never()).setMarcado(any(Localizacao.class));
        verify(mockTabuleiro, never()).setDesmarcado(any(Localizacao.class));
    }
}