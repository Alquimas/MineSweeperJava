package org.minesweeper.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.minesweeper.exceptions.ForaDoTabuleiroException;
import org.minesweeper.model.Localizacao;
import org.minesweeper.model.Quadrado;
import org.minesweeper.model.Tabuleiro;
import org.minesweeper.model.TabuleiroFront;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MontarTabuleiroTest {

    @Mock
    private Tabuleiro mockTabuleiro;

    @Mock
    private Random mockRandom; // Mock para controlar a aleatoriedade

    @Nested
    @DisplayName("Testes para visitTabuleiro(Tabuleiro)")
    class VisitTabuleiroTests {

        @Test
        @DisplayName("Deve inicializar o tabuleiro com o tamanho e a quantidade de bombas corretos")
        void testTamanhoEBombas() throws ForaDoTabuleiroException {
            // Arrange
            int linhas = 5;
            int colunas = 5;
            int bombas = 10;
            int total = linhas * colunas;
            MontarTabuleiro acao = new MontarTabuleiro(linhas, colunas, bombas);

            // ArgumentCaptor para capturar os quadrados adicionados
            ArgumentCaptor<Quadrado> quadradoCaptor = ArgumentCaptor.forClass(Quadrado.class);

            // Act
            acao.visitTabuleiro(mockTabuleiro);

            // Assert
            // 1. Verifica se o tabuleiro foi inicializado com o tamanho correto
            verify(mockTabuleiro).inicializaTabuleiroVazio(linhas, colunas);

            // 2. Captura todos os quadrados que foram adicionados e verifica a quantidade
            verify(mockTabuleiro, times(total)).adicionaQuadrado(quadradoCaptor.capture());

            // 3. Verifica a contagem de bombas nos quadrados capturados
            List<Quadrado> quadradosAdicionados = quadradoCaptor.getAllValues();
            assertEquals(total, quadradosAdicionados.size());
            long contagemDeBombas = quadradosAdicionados.stream().filter(Quadrado::isBomba).count();
            assertEquals(bombas, contagemDeBombas, "A quantidade de bombas deve ser a mesma informada.");
        }

        @Test
        @DisplayName("Deve definir a localização correta para cada quadrado adicionado")
        void testLocalizacaoCorretaDosQuadrados() throws ForaDoTabuleiroException {
            // Arrange
            int linhas = 2;
            int colunas = 2;
            MontarTabuleiro acao = new MontarTabuleiro(linhas, colunas, 1);
            ArgumentCaptor<Quadrado> quadradoCaptor = ArgumentCaptor.forClass(Quadrado.class);

            // Act
            acao.visitTabuleiro(mockTabuleiro);

            // Assert
            verify(mockTabuleiro, times(4)).adicionaQuadrado(quadradoCaptor.capture());
            List<Quadrado> quadrados = quadradoCaptor.getAllValues();

            // Verifica se a localização de cada quadrado corresponde à sua ordem de inserção
            Localizacao loc00 = quadrados.get(0).getLocalizacao();
            assertEquals(0, loc00.getLinha());
            assertEquals(0, loc00.getColuna());

            Localizacao loc01 = quadrados.get(1).getLocalizacao();
            assertEquals(0, loc01.getLinha());
            assertEquals(1, loc01.getColuna());

            Localizacao loc10 = quadrados.get(2).getLocalizacao();
            assertEquals(1, loc10.getLinha());
            assertEquals(0, loc10.getColuna());

            Localizacao loc11 = quadrados.get(3).getLocalizacao();
            assertEquals(1, loc11.getLinha());
            assertEquals(1, loc11.getColuna());
        }

        @Test
        @DisplayName("Deve usar o gerador de números aleatórios para embaralhar os quadrados")
        void testAleatoriedade() throws ForaDoTabuleiroException {
            // Arrange
            // Cenário: tabuleiro 2x2 com 1 bomba. Lista inicial: [S, S, S, B]
            int linhas = 2, colunas = 2, bombas = 1;

            // Criamos uma instância de MontarTabuleiro, mas injetamos nosso mock de Random nela
            MontarTabuleiro acao = new MontarTabuleiro(linhas, colunas, bombas);
            try {
                java.lang.reflect.Field randField = MontarTabuleiro.class.getDeclaredField("rand");
                randField.setAccessible(true);
                randField.set(acao, mockRandom);
            } catch (Exception e) {
                fail("Falha ao injetar o mock de Random via reflection.");
            }

            // Controlamos a sequência de números "aleatórios"
            // rand.nextInt(4) -> retorna 3 (pega a bomba, último elemento)
            // rand.nextInt(3) -> retorna 1 (pega o segundo 'S')
            // rand.nextInt(2) -> retorna 1 (pega o último 'S' restante)
            // rand.nextInt(1) -> retorna 0 (pega o primeiro 'S')
            when(mockRandom.nextInt(anyInt()))
                    .thenReturn(3) // Primeira chamada, com 4 elementos
                    .thenReturn(1) // Segunda chamada, com 3 elementos
                    .thenReturn(1) // Terceira chamada, com 2 elementos
                    .thenReturn(0); // Última chamada, com 1 elemento

            ArgumentCaptor<Quadrado> quadradoCaptor = ArgumentCaptor.forClass(Quadrado.class);

            // Act
            acao.visitTabuleiro(mockTabuleiro);

            // Assert
            verify(mockTabuleiro, times(4)).adicionaQuadrado(quadradoCaptor.capture());
            List<Quadrado> quadrados = quadradoCaptor.getAllValues();

            // Verifica se a ordem de adição seguiu a nossa aleatoriedade controlada
            assertTrue(quadrados.get(0).isBomba(), "O primeiro quadrado adicionado deveria ser a bomba.");
            assertFalse(quadrados.get(1).isBomba(), "O segundo quadrado deveria ser seguro.");
            assertFalse(quadrados.get(2).isBomba(), "O terceiro quadrado deveria ser seguro.");
            assertFalse(quadrados.get(3).isBomba(), "O quarto quadrado deveria ser seguro.");
        }

        @Test
        @DisplayName("Deve retornar um TabuleiroFront com as dimensões corretas")
        void testRetornoTabuleiroFront() {
            // Arrange
            int linhas = 10;
            int colunas = 20;
            MontarTabuleiro acao = new MontarTabuleiro(linhas, colunas, 5);

            // Act
            TabuleiroFront resultado = (TabuleiroFront) acao.visitTabuleiro(mockTabuleiro);

            // Assert
            assertNotNull(resultado);
            assertEquals(linhas, resultado.getLinha_size());
            assertEquals(colunas, resultado.getColuna_size());
        }
    }
}