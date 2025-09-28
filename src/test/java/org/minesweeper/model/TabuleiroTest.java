package org.minesweeper.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.minesweeper.exceptions.ForaDoTabuleiroException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TabuleiroTest {

    private Tabuleiro tabuleiro;

    // Antes de cada teste, reseta a instância do Singleton para garantir isolamento.
    @BeforeEach
    void setUp() throws Exception {
        Field instanceField = Tabuleiro.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
        tabuleiro = Tabuleiro.getInstance();
    }

    /**
     * Método auxiliar para injetar um tabuleiro customizado para testes previsíveis.
     */
    private void setCustomTabuleiro(ArrayList<ArrayList<Quadrado>> boardState) {
        try {
            int linhas = boardState.size();
            int colunas = boardState.isEmpty() ? 0 : boardState.get(0).size();

            Field tabuleiroField = Tabuleiro.class.getDeclaredField("tabuleiro");
            tabuleiroField.setAccessible(true);
            tabuleiroField.set(tabuleiro, boardState);

            Field linhaSizeField = Tabuleiro.class.getDeclaredField("linha_size");
            linhaSizeField.setAccessible(true);
            linhaSizeField.set(tabuleiro, linhas);

            Field colunaSizeField = Tabuleiro.class.getDeclaredField("coluna_size");
            colunaSizeField.setAccessible(true);
            colunaSizeField.set(tabuleiro, colunas);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Falha ao configurar o tabuleiro para o teste via reflection: " + e.getMessage());
        }
    }

    @Nested
    @DisplayName("Testes para o Padrão Singleton")
    class SingletonTests {
        @Test
        @DisplayName("Garante que getInstance() sempre retorna a mesma instância")
        void testRetornaMesmaInstancia() {
            Tabuleiro instance1 = Tabuleiro.getInstance();
            Tabuleiro instance2 = Tabuleiro.getInstance();
            assertSame(instance1, instance2, "getInstance deve sempre retornar a mesma instância.");
        }
    }

    @Nested
    @DisplayName("Testes para criaTabuleiro(int, int, int)")
    class CriaTabuleiroTests {
        @Test
        @DisplayName("Cria tabuleiro com tamanho e quantidade de bombas corretos")
        void testTamanhoEBombas() throws ForaDoTabuleiroException {
            tabuleiro.criaTabuleiro(10, 15, 20);
            assertEquals(10, tabuleiro.getLinha_size());
            assertEquals(15, tabuleiro.getColuna_size());

            long bombCount = 0;
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 15; j++) {
                    if (tabuleiro.isBomba(new Localizacao(i, j))) {
                        bombCount++;
                    }
                }
            }
            assertEquals(20, bombCount);
        }

        @Test
        @DisplayName("Verifica se a localização dos quadrados corresponde à sua posição na matriz")
        void testLocalizacaoCorretaDosQuadrados() throws Exception {
            tabuleiro.criaTabuleiro(8, 8, 10);

            Field tabuleiroField = Tabuleiro.class.getDeclaredField("tabuleiro");
            tabuleiroField.setAccessible(true);
            @SuppressWarnings("unchecked")
            ArrayList<ArrayList<Quadrado>> grid = (ArrayList<ArrayList<Quadrado>>) tabuleiroField.get(tabuleiro);

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Localizacao loc = grid.get(i).get(j).getLocalizacao();
                    assertNotNull(loc);
                    assertEquals(i, loc.getLinha());
                    assertEquals(j, loc.getColuna());
                }
            }
        }
    }

    // Substitua a classe aninhada existente por esta em seu arquivo TabuleiroTest.java

    @Nested
    @DisplayName("Testes para quantVizinhosPerigosos(Localizacao)")
    class QuantVizinhosPerigososTests {

        // Método auxiliar para criar um tabuleiro 3x3 com base em uma matriz de booleanos
        private void criarTabuleiro3x3(boolean[][] layoutBombas) {
            ArrayList<ArrayList<Quadrado>> board = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                board.add(new ArrayList<>());
                for (int j = 0; j < 3; j++) {
                    board.get(i).add(new Quadrado(layoutBombas[i][j], false, false));
                }
            }
            // Usa o helper da classe principal para injetar o tabuleiro
            setCustomTabuleiro(board);
        }

        @Test
        @DisplayName("Calcula vizinhos para um quadrado no CENTRO")
        void testVizinhosCentro() {
            // [ B , 0 , B ]
            // [ 0 , X , 0 ]
            // [ B , 0 , B ] X em (1,1)
            boolean[][] layout = {
                    {true, false, true},
                    {false, false, false},
                    {true, false, true}
            };
            criarTabuleiro3x3(layout);
            assertEquals(4, tabuleiro.quantVizinhosPerigosos(new Localizacao(1, 1)));
        }

        @Test
        @DisplayName("Calcula vizinhos para o CANTO SUPERIOR ESQUERDO")
        void testVizinhosCantoSuperiorEsquerdo() {
            // [ X , B , 0 ]
            // [ B , B , 0 ]
            // [ 0 , 0 , 0 ] X em (0,0)
            boolean[][] layout = {
                    {false, true, false},
                    {true, true, false},
                    {false, false, false}
            };
            criarTabuleiro3x3(layout);
            assertEquals(3, tabuleiro.quantVizinhosPerigosos(new Localizacao(0, 0)));
        }

        @Test
        @DisplayName("Calcula vizinhos para o CANTO SUPERIOR DIREITO")
        void testVizinhosCantoSuperiorDireito() {
            // [ 0 , B , X ]
            // [ 0 , B , B ]
            // [ 0 , 0 , 0 ] X em (0,2)
            boolean[][] layout = {
                    {false, true, false},
                    {false, true, true},
                    {false, false, false}
            };
            criarTabuleiro3x3(layout);
            assertEquals(3, tabuleiro.quantVizinhosPerigosos(new Localizacao(0, 2)));
        }

        @Test
        @DisplayName("Calcula vizinhos para o CANTO INFERIOR ESQUERDO")
        void testVizinhosCantoInferiorEsquerdo() {
            // [ 0 , 0 , 0 ]
            // [ B , B , 0 ]
            // [ X , B , 0 ] X em (2,0)
            boolean[][] layout = {
                    {false, false, false},
                    {true, true, false},
                    {false, true, false}
            };
            criarTabuleiro3x3(layout);
            assertEquals(3, tabuleiro.quantVizinhosPerigosos(new Localizacao(2, 0)));
        }

        @Test
        @DisplayName("Calcula vizinhos para o CANTO INFERIOR DIREITO")
        void testVizinhosCantoInferiorDireito() {
            // [ 0 , 0 , 0 ]
            // [ 0 , B , B ]
            // [ 0 , B , X ] X em (2,2)
            boolean[][] layout = {
                    {false, false, false},
                    {false, true, true},
                    {false, true, false}
            };
            criarTabuleiro3x3(layout);
            assertEquals(3, tabuleiro.quantVizinhosPerigosos(new Localizacao(2, 2)));
        }

        @Test
        @DisplayName("Calcula vizinhos para a PRIMEIRA LINHA (borda)")
        void testVizinhosPrimeiraLinha() {
            // [ B , X , 0 ]
            // [ B , B , B ]
            // [ 0 , 0 , 0 ] X em (0,1)
            boolean[][] layout = {
                    {true, false, false},
                    {true, true, true},
                    {false, false, false}
            };
            criarTabuleiro3x3(layout);
            assertEquals(4, tabuleiro.quantVizinhosPerigosos(new Localizacao(0, 1)));
        }

        @Test
        @DisplayName("Calcula vizinhos para a ÚLTIMA LINHA (borda)")
        void testVizinhosUltimaLinha() {
            // [ 0 , 0 , 0 ]
            // [ B , B , B ]
            // [ 0 , X , B ] X em (2,1)
            boolean[][] layout = {
                    {false, false, false},
                    {true, true, true},
                    {false, false, true}
            };
            criarTabuleiro3x3(layout);
            assertEquals(4, tabuleiro.quantVizinhosPerigosos(new Localizacao(2, 1)));
        }

        @Test
        @DisplayName("Calcula vizinhos para a PRIMEIRA COLUNA (borda)")
        void testVizinhosPrimeiraColuna() {
            // [ B , B , 0 ]
            // [ X , B , 0 ]
            // [ B , 0 , 0 ] X em (1,0)
            boolean[][] layout = {
                    {true, true, false},
                    {false, true, false},
                    {true, false, false}
            };
            criarTabuleiro3x3(layout);
            assertEquals(4, tabuleiro.quantVizinhosPerigosos(new Localizacao(1, 0)));
        }

        @Test
        @DisplayName("Calcula vizinhos para a ÚLTIMA COLUNA (borda)")
        void testVizinhosUltimaColuna() {
            // [ 0 , B , B ]
            // [ 0 , B , X ]
            // [ 0 , 0 , B ] X em (1,2)
            boolean[][] layout = {
                    {false, true, true},
                    {false, true, false},
                    {false, false, true}
            };
            criarTabuleiro3x3(layout);
            assertEquals(4, tabuleiro.quantVizinhosPerigosos(new Localizacao(1, 2)));
        }
    }

    @Nested
    @DisplayName("Testes para Getters de Estado (isBomba, isAberto, isMarcado)")
    class GettersDeEstadoTests {
        private final Localizacao locValida = new Localizacao(3, 3);
        private final Localizacao locInvalida = new Localizacao(10, 10);

        @BeforeEach
        void setupBoard() throws ForaDoTabuleiroException {
            tabuleiro.criaTabuleiro(10, 10, 5);
            // Configura um estado conhecido
            tabuleiro.setAberto(locValida);
            tabuleiro.setMarcado(locValida);
        }

        @Test
        @DisplayName("isAberto deve retornar true para quadrado aberto e false para fechado")
        void testIsAberto() throws ForaDoTabuleiroException {
            assertTrue(tabuleiro.isAberto(locValida));
            assertFalse(tabuleiro.isAberto(new Localizacao(0, 0))); // Quadrado padrão
        }

        @Test
        @DisplayName("isMarcado deve retornar true para quadrado marcado e false para desmarcado")
        void testIsMarcado() throws ForaDoTabuleiroException {
            assertTrue(tabuleiro.isMarcado(locValida));
            assertFalse(tabuleiro.isMarcado(new Localizacao(0, 0)));
        }

        @DisplayName("Getters devem lançar ForaDoTabuleiroException para todos os limites críticos")
        @ParameterizedTest(name = "linha={0}, coluna={1}")
        @CsvSource({
                "-1,  5",     // linha negativa
                " 5, -1",     // coluna negativa
                "10,  5",     // linha igual ao tamanho (fora do limite)
                " 5, 10",      // coluna igual ao tamanho (fora do limite)
                "-1, -1",     // canto superior esquerdo
                "10, 10"      // canto inferior direito
        })
        void testGettersLancamExcecaoNosLimites(int linha, int coluna) {
            Localizacao locInvalida = new Localizacao(linha, coluna);

            assertThrows(ForaDoTabuleiroException.class, () -> tabuleiro.isAberto(locInvalida));
            assertThrows(ForaDoTabuleiroException.class, () -> tabuleiro.isMarcado(locInvalida));
            assertThrows(ForaDoTabuleiroException.class, () -> tabuleiro.isBomba(locInvalida));
        }

        @DisplayName("Getters NÃO devem lançar exceção na fronteira de aceitação")
        @ParameterizedTest(name = "linha={0}, coluna={1}")
        @CsvSource({
                "0,  5",     // primeira linha válida
                "9,  5",     // última linha válida (tamanho 10 - 1)
                "5,  0",     // primeira coluna válida
                "5,  9",     // última coluna válida (tamanho 10 - 1)
                "0,  0",     // canto superior esquerdo
                "9,  9"      // canto inferior direito
        })
        void testGettersNaoLancamExcecaoNaFronteiraDeAceitacao(int linha, int coluna) {
            Localizacao locValida = new Localizacao(linha, coluna);

            assertDoesNotThrow(() -> tabuleiro.isAberto(locValida), "Não deveria lançar exceção para isAberto em coordenada válida de fronteira.");
            assertDoesNotThrow(() -> tabuleiro.isMarcado(locValida), "Não deveria lançar exceção para isMarcado em coordenada válida de fronteira.");
            assertDoesNotThrow(() -> tabuleiro.isBomba(locValida), "Não deveria lançar exceção para isBomba em coordenada válida de fronteira.");
        }
    }

    @Nested
    @DisplayName("Testes para Setters de Estado (setAberto, setMarcado, setDesmarcado)")
    class SettersDeEstadoTests {
        private final Localizacao locValida = new Localizacao(5, 5);
        private final Localizacao locInvalida = new Localizacao(10, 10);

        @BeforeEach
        void setupBoard() {
            tabuleiro.criaTabuleiro(10, 10, 15);
        }

        @Test
        @DisplayName("setAberto deve abrir um quadrado fechado")
        void testSetAberto() throws ForaDoTabuleiroException {
            assertFalse(tabuleiro.isAberto(locValida), "Pré-condição: quadrado deve estar fechado.");
            tabuleiro.setAberto(locValida);
            assertTrue(tabuleiro.isAberto(locValida), "O quadrado deveria ter sido aberto.");
        }

        @Test
        @DisplayName("setMarcado deve marcar um quadrado desmarcado")
        void testSetMarcado() throws ForaDoTabuleiroException {
            assertFalse(tabuleiro.isMarcado(locValida), "Pré-condição: quadrado deve estar desmarcado.");
            tabuleiro.setMarcado(locValida);
            assertTrue(tabuleiro.isMarcado(locValida), "O quadrado deveria ter sido marcado.");
        }

        @Test
        @DisplayName("setDesmarcado deve desmarcar um quadrado marcado")
        void testSetDesmarcado() throws ForaDoTabuleiroException {
            // Arrange: primeiro marca o quadrado
            tabuleiro.setMarcado(locValida);
            assertTrue(tabuleiro.isMarcado(locValida), "Pré-condição: quadrado deve estar marcado.");

            // Act: desmarca
            tabuleiro.setDesmarcado(locValida);

            // Assert: verifica se desmarcou
            assertFalse(tabuleiro.isMarcado(locValida), "O quadrado deveria ter sido desmarcado.");
        }

        @DisplayName("Setters devem lançar ForaDoTabuleiroException para todos os limites críticos")
        @ParameterizedTest(name = "linha={0}, coluna={1}")
        @CsvSource({
                "-1,  5",     // linha negativa
                " 5, -1",     // coluna negativa
                "10,  5",     // linha igual ao tamanho (fora do limite)
                " 5, 10",     // coluna igual ao tamanho (fora do limite)
                "-1, -1",     // canto superior esquerdo
                "10, 10"      // canto inferior direito
        })
        void testSettersLancamExcecaoNosLimites(int linha, int coluna) {
            Localizacao locInvalida = new Localizacao(linha, coluna);

            assertThrows(ForaDoTabuleiroException.class, () -> tabuleiro.setAberto(locInvalida));
            assertThrows(ForaDoTabuleiroException.class, () -> tabuleiro.setMarcado(locInvalida));
            assertThrows(ForaDoTabuleiroException.class, () -> tabuleiro.setDesmarcado(locInvalida));
        }

        @DisplayName("Setters NÃO devem lançar exceção na fronteira de aceitação")
        @ParameterizedTest(name = "linha={0}, coluna={1}")
        @CsvSource({
                "0,  5",     // primeira linha válida
                "9,  5",     // última linha válida (tamanho 10 - 1)
                "5,  0",     // primeira coluna válida
                "5,  9",     // última coluna válida (tamanho 10 - 1)
                "0,  0",     // canto superior esquerdo
                "9,  9"      // canto inferior direito
        })
        void testSettersNaoLancamExcecaoNaFronteiraDeAceitacao(int linha, int coluna) {
            Localizacao locValida = new Localizacao(linha, coluna);

            assertDoesNotThrow(() -> tabuleiro.setAberto(locValida), "Não deveria lançar exceção para setAberto em coordenada válida de fronteira.");
            assertDoesNotThrow(() -> tabuleiro.setMarcado(locValida), "Não deveria lançar exceção para setMarcado em coordenada válida de fronteira.");
            assertDoesNotThrow(() -> tabuleiro.setDesmarcado(locValida), "Não deveria lançar exceção para setDesmarcado em coordenada válida de fronteira.");
        }
    }
}