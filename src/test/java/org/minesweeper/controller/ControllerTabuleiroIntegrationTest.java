package org.minesweeper.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.minesweeper.exceptions.ForaDoTabuleiroException;
import org.minesweeper.model.Localizacao;
import org.minesweeper.model.Quadrado;
import org.minesweeper.model.QuadradoFront;
import org.minesweeper.model.Tabuleiro;
import org.minesweeper.model.TabuleiroFront;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTabuleiroIntegrationTest {

    private ControllerTabuleiro controller;
    private Tabuleiro tabuleiro;

    // Antes de cada teste, reseta AMBOS os Singletons para garantir isolamento total
    @BeforeEach
    void setUp() throws Exception {
        // Reseta o Singleton do Controller
        Field controllerInstance = ControllerTabuleiro.class.getDeclaredField("instance");
        controllerInstance.setAccessible(true);
        controllerInstance.set(null, null);

        // Reseta o Singleton do Tabuleiro
        Field tabuleiroInstance = Tabuleiro.class.getDeclaredField("instance");
        tabuleiroInstance.setAccessible(true);
        tabuleiroInstance.set(null, null);

        // Obtém as novas instâncias reais
        controller = ControllerTabuleiro.getInstance();
        tabuleiro = Tabuleiro.getInstance();
    }

    @Nested
    @DisplayName("Testes de Integração para iniciarNovoJogo")
    class IniciarNovoJogoIntegrationTests {
        @Test
        @DisplayName("Deve retornar um TabuleiroFront e configurar o Tabuleiro real corretamente")
        void iniciarNovoJogo_configuraModeloERetornaFront() throws ForaDoTabuleiroException {
            // Arrange
            int linhas = 8;
            int colunas = 10;
            int bombas = 15;

            // Act
            TabuleiroFront resultadoFront = controller.iniciarNovoJogo(linhas, colunas, bombas);

            // Assert
            // 1. Verifica o objeto retornado pelo Controller
            assertNotNull(resultadoFront);
            assertEquals(linhas, resultadoFront.getLinha_size());
            assertEquals(colunas, resultadoFront.getColuna_size());

            // 2. Verifica o ESTADO REAL do modelo Tabuleiro após a ação
            assertEquals(linhas, tabuleiro.getLinha_size());
            assertEquals(colunas, tabuleiro.getColuna_size());

            long contagemDeBombas = 0;
            for (int i = 0; i < linhas; i++) {
                for (int j = 0; j < colunas; j++) {
                    if (tabuleiro.isBomba(new Localizacao(i, j))) {
                        contagemDeBombas++;
                    }
                }
            }
            assertEquals(bombas, contagemDeBombas, "A quantidade de bombas no tabuleiro real deve ser a correta.");
        }
    }

    @Nested
    @DisplayName("Testes de Integração para clicarBotaoDireito")
    class ClicarBotaoDireitoIntegrationTests {
        private final Localizacao loc = new Localizacao(2, 2);

        // Popula o tabuleiro real antes de cada teste neste grupo
        @BeforeEach
        void setupBoard() {
            tabuleiro.inicializaTabuleiroVazio(5, 5);
        }

        @Test
        @DisplayName("Deve marcar um quadrado desmarcado no tabuleiro real")
        void clicarBotaoDireito_marcaQuadradoDesmarcado() throws ForaDoTabuleiroException {
            // Pré-condição: Garante que o quadrado está desmarcado
            assertFalse(tabuleiro.isMarcado(loc));

            // Act
            QuadradoFront resultado = (QuadradoFront) controller.clicarBotaoDireito(loc);

            // Assert
            // 1. Verifica o retorno
            assertNotNull(resultado);
            assertTrue(resultado.isMarcado());

            // 2. Verifica o estado final do tabuleiro real
            assertTrue(tabuleiro.isMarcado(loc), "O quadrado deveria estar marcado no tabuleiro real.");
        }

        @Test
        @DisplayName("Deve desmarcar um quadrado marcado no tabuleiro real")
        void clicarBotaoDireito_desmarcaQuadradoMarcado() throws ForaDoTabuleiroException {
            // Arrange: Coloca o tabuleiro no estado desejado
            tabuleiro.setMarcado(loc);
            assertTrue(tabuleiro.isMarcado(loc)); // Pré-condição

            // Act
            QuadradoFront resultado = (QuadradoFront) controller.clicarBotaoDireito(loc);

            // Assert
            assertNotNull(resultado);
            assertFalse(resultado.isMarcado());
            assertFalse(tabuleiro.isMarcado(loc), "O quadrado deveria estar desmarcado no tabuleiro real.");
        }

        @Test
        @DisplayName("Deve retornar null para um quadrado já aberto")
        void clicarBotaoDireito_emQuadradoAberto_retornaNull() throws ForaDoTabuleiroException {
            // Arrange
            tabuleiro.setAberto(loc);

            // Act
            QuadradoFront resultado = (QuadradoFront) controller.clicarBotaoDireito(loc);

            // Assert
            assertNull(resultado);
            assertFalse(tabuleiro.isMarcado(loc), "O estado do tabuleiro não deveria ter sido alterado.");
        }
    }

    @Nested
    @DisplayName("Testes de Integração para clicarBotaoEsquerdo")
    class ClicarBotaoEsquerdoIntegrationTests {
        private final Localizacao locBomba = new Localizacao(1, 1);
        private final Localizacao locSegura = new Localizacao(2, 2);

        // Cria um tabuleiro previsível com uma bomba em (1,1)
        @BeforeEach
        void setupBoard() throws ForaDoTabuleiroException {
            tabuleiro.inicializaTabuleiroVazio(5, 5);
            for (int i=0; i<5; i++) {
                for (int j=0; j<5; j++) {
                    boolean temBomba = (i == locBomba.getLinha() && j == locBomba.getColuna());
                    Quadrado q = new Quadrado(temBomba, false, false);
                    q.setLocalizacao(new Localizacao(i,j));
                    tabuleiro.adicionaQuadrado(q);
                }
            }
        }

        @Test
        @DisplayName("Deve abrir um quadrado seguro e retornar sua informação")
        void clicarBotaoEsquerdo_emQuadradoSeguro_retornaArrayList() throws ForaDoTabuleiroException {
            // Act
            ArrayList<QuadradoFront> resultado = (ArrayList<QuadradoFront>) controller.clicarBotaoEsquerdo(locSegura);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals(1, resultado.get(0).getNumero(), "Deveria ter 1 vizinho perigoso (a bomba em 1,1).");
            assertTrue(tabuleiro.isAberto(locSegura), "O quadrado deveria estar aberto no tabuleiro real.");
        }

        @Test
        @DisplayName("Deve retornar null para um quadrado marcado")
        void clicarBotaoEsquerdo_emQuadradoMarcado_retornaNull() throws ForaDoTabuleiroException {
            // Arrange
            tabuleiro.setMarcado(locSegura);

            // Act
            ArrayList<QuadradoFront> resultado = (ArrayList<QuadradoFront>) controller.clicarBotaoEsquerdo(locSegura);

            // Assert
            assertNull(resultado);
            assertFalse(tabuleiro.isAberto(locSegura), "O quadrado não deveria ter sido aberto.");
        }
    }
}