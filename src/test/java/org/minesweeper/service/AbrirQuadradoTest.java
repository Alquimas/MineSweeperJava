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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

        // Substitua o método de teste existente por este em AbrirQuadradoTest.java

        @Test
        @DisplayName("Deve abrir em cascata e lidar com vizinhos fora do tabuleiro")
        void quandoVizinhoTambemTemZeroVizinhos_AbreRecursivamente() throws ForaDoTabuleiroException {
            // ARRANGE: Cenário de recursão com configuração para vizinhos fora do tabuleiro.

            record EstadoQuadrado(boolean aberto, boolean marcado, boolean bomba, int numVizinhos) {}
            Map<Localizacao, EstadoQuadrado> tabuleiroVirtual = new HashMap<>();

            // --- Define as localizações para clareza ---
            Localizacao locInicial = new Localizacao(1, 1);
            Localizacao locVizinhoRecursivo = new Localizacao(0, 1);
            Localizacao locTerminal1 = new Localizacao(0, 0);
            Localizacao locTerminal2 = new Localizacao(1, 0);

            // --- Popula o nosso tabuleiro virtual com o estado inicial ---
            tabuleiroVirtual.put(locInicial, new EstadoQuadrado(false, false, false, 0));
            tabuleiroVirtual.put(locVizinhoRecursivo, new EstadoQuadrado(false, false, false, 0));
            tabuleiroVirtual.put(locTerminal1, new EstadoQuadrado(false, false, false, 1));
            tabuleiroVirtual.put(locTerminal2, new EstadoQuadrado(false, false, false, 2));

            // Configura os demais vizinhos como marcados
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0) continue;
                    tabuleiroVirtual.putIfAbsent(new Localizacao(1 + i, 1 + j), new EstadoQuadrado(false, true, false, 0));
                    tabuleiroVirtual.putIfAbsent(new Localizacao(0 + i, 1 + j), new EstadoQuadrado(false, true, false, 0));
                }
            }

            // --- Define as localizações que devem lançar exceção ---
            Set<Localizacao> locsForaDoTabuleiro = Set.of(
                    new Localizacao(-1, 0),
                    new Localizacao(-1, 1),
                    new Localizacao(-1, 2)
            );

            // --- Configura o Mock para usar o Tabuleiro Virtual com a nova lógica ---
            when(mockTabuleiro.isAberto(any(Localizacao.class))).thenAnswer(invocation -> {
                Localizacao loc = invocation.getArgument(0);

                // NOVA LÓGICA: Verifica se é uma localização que deve lançar exceção
                if (locsForaDoTabuleiro.contains(loc)) {
                    throw new ForaDoTabuleiroException();
                }

                // Lógica antiga para consultar o mapa
                return tabuleiroVirtual.getOrDefault(loc, new EstadoQuadrado(true, false, false, -1)).aberto();
            });

            // Configura os outros métodos de leitura (isMarcado, etc.)
            when(mockTabuleiro.isMarcado(any(Localizacao.class))).thenAnswer(invocation -> {
                Localizacao loc = invocation.getArgument(0);
                if (locsForaDoTabuleiro.contains(loc)) { // Consistência
                    throw new ForaDoTabuleiroException();
                }
                return tabuleiroVirtual.getOrDefault(loc, new EstadoQuadrado(false, true, false, -1)).marcado();
            });
            when(mockTabuleiro.quantVizinhosPerigosos(any(Localizacao.class))).thenAnswer(invocation -> {
                Localizacao loc = invocation.getArgument(0);
                return tabuleiroVirtual.getOrDefault(loc, new EstadoQuadrado(false, false, false, -1)).numVizinhos();
            });

            // Configura a mudança de estado
            doAnswer(invocation -> {
                Localizacao loc = invocation.getArgument(0);
                if (tabuleiroVirtual.containsKey(loc)) {
                    EstadoQuadrado estadoAntigo = tabuleiroVirtual.get(loc);
                    tabuleiroVirtual.put(loc, new EstadoQuadrado(true, estadoAntigo.marcado(), estadoAntigo.bomba(), estadoAntigo.numVizinhos()));
                }
                return null;
            }).when(mockTabuleiro).setAberto(any(Localizacao.class));

            // ACT
            AbrirQuadrado acao = new AbrirQuadrado(locInicial);
            ArrayList<QuadradoFront> resultado = acao.visitTabuleiro(mockTabuleiro);

            // ASSERT
            // O resultado esperado não muda, pois a exceção deve ser tratada e não adicionar quadrados à lista.
            assertNotNull(resultado);
            assertEquals(4, resultado.size());

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
                    .filter(q -> q.getLocalizacao().getLinha() == loc.getLinha() && q.getLocalizacao().getColuna() == loc.getColuna())
                    .findFirst();

            assertTrue(quadrado.isPresent(), "O quadrado na localização " + loc.getLinha() + "," + loc.getColuna() + " deveria estar na lista.");
            quadrado.ifPresent(q -> {
                assertEquals(numEsperado, q.getNumero(), "O número de vizinhos para o quadrado " + loc.getLinha() + "," + loc.getColuna() + " está incorreto.");
                assertTrue(q.isAberto(), "O quadrado deveria estar marcado como aberto.");
            });
        }
    }
}