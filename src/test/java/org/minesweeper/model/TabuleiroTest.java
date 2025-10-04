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
    @DisplayName("Testes para inicializaTabuleiroVazio(int, int)")
    class TestesParaInicializaTabuleiroVazio {

        @Test
        @DisplayName("Garante que os valores linha_size e coluna_size correspondem à entrada")
        void testInicializaTabuleiroVazio_DefineTamanhosCorretamente() {
            // Arrange
            int linhas = 8;
            int colunas = 12;

            // Act
            tabuleiro.inicializaTabuleiroVazio(linhas, colunas);

            // Assert
            assertEquals(linhas, tabuleiro.getLinha_size(), "O tamanho da linha deve ser 8.");
            assertEquals(colunas, tabuleiro.getColuna_size(), "O tamanho da coluna deve ser 12.");
        }

        @Test
        @DisplayName("Garante que o grid é preenchido com quadrados e localizações corretas")
        void testInicializaTabuleiroVazio_PreencheGridComQuadradosCorretos() throws Exception {
            // Arrange
            int linhas = 10;
            int colunas = 15;

            // Act
            tabuleiro.inicializaTabuleiroVazio(linhas, colunas);

            // Assert
            // Usa reflexão para verificar o estado interno do tabuleiro
            Field tabuleiroField = Tabuleiro.class.getDeclaredField("tabuleiro");
            tabuleiroField.setAccessible(true);
            @SuppressWarnings("unchecked")
            ArrayList<ArrayList<Quadrado>> grid = (ArrayList<ArrayList<Quadrado>>) tabuleiroField.get(tabuleiro);

            assertNotNull(grid);
            assertEquals(linhas, grid.size(), "Deve haver 10 listas de linha no tabuleiro.");

            // Itera para verificar cada linha e cada quadrado
            for (int i = 0; i < linhas; i++) {
                ArrayList<Quadrado> linhaAtual = grid.get(i);
                assertNotNull(linhaAtual);
                assertEquals(colunas, linhaAtual.size(), "A linha " + i + " deve conter 15 quadrados.");

                for (int j = 0; j < colunas; j++) {
                    Quadrado quadradoAtual = linhaAtual.get(j);
                    assertNotNull(quadradoAtual, "A posição [" + i + "][" + j + "] não deve ser nula.");

                    // Verifica a localização de cada quadrado
                    Localizacao loc = quadradoAtual.getLocalizacao();
                    assertNotNull(loc);
                    assertEquals(i, loc.getLinha(), "A linha do quadrado em [" + i + "][" + j + "] deve ser " + i);
                    assertEquals(j, loc.getColuna(), "A coluna do quadrado em [" + i + "][" + j + "] deve ser " + j);

                    // Verifica o estado padrão do quadrado
                    assertFalse(quadradoAtual.isAberto());
                    assertFalse(quadradoAtual.isMarcado());
                    assertFalse(quadradoAtual.isBomba());
                }
            }
        }

        @Test
        @DisplayName("Garante que os contadores de bombas e quadrados abertos são inicializados com 0")
        void testInicializaTabuleiroVazio_InicializaContadores() throws Exception {
            // Act
            tabuleiro.inicializaTabuleiroVazio(10, 10);

            // Assert: Usa reflexão para acessar os campos privados
            Field quadradosAbertosField = Tabuleiro.class.getDeclaredField("quadradosAbertos");
            quadradosAbertosField.setAccessible(true);
            int quadradosAbertos = (int) quadradosAbertosField.get(tabuleiro);
            assertEquals(0, quadradosAbertos, "quadradosAbertos deve ser inicializado com 0.");

            Field bombasField = Tabuleiro.class.getDeclaredField("bombas");
            bombasField.setAccessible(true);
            int bombas = (int) bombasField.get(tabuleiro);
            assertEquals(0, bombas, "bombas deve ser inicializado com 0.");
        }

    }

    @Nested
    @DisplayName("Testes para Getters de Estado (isBomba, isAberto, isMarcado)")
    class GettersDeEstadoTests {
        private final Localizacao locValida = new Localizacao(3, 3);
        private final int LINHAS = 10;
        private final int COLUNAS = 10;

        @BeforeEach
        void setupBoard() throws ForaDoTabuleiroException {
            // --- LÓGICA DE SETUP ATUALIZADA ---
            // Agora a inicialização é feita em um único passo
            tabuleiro.inicializaTabuleiroVazio(LINHAS, COLUNAS);

            // Configura um estado conhecido para o teste
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
        private final int LINHAS = 10;
        private final int COLUNAS = 10;

        @BeforeEach
        void setupBoard() {
            // --- LÓGICA DE SETUP ATUALIZADA ---
            // A inicialização agora é muito mais simples
            tabuleiro.inicializaTabuleiroVazio(LINHAS, COLUNAS);
        }

        @Test
        @DisplayName("setAberto deve abrir um quadrado e incrementar o contador de quadrados abertos")
        void testSetAberto_AbreQuadradoEIncrementaContador() throws Exception {
            // Arrange
            assertFalse(tabuleiro.isAberto(locValida), "Pré-condição: quadrado deve estar fechado.");

            // Pega o valor inicial do contador via reflexão
            Field contadorField = Tabuleiro.class.getDeclaredField("quadradosAbertos");
            contadorField.setAccessible(true);
            int valorInicial = (int) contadorField.get(tabuleiro);

            // Act
            tabuleiro.setAberto(locValida);

            // Assert
            // 1. Verifica se o quadrado está aberto
            assertTrue(tabuleiro.isAberto(locValida), "O quadrado deveria ter sido aberto.");

            // 2. Verifica se o contador foi incrementado
            int valorFinal = (int) contadorField.get(tabuleiro);
            assertEquals(valorInicial + 1, valorFinal, "O contador de quadradosAbertos deveria ter sido incrementado em 1.");
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

    @Nested
    @DisplayName("Testes para ganhou()")
    class TestesParaGanhou {

        // Método auxiliar para definir o estado do jogo via reflexão
        private void setEstadoDoJogo(int linhas, int colunas, int bombas, int abertos) throws Exception {
            Field linhaSizeField = Tabuleiro.class.getDeclaredField("linha_size");
            linhaSizeField.setAccessible(true);
            linhaSizeField.set(tabuleiro, linhas);

            Field colunaSizeField = Tabuleiro.class.getDeclaredField("coluna_size");
            colunaSizeField.setAccessible(true);
            colunaSizeField.set(tabuleiro, colunas);

            Field bombasField = Tabuleiro.class.getDeclaredField("bombas");
            bombasField.setAccessible(true);
            bombasField.set(tabuleiro, bombas);

            Field quadradosAbertosField = Tabuleiro.class.getDeclaredField("quadradosAbertos");
            quadradosAbertosField.setAccessible(true);
            quadradosAbertosField.set(tabuleiro, abertos);
        }

        @Test
        @DisplayName("Retorna true quando todos os quadrados seguros foram abertos")
        void ganhou_RetornaTrue_QuandoJogoEstaGanho() throws Exception {
            // Arrange: (10*10) - 15 bombas = 85 quadrados seguros.
            // Se 85 quadrados foram abertos, o jogo está ganho.
            setEstadoDoJogo(10, 10, 15, 85);

            // Act & Assert
            assertTrue(tabuleiro.ganhou(), "O método ganhou() deveria retornar true.");
        }

        @Test
        @DisplayName("Retorna false quando ainda faltam quadrados seguros para abrir")
        void ganhou_RetornaFalse_QuandoJogoNaoEstaGanho() throws Exception {
            // Arrange: (10*10) - 15 bombas = 85 quadrados seguros.
            // Se apenas 84 foram abertos, o jogo não está ganho.
            setEstadoDoJogo(10, 10, 15, 84);

            // Act & Assert
            assertFalse(tabuleiro.ganhou(), "O método ganhou() deveria retornar false.");
        }

        @Test
        @DisplayName("Retorna false quando o número de quadrados abertos é maior que o necessário (caso de segurança)")
        void ganhou_RetornaFalse_QuandoAbertosMaiorQueNecessario() throws Exception {
            // Arrange: (10*10) - 15 bombas = 85 quadrados seguros.
            // Se 86 foram abertos (cenário hipotético de um bug), não é uma condição de vitória.
            setEstadoDoJogo(10, 10, 15, 86);

            // Act & Assert
            assertFalse(tabuleiro.ganhou(), "O método ganhou() deveria retornar false se o contador estiver incorreto.");
        }
    }

}