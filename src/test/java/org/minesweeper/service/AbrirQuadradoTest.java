package org.minesweeper.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.minesweeper.exceptions.ForaDoTabuleiroException;
import org.minesweeper.model.Localizacao;
import org.minesweeper.model.QuadradoFront;
import org.minesweeper.model.Tabuleiro;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AbrirQuadradoTest {

    @Mock
    private Tabuleiro mockTabuleiro;

    @Nested
    @DisplayName("Testes para visitTabuleiro (Casos Simples)")
    class CasosSimples {
        @Test
        @DisplayName("Deve retornar null se o quadrado já está aberto")
        void quandoQuadradoAberto_retornaNull() throws ForaDoTabuleiroException {
            Localizacao loc = new Localizacao(1, 1);
            when(mockTabuleiro.isAberto(loc)).thenReturn(true);
            AbrirQuadrado acao = new AbrirQuadrado(loc);
            assertNull(acao.visitTabuleiro(mockTabuleiro));
        }

        @Test
        @DisplayName("Deve retornar null se o quadrado está marcado")
        void quandoQuadradoMarcado_retornaNull() throws ForaDoTabuleiroException {
            Localizacao loc = new Localizacao(1, 1);
            when(mockTabuleiro.isAberto(loc)).thenReturn(false);
            when(mockTabuleiro.isMarcado(loc)).thenReturn(true);
            AbrirQuadrado acao = new AbrirQuadrado(loc);
            assertNull(acao.visitTabuleiro(mockTabuleiro));
        }

        @Test
        @DisplayName("Deve retornar null se a localização for inválida")
        void quandoLocalizacaoInvalida_retornaNull() throws ForaDoTabuleiroException {
            Localizacao loc = new Localizacao(-1, 1);
            when(mockTabuleiro.isAberto(loc)).thenThrow(new ForaDoTabuleiroException());
            AbrirQuadrado acao = new AbrirQuadrado(loc);
            assertNull(acao.visitTabuleiro(mockTabuleiro));
        }

        @Test
        @DisplayName("Deve retornar QuadradoFront de bomba se for uma bomba")
        void quandoQuadradoEhBomba_retornaListaComBomba() throws ForaDoTabuleiroException {
            Localizacao loc = new Localizacao(2, 2);
            when(mockTabuleiro.isAberto(loc)).thenReturn(false);
            when(mockTabuleiro.isMarcado(loc)).thenReturn(false);
            when(mockTabuleiro.isBomba(loc)).thenReturn(true);

            AbrirQuadrado acao = new AbrirQuadrado(loc);
            ArrayList<QuadradoFront> resultado = acao.visitTabuleiro(mockTabuleiro);

            verify(mockTabuleiro).setAberto(loc);
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            QuadradoFront qf = resultado.get(0);
            assertTrue(qf.isBomba());
            assertTrue(qf.isAberto());
        }

        @Test
        @DisplayName("Deve retornar QuadradoFront com número de vizinhos se > 0")
        void quandoVizinhosPerigososMaiorQueZero_retornaListaComNumero() throws ForaDoTabuleiroException {
            Localizacao loc = new Localizacao(3, 3);
            when(mockTabuleiro.isAberto(loc)).thenReturn(false);
            when(mockTabuleiro.isMarcado(loc)).thenReturn(false);
            when(mockTabuleiro.isBomba(loc)).thenReturn(false);
            when(mockTabuleiro.quantVizinhosPerigosos(loc)).thenReturn(3);

            AbrirQuadrado acao = new AbrirQuadrado(loc);
            ArrayList<QuadradoFront> resultado = acao.visitTabuleiro(mockTabuleiro);

            verify(mockTabuleiro).setAberto(loc);
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            QuadradoFront qf = resultado.get(0);
            assertFalse(qf.isBomba());
            assertTrue(qf.isAberto());
            assertEquals(3, qf.getNumero());
        }
    }

    @Nested
    @DisplayName("Testes para abreVizinhos (Lógica Recursiva)")
    class CasosRecursivos {
        @Test
        @DisplayName("Deve abrir todos os vizinhos recursivamente quando não há bombas ao redor")
        void quandoVizinhosPerigososIgualZero_retornaListaDeVizinhosAbertos() throws ForaDoTabuleiroException {
            // Arrange: Cenário complexo
            // Quadrado (1,1) é o alvo inicial, com 0 vizinhos.
            // Vizinho (0,1) tem 2 vizinhos perigosos.
            // Vizinho (2,2) também será aberto, mas já está aberto (deve retornar null).
            // Todos os outros vizinhos de (1,1) estão marcados (devem retornar null).
            Localizacao locInicial = new Localizacao(1, 1);
            Localizacao locVizinho1 = new Localizacao(0, 1);
            Localizacao locVizinhoJaAberto = new Localizacao(2, 2);

            // Comportamento do quadrado inicial (1,1)
            when(mockTabuleiro.isAberto(locInicial)).thenReturn(false);
            when(mockTabuleiro.isMarcado(locInicial)).thenReturn(false);
            when(mockTabuleiro.isBomba(locInicial)).thenReturn(false);
            when(mockTabuleiro.quantVizinhosPerigosos(locInicial)).thenReturn(0);

            // Comportamento do vizinho (0,1) que deve ser aberto
            when(mockTabuleiro.isAberto(locVizinho1)).thenReturn(false);
            when(mockTabuleiro.isMarcado(locVizinho1)).thenReturn(false);
            when(mockTabuleiro.isBomba(locVizinho1)).thenReturn(false);
            when(mockTabuleiro.quantVizinhosPerigosos(locVizinho1)).thenReturn(2);

            // Comportamento do vizinho (2,2) que já está aberto
            when(mockTabuleiro.isAberto(locVizinhoJaAberto)).thenReturn(true);

            // Comportamento de todos os outros 6 vizinhos (marcados)
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if ((i == 0 && j == 0) || (i == -1 && j == 0) || (i == 1 && j == 1)) continue; // Pula o inicial, o vizinho1 e o já aberto
                    Localizacao locMarcado = new Localizacao(1 + i, 1 + j);
                    when(mockTabuleiro.isAberto(locMarcado)).thenReturn(false);
                    when(mockTabuleiro.isMarcado(locMarcado)).thenReturn(true);
                }
            }

            // Act
            AbrirQuadrado acao = new AbrirQuadrado(locInicial);
            ArrayList<QuadradoFront> resultado = acao.visitTabuleiro(mockTabuleiro);

            // Assert
            assertNotNull(resultado);
            // Deve conter o quadrado inicial (num=0) e o vizinho (num=2)
            assertEquals(2, resultado.size(), "A lista final deve conter 2 quadrados abertos.");

            // Verifica se o quadrado inicial está na lista
            Optional<QuadradoFront> qfInicial = resultado.stream().filter(q -> q.getLocalizacao().equals(locInicial)).findFirst();
            assertTrue(qfInicial.isPresent());
            assertEquals(0, qfInicial.get().getNumero());

            // Verifica se o vizinho recursivo está na lista
            Optional<QuadradoFront> qfVizinho = resultado.stream().filter(q -> q.getLocalizacao().equals(locVizinho1)).findFirst();
            assertTrue(qfVizinho.isPresent());
            assertEquals(2, qfVizinho.get().getNumero());
        }

        @Test
        @DisplayName("Deve abrir em cascata quando um vizinho também tem 0 vizinhos perigosos")
        void quandoVizinhoTambemTemZeroVizinhos_AbreRecursivamente() throws ForaDoTabuleiroException {
            // ARRANGE: Cenário de recursão em 2 níveis
            // (1,1) -> Inicia. Abre vizinhos.
            // (0,1) -> Vizinho de (1,1). Também tem 0 bombas, então abre seus próprios vizinhos.
            // (0,0) -> Vizinho de (0,1). Tem 1 bomba, termina a recursão aqui.
            // (1,0) -> Vizinho de (0,1). Tem 2 bombas, termina a recursão aqui.
            // Outros -> Marcados ou abertos para não participarem do resultado.

            // --- Define as localizações para clareza ---
            Localizacao locInicial = new Localizacao(1, 1);
            Localizacao locVizinhoRecursivo = new Localizacao(0, 1);
            Localizacao locTerminal1 = new Localizacao(0, 0);
            Localizacao locTerminal2 = new Localizacao(1, 0);
            Localizacao locMarcado = new Localizacao(0, 2);
            Localizacao locJaAberto = new Localizacao(1, 2);

            // --- Configura o comportamento do Mock para cada localização ---

            // 1. Quadrado inicial (1,1) -> Inicia a cascata
            when(mockTabuleiro.isAberto(locInicial)).thenReturn(false);
            when(mockTabuleiro.isMarcado(locInicial)).thenReturn(false);
            when(mockTabuleiro.isBomba(locInicial)).thenReturn(false);
            when(mockTabuleiro.quantVizinhosPerigosos(locInicial)).thenReturn(0);

            // 2. Vizinho (0,1) -> Continua a cascata
            when(mockTabuleiro.isAberto(locVizinhoRecursivo)).thenReturn(false);
            when(mockTabuleiro.isMarcado(locVizinhoRecursivo)).thenReturn(false);
            when(mockTabuleiro.isBomba(locVizinhoRecursivo)).thenReturn(false);
            when(mockTabuleiro.quantVizinhosPerigosos(locVizinhoRecursivo)).thenReturn(0);

            // 3. Vizinho terminal (0,0) -> Para a recursão com valor 1
            when(mockTabuleiro.isAberto(locTerminal1)).thenReturn(false);
            when(mockTabuleiro.isMarcado(locTerminal1)).thenReturn(false);
            when(mockTabuleiro.isBomba(locTerminal1)).thenReturn(false);
            when(mockTabuleiro.quantVizinhosPerigosos(locTerminal1)).thenReturn(1);

            // 4. Vizinho terminal (1,0) -> Para a recursão com valor 2
            when(mockTabuleiro.isAberto(locTerminal2)).thenReturn(false);
            when(mockTabuleiro.isMarcado(locTerminal2)).thenReturn(false);
            when(mockTabuleiro.isBomba(locTerminal2)).thenReturn(false);
            when(mockTabuleiro.quantVizinhosPerigosos(locTerminal2)).thenReturn(2);

            // 5. Outros vizinhos que não devem ser abertos
            when(mockTabuleiro.isMarcado(locMarcado)).thenReturn(true);
            when(mockTabuleiro.isAberto(locJaAberto)).thenReturn(true);
            // ... (para um teste completo, todos os outros vizinhos seriam configurados)

            // ACT
            AbrirQuadrado acao = new AbrirQuadrado(locInicial);
            ArrayList<QuadradoFront> resultado = acao.visitTabuleiro(mockTabuleiro);

            // ASSERT
            assertNotNull(resultado);
            // Esperamos 4 quadrados: o inicial (1,1), o vizinho recursivo (0,1), e os dois terminais (0,0) e (1,0)
            assertEquals(4, resultado.size(), "A lista final deve conter 4 quadrados abertos pela cascata.");

            // Verifica se cada quadrado esperado está na lista com seu número correto de vizinhos
            assertQuadradoNaLista(resultado, locInicial, 0);
            assertQuadradoNaLista(resultado, locVizinhoRecursivo, 0);
            assertQuadradoNaLista(resultado, locTerminal1, 1);
            assertQuadradoNaLista(resultado, locTerminal2, 2);
        }

        /**
         * Método auxiliar para verificar se um QuadradoFront com uma localização e número
         * específicos existe dentro da lista de resultados.
         */
        private void assertQuadradoNaLista(List<QuadradoFront> lista, Localizacao loc, int numEsperado) {
            Optional<QuadradoFront> quadrado = lista.stream()
                    .filter(q -> q.getLocalizacao().equals(loc))
                    .findFirst();

            assertTrue(quadrado.isPresent(), "O quadrado na localização " + loc.getLinha() + "," + loc.getColuna() + " deveria estar na lista.");
            quadrado.ifPresent(q -> {
                assertEquals(numEsperado, q.getNumero(), "O número de vizinhos para o quadrado " + loc.getLinha() + "," + loc.getColuna() + " está incorreto.");
                assertTrue(q.isAberto(), "O quadrado deveria estar marcado como aberto.");
            });
        }
    }
}