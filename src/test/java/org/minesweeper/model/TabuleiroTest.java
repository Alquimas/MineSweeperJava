package org.minesweeper.model;

import org.junit.jupiter.api.*;
import org.minesweeper.exceptions.ForaDoTabuleiroException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
        // Usa reflection para resetar a instância Singleton antes de cada teste
        Field instance = Tabuleiro.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
        tabuleiro = Tabuleiro.getInstance();
    }

    /**
     * Método auxiliar para injetar um tabuleiro customizado para testes previsíveis.
     * @param boardState Uma matriz de Quadrado que representa o estado desejado.
     */
    private void setCustomTabuleiro(ArrayList<ArrayList<Quadrado>> boardState) {
        try {
            int linhas = boardState.size();
            int colunas = boardState.isEmpty() ? 0 : boardState.get(0).size();

            // Injeta o tabuleiro customizado
            Field tabuleiroField = Tabuleiro.class.getDeclaredField("tabuleiro");
            tabuleiroField.setAccessible(true);
            tabuleiroField.set(tabuleiro, boardState);

            // Define os tamanhos de linha e coluna
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
    @DisplayName("Testes para criaTabuleiro(int, int, int)")
    class CriaTabuleiroTests {

        @Test
        @DisplayName("Cria tabuleiro com tamanho e quantidade de bombas corretos")
        void testTamanhoEBombas() throws Exception{
            tabuleiro.criaTabuleiro(10, 15, 20);
            assertEquals(10, tabuleiro.getLinha_size(), "Valor de linha_size deve ser 10");
            assertEquals(15, tabuleiro.getColuna_size(), "Valor de coluna_size deve ser 15");

            long bombCount = 0;
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 15; j++) {
                    if (tabuleiro.isBomba(new Localizacao(i, j))) {
                        bombCount++;
                    }
                }
            }
            assertEquals(20, bombCount, "Número de bombas deve ser 20");

            Field tabuleiroField = Tabuleiro.class.getDeclaredField("tabuleiro");
            tabuleiroField.setAccessible(true);

            @SuppressWarnings("unchecked")
            ArrayList<ArrayList<Quadrado>> grid = (ArrayList<ArrayList<Quadrado>>) tabuleiroField.get(tabuleiro);
            assertEquals(10, grid.size(), "Número de linhas em tabuleiro deve ser 10");

            for (int i = 0; i < 10; i++) {
                assertEquals(15, grid.get(i).size(), "Número de colunas na linha " + i + " em tabuleiro deve ser 15");
            }

        }

        @Test
        @DisplayName("Verifica se a localização dos quadrados corresponde à sua posição")
        void testLocalizacaoCorretaDosQuadrados() throws Exception {
            // Adicionamos 'throws Exception' para simplificar o tratamento de erros de reflexão
            // 1. Arrange: Define as dimensões e cria o tabuleiro
            int linhas = 8;
            int colunas = 8;
            tabuleiro.criaTabuleiro(linhas, colunas, 10);

            // 2. Act: Usa a Reflexão para acessar o campo privado 'tabuleiro'
            Field tabuleiroField = Tabuleiro.class.getDeclaredField("tabuleiro");
            tabuleiroField.setAccessible(true); // Torna o campo privado acessível

            // Pega o valor do campo (a ArrayList) da instância do tabuleiro e faz o cast
            // Suprimimos o warning pois sabemos que o tipo está correto
            @SuppressWarnings("unchecked")
            ArrayList<ArrayList<Quadrado>> grid = (ArrayList<ArrayList<Quadrado>>) tabuleiroField.get(tabuleiro);

            // 3. Assert: Itera sobre a matriz interna e verifica cada quadrado
            assertNotNull(grid, "A matriz interna do tabuleiro não deveria ser nula.");

            for (int i = 0; i < linhas; i++) {
                for (int j = 0; j < colunas; j++) {
                    Quadrado quadradoAtual = grid.get(i).get(j);
                    Localizacao locDoQuadrado = quadradoAtual.getLocalizacao();

                    // Verifica se o objeto Localizacao não é nulo
                    assertNotNull(locDoQuadrado, "A Localizacao do quadrado em [" + i + "][" + j + "] não pode ser nula.");

                    // A verificação principal: a localização interna corresponde à posição na matriz?
                    assertEquals(i, locDoQuadrado.getLinha(),
                            "A linha armazenada no quadrado [" + i + "][" + j + "] deve ser " + i);

                    assertEquals(j, locDoQuadrado.getColuna(),
                            "A coluna armazenada no quadrado [" + i + "][" + j + "] deve ser " + j);
                }
            }
        }

        @Test
        @DisplayName("Verifica a aleatoriedade na criação de dois tabuleiros")
        void testAleatoriedade() throws ForaDoTabuleiroException {
            tabuleiro.criaTabuleiro(10, 10, 40);
            Set<Localizacao> bombasPrimeiroTabuleiro = new HashSet<>();
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (tabuleiro.isBomba(new Localizacao(i, j))) {
                        bombasPrimeiroTabuleiro.add(new Localizacao(i, j));
                    }
                }
            }

            try {
                // Cria uma segunda instância
                Constructor<Tabuleiro> constructor = Tabuleiro.class.getDeclaredConstructor();
                // 2. Torná-lo acessível
                constructor.setAccessible(true);
                // 3. Criar uma nova instância usando o construtor privado
                Tabuleiro tab2 = constructor.newInstance();

                tab2.criaTabuleiro(10, 10, 40);
                Set<Localizacao> bombasSegundoTabuleiro = new HashSet<>();
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        if (tab2.isBomba(new Localizacao(i, j))) {
                            bombasSegundoTabuleiro.add(new Localizacao(i, j));
                        }
                    }
                }

                // É estatisticamente improvável que sejam idênticos.
                // Este teste pode falhar raramente, mas indica uma boa aleatoriedade.
                assertNotEquals(bombasPrimeiroTabuleiro, bombasSegundoTabuleiro, "A distribuição de bombas deveria ser diferente entre dois tabuleiros");

            } catch (NoSuchMethodException | InstantiationException |
                     IllegalAccessException | InvocationTargetException e) {
                fail("Falha ao acessar uma nova instância do tabuleiro via reflection: " + e.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("Testes para abrirQuadrado(Localizacao)")
    class AbrirQuadradoTests {
        private ArrayList<ArrayList<Quadrado>> board;

        @BeforeEach
        void setupBoard() {
            // Tabuleiro 3x3 para os testes:
            // [ B , 1 , 0 ]
            // [ 1 , 1 , 0 ]
            // [ 0 , 0 , 0 ]
            // B = Bomba em (0,0)
            board = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                board.add(new ArrayList<>());
                for (int j = 0; j < 3; j++) {
                    boolean isBomba = (i == 0 && j == 0);
                    Quadrado q = new Quadrado(isBomba, false, false);
                    q.setLocalizacao(new Localizacao(i, j));
                    board.get(i).add(q);
                }
            }
            setCustomTabuleiro(board);
        }

        @Test
        @DisplayName("Retorna -1 ao tentar abrir um quadrado já aberto e quadrado permanece aberto")
        void testAbrirQuadradoJaAberto() throws ForaDoTabuleiroException {
            board.get(1).get(1).setAberto(true);
            assertEquals(-1, tabuleiro.abrirQuadrado(new Localizacao(1, 1)));
            assertTrue(tabuleiro.isAberto(new Localizacao(1, 1)), "Quadrado aberto deve permanecer aberto");
        }

        @Test
        @DisplayName("Retorna -2 ao tentar abrir um quadrado marcado e não conseguiu abrir quadrado marcado")
        void testAbrirQuadradoMarcado() throws ForaDoTabuleiroException {
            board.get(1).get(2).setMarcado(true);
            assertEquals(-2, tabuleiro.abrirQuadrado(new Localizacao(1, 2)));
            assertFalse(tabuleiro.isAberto(new Localizacao(1, 2)), "Quadrado marcado não deve ser aberto");
        }

        @Test
        @DisplayName("Retorna -3 e abre ao clicar em uma bomba e quadrado foi aberto")
        void testAbrirQuadradoComBomba() throws ForaDoTabuleiroException {
            assertEquals(-3, tabuleiro.abrirQuadrado(new Localizacao(0, 0)));
            assertTrue(tabuleiro.isAberto(new Localizacao(0, 0)), "Quadrado com bomba deve ser aberto");
        }

        @Test
        @DisplayName("Retorna número de vizinhos perigosos ao abrir quadrado seguro e quadrado foi aberto")
        void testAbrirQuadradoSeguro() throws ForaDoTabuleiroException {
            // Quadrado (0,1) tem 1 vizinho com bomba (0,0)
            assertEquals(1, tabuleiro.abrirQuadrado(new Localizacao(0, 1)));
            assertTrue(tabuleiro.isAberto(new Localizacao(0, 1)), "Quadrado seguro deve ser aberto");
        }

        @Test
        @DisplayName("Lança exceção correta para localização fora dos limites")
        void testAbrirQuadradoComLocalizacaoInvalida() {
            assertThrows(ForaDoTabuleiroException.class, () -> tabuleiro.abrirQuadrado(new Localizacao(10, 0)),
                    "Linha fora dos limites do tabuleiro deveria gerar exceção ForaDoTabuleiroException em marcaQuadrado");
            assertThrows(ForaDoTabuleiroException.class, () -> tabuleiro.abrirQuadrado(new Localizacao(0, 10)),
                    "Coluna fora dos limites do tabuleiro deveria gerar exceção ForaDoTabuleiroException em marcaQuadrado");
        }
    }

    @Nested
    @DisplayName("Testes para quantVizinhosPerigosos(Localizacao)")
    class QuantVizinhosPerigososTests {

        private Method metodoQuantVizinhos;

        // Bloco para obter o método privado uma vez antes de todos os testes nesta classe aninhada
        @BeforeEach
        void setUp() {
            try {
                metodoQuantVizinhos = Tabuleiro.class.getDeclaredMethod("quantVizinhosPerigosos", Localizacao.class);
                metodoQuantVizinhos.setAccessible(true);
            } catch (NoSuchMethodException e) {
                fail("Não foi possível encontrar o método 'quantVizinhosPerigosos' via reflexão.");
            }
        }

        // Método auxiliar para criar um tabuleiro 3x3 com base em uma matriz de booleanos
        private void criarTabuleiro3x3(boolean[][] layoutBombas) {
            ArrayList<ArrayList<Quadrado>> board = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                board.add(new ArrayList<>());
                for (int j = 0; j < 3; j++) {
                    board.get(i).add(new Quadrado(layoutBombas[i][j], false, false));
                }
            }
            setCustomTabuleiro(board); // Assumindo que setCustomTabuleiro existe na classe principal de teste
        }

        @Test
        @DisplayName("Calcula vizinhos para um quadrado no CENTRO com 8 bombas")
        void testVizinhosCentroCom8Bombas() throws Exception {
            // [ B , B , B ]
            // [ B , X , B ]
            // [ B , B , B ] X em (1,1)
            boolean[][] layout = {
                    {true, true, true},
                    {true, false, true},
                    {true, true, true}
            };
            criarTabuleiro3x3(layout);

            int result = (int) metodoQuantVizinhos.invoke(tabuleiro, new Localizacao(1, 1));
            assertEquals(8, result, "Deveria haver 8 vizinhos com bomba");
        }

        @Test
        @DisplayName("Calcula vizinhos para um quadrado no CENTRO com 4 bombas")
        void testVizinhosCentroCom4Bombas() throws Exception {
            // [ B , 0 , B ]
            // [ 0 , X , 0 ]
            // [ B , 0 , B ] X em (1,1)
            boolean[][] layout = {
                    {true, false, true},
                    {false, false, false},
                    {true, false, true}
            };
            criarTabuleiro3x3(layout);

            int result = (int) metodoQuantVizinhos.invoke(tabuleiro, new Localizacao(1, 1));
            assertEquals(4, result, "Deveria haver 4 vizinhos com bomba (contagem correta)");
        }

        @Test
        @DisplayName("Calcula vizinhos para um quadrado no CENTRO com 0 bombas")
        void testVizinhosCentroCom0Bombas() throws Exception {
            // [ 0 , 0 , 0 ]
            // [ 0 , X , 0 ]
            // [ 0 , 0 , 0 ] X em (1,1)
            boolean[][] layout = {
                    {false, false, false},
                    {false, false, false},
                    {false, false, false}
            };
            criarTabuleiro3x3(layout);

            int result = (int) metodoQuantVizinhos.invoke(tabuleiro, new Localizacao(1, 1));
            assertEquals(0, result, "Deveria haver 0 vizinhos com bomba");
        }

        @Test
        @DisplayName("Calcula vizinhos para o CANTO SUPERIOR ESQUERDO")
        void testVizinhosCantoSuperiorEsquerdo() throws Exception {
            // [ X , B , B ]
            // [ B , B , B ]
            // [ B , B , B ] X em (0,0)
            boolean[][] layout = {
                    {false, true, true},
                    {true, true, true},
                    {true, false, true}
            };
            criarTabuleiro3x3(layout);

            int result = (int) metodoQuantVizinhos.invoke(tabuleiro, new Localizacao(0, 0));
            assertEquals(3, result, "Deveria haver 3 vizinhos com bomba no canto superior esquerdo");
        }

        @Test
        @DisplayName("Calcula vizinhos para o CANTO SUPERIOR DIREITO")
        void testVizinhosCantoSuperiorDireito() throws Exception {
            // [ B , B , X ]
            // [ B , B , B ]
            // [ B , B , B ] X em (0,2)
            boolean[][] layout = {
                    {true, true, false},
                    {true, true, true},
                    {true, true, true}
            };
            criarTabuleiro3x3(layout);

            int result = (int) metodoQuantVizinhos.invoke(tabuleiro, new Localizacao(0, 2));
            assertEquals(3, result, "Deveria haver 3 vizinhos com bomba no canto superior direito");
        }

        @Test
        @DisplayName("Calcula vizinhos para o CANTO INFERIOR ESQUERDO")
        void testVizinhosCantoInferiorEsquerdo() throws Exception {
            // [ B , B , B ]
            // [ B , B , B ]
            // [ X , B , B ] X em (2,0)
            boolean[][] layout = {
                    {true, true, true},
                    {true, true, true},
                    {false, true, true}
            };
            criarTabuleiro3x3(layout);

            int result = (int) metodoQuantVizinhos.invoke(tabuleiro, new Localizacao(2, 0));
            assertEquals(3, result, "Deveria haver 3 vizinhos com bomba no canto inferior esquerdo");
        }

        @Test
        @DisplayName("Calcula vizinhos para o CANTO INFERIOR DIREITO")
        void testVizinhosCantoInferiorDireito() throws Exception {
            // [ B , B , B ]
            // [ B , B , B ]
            // [ B , B , X ] X em (2,2)
            boolean[][] layout = {
                    {true, true, true},
                    {true, true, true},
                    {true, true, false}
            };
            criarTabuleiro3x3(layout);

            int result = (int) metodoQuantVizinhos.invoke(tabuleiro, new Localizacao(2, 2));
            assertEquals(3, result, "Deveria haver 3 vizinhos com bomba no canto inferior direito");
        }

        @Test
        @DisplayName("Calcula vizinhos para a PRIMEIRA LINHA (borda)")
        void testVizinhosPrimeiraLinha() throws Exception {
            // [ B , X , 0 ]
            // [ B , B , B ]
            // [ B , B , B ] X em (0,1)
            boolean[][] layout = {
                    {true, false, false},
                    {true, true, true},
                    {true, true, true}
            };
            criarTabuleiro3x3(layout);

            int result = (int) metodoQuantVizinhos.invoke(tabuleiro, new Localizacao(0, 1));
            assertEquals(4, result, "Deveria haver 4 vizinhos com bomba na primeira linha");
        }

        @Test
        @DisplayName("Calcula vizinhos para a ÚLTIMA LINHA (borda)")
        void testVizinhosUltimaLinha() throws Exception {
            // [ B , B , B ]
            // [ B , B , B ]
            // [ 0 , X , B ] X em (2,1)
            boolean[][] layout = {
                    {true, true, true},
                    {true, true, true},
                    {false, false, true}
            };
            criarTabuleiro3x3(layout);

            int result = (int) metodoQuantVizinhos.invoke(tabuleiro, new Localizacao(2, 1));
            assertEquals(4, result, "Deveria haver 4 vizinhos com bomba na última linha");
        }

        @Test
        @DisplayName("Calcula vizinhos para a PRIMEIRA COLUNA (borda)")
        void testVizinhosPrimeiraColuna() throws Exception {
            // [ B , B , B ]
            // [ X , B , B ]
            // [ B , 0 , B ] X em (1,0)
            boolean[][] layout = {
                    {true, true, true},
                    {false, true, true},
                    {true, false, true}
            };
            criarTabuleiro3x3(layout);

            int result = (int) metodoQuantVizinhos.invoke(tabuleiro, new Localizacao(1, 0));
            assertEquals(4, result, "Deveria haver 4 vizinhos com bomba na primeira coluna");
        }

        @Test
        @DisplayName("Calcula vizinhos para a ÚLTIMA COLUNA (borda)")
        void testVizinhosUltimaColuna() throws Exception {
            // [ B , B , B ]
            // [ B , B , X ]
            // [ B , 0 , B ] X em (1,2)
            boolean[][] layout = {
                    {true, true, true},
                    {true, true, false},
                    {true, false, true}
            };
            criarTabuleiro3x3(layout);

            int result = (int) metodoQuantVizinhos.invoke(tabuleiro, new Localizacao(1, 2));
            assertEquals(4, result, "Deveria haver 4 vizinhos com bomba na última coluna");
        }
    }

    @Nested
    @DisplayName("Testes para quadradoExiste(int, int)")
    class QuadradoExisteTests {

        private Method method;

        @BeforeEach
        void setupMethod() throws Exception {
            tabuleiro.criaTabuleiro(10, 10, 0); // Cria um tabuleiro 10x10
            method = Tabuleiro.class.getDeclaredMethod("quadradoExiste", int.class, int.class);
            method.setAccessible(true);
        }

        @Test
        @DisplayName("Retorna falso para linha < 0")
        void testLinhaNegativa() throws Exception {
            assertFalse((boolean) method.invoke(tabuleiro, -1, 5));
        }

        @Test
        @DisplayName("Retorna falso para linha >= linha_size")
        void testLinhaForaDoLimiteSuperior() throws Exception {
            assertFalse((boolean) method.invoke(tabuleiro, 10, 5));
        }

        @Test
        @DisplayName("Retorna falso para coluna < 0")
        void testColunaNegativa() throws Exception {
            assertFalse((boolean) method.invoke(tabuleiro, 5, -1));
        }

        @Test
        @DisplayName("Retorna falso para coluna >= coluna_size")
        void testColunaForaDoLimiteSuperior() throws Exception {
            assertFalse((boolean) method.invoke(tabuleiro, 5, 10));
        }

        @Test
        @DisplayName("Retorna verdadeiro para coordenadas válidas")
        void testCoordenadasValidas() throws Exception {
            assertTrue((boolean) method.invoke(tabuleiro, 0, 0));
            assertTrue((boolean) method.invoke(tabuleiro, 9, 9));
            assertTrue((boolean) method.invoke(tabuleiro, 5, 3));
        }
    }

    @Nested
    @DisplayName("Testes para marcaQuadrado(Localizacao)")
    class MarcaQuadradoTests {
        @BeforeEach
        void setupBoard() {
            ArrayList<ArrayList<Quadrado>> board = new ArrayList<>();
            board.add(new ArrayList<>());
            board.get(0).add(new Quadrado(false, false, false)); // (0,0) Fechado, não marcado
            board.get(0).add(new Quadrado(false, true, false));  // (0,1) Fechado, marcado
            board.get(0).add(new Quadrado(false, false, true)); // (0,2) Aberto
            setCustomTabuleiro(board);
        }

        @Test
        @DisplayName("Retorna -1 ao tentar marcar um quadrado aberto (quadrado fica desmarcado)")
        void testMarcarQuadradoAberto() throws ForaDoTabuleiroException {
            assertEquals(-1, tabuleiro.marcaQuadrado(new Localizacao(0, 2)));
            assertFalse(tabuleiro.isMarcado(new Localizacao(0, 2)));
        }

        @Test
        @DisplayName("Retorna 1 e desmarca um quadrado já marcado")
        void testDesmarcarQuadrado() throws ForaDoTabuleiroException {
            assertEquals(1, tabuleiro.marcaQuadrado(new Localizacao(0, 1)));
            assertFalse(tabuleiro.isMarcado(new Localizacao(0, 1)));
        }

        @Test
        @DisplayName("Retorna 0 e marca um quadrado não marcado")
        void testMarcarQuadrado() throws ForaDoTabuleiroException {
            assertEquals(0, tabuleiro.marcaQuadrado(new Localizacao(0, 0)));
            assertTrue(tabuleiro.isMarcado(new Localizacao(0, 0)));
        }

        @Test
        @DisplayName("Lança exceção correta para localização fora dos limites")
        void testMarcaQuadradoComLocalizacaoInvalida() {
            assertThrows(ForaDoTabuleiroException.class, () -> tabuleiro.marcaQuadrado(new Localizacao(10, 0)),
                    "Linha fora dos limites do tabuleiro deveria gerar exceção ForaDoTabuleiroException em marcaQuadrado");
            assertThrows(ForaDoTabuleiroException.class, () -> tabuleiro.marcaQuadrado(new Localizacao(0, 10)),
                    "Coluna fora dos limites do tabuleiro deveria gerar exceção ForaDoTabuleiroException em marcaQuadrado");
        }
    }

    @Nested
    @DisplayName("Testes para Getters (isBomba, isAberto, isMarcado)")
    class GettersTest {
        @BeforeEach
        void setupBoard() {
            ArrayList<ArrayList<Quadrado>> board = new ArrayList<>();
            board.add(new ArrayList<>());
            // (0,0): Bomba, Aberto, Marcado (cenário de fim de jogo)
            Quadrado q1 = new Quadrado(true, true, true);
            board.get(0).add(q1);
            // (0,1): Não Bomba, Fechado, Não Marcado
            Quadrado q2 = new Quadrado(false, false, false);
            board.get(0).add(q2);
            setCustomTabuleiro(board);
        }

        @Test
        void testIsBomba() throws ForaDoTabuleiroException {
            assertTrue(tabuleiro.isBomba(new Localizacao(0, 0)));
            assertFalse(tabuleiro.isBomba(new Localizacao(0, 1)));
        }

        @Test
        void testIsAberto() throws ForaDoTabuleiroException {
            assertTrue(tabuleiro.isAberto(new Localizacao(0, 0)));
            assertFalse(tabuleiro.isAberto(new Localizacao(0, 1)));
        }

        @Test
        void testIsMarcado() throws ForaDoTabuleiroException {
            assertTrue(tabuleiro.isMarcado(new Localizacao(0, 0)));
            assertFalse(tabuleiro.isMarcado(new Localizacao(0, 1)));
        }

        @Test
        @DisplayName("Lança exceção correta para localização fora dos limites")
        void testGettersComLocalizacaoInvalida() {
            assertThrows(ForaDoTabuleiroException.class, () -> tabuleiro.isBomba(new Localizacao(10, 0)),
                    "Linha fora dos limites do tabuleiro deveria gerar exceção ForaDoTabuleiroException em isBomba");
            assertThrows(ForaDoTabuleiroException.class, () -> tabuleiro.isBomba(new Localizacao(0, 10)),
                    "Coluna fora dos limites do tabuleiro deveria gerar exceção ForaDoTabuleiroException em isBomba");
            assertThrows(ForaDoTabuleiroException.class, () -> tabuleiro.isMarcado(new Localizacao(10, 0)),
                    "Linha fora dos limites do tabuleiro deveria gerar exceção ForaDoTabuleiroException em isMarcado");
            assertThrows(ForaDoTabuleiroException.class, () -> tabuleiro.isMarcado(new Localizacao(0, 10)),
                    "Coluna fora dos limites do tabuleiro deveria gerar exceção ForaDoTabuleiroException em isMarcado");
            assertThrows(ForaDoTabuleiroException.class, () -> tabuleiro.isAberto(new Localizacao(10, 0)),
                    "Linha fora dos limites do tabuleiro deveria gerar exceção ForaDoTabuleiroException em isAberto");
            assertThrows(ForaDoTabuleiroException.class, () -> tabuleiro.isAberto(new Localizacao(0, 10)),
                    "Coluna fora dos limites do tabuleiro deveria gerar exceção ForaDoTabuleiroException em isAberto");
        }
    }

    @Nested
    @DisplayName("Testes para getInstance() (Singleton)")
    class GetInstanceTests {

        @Test
        @DisplayName("Garante que a mesma instância é retornada")
        void testRetornaMesmaInstancia() {
            Tabuleiro instance1 = Tabuleiro.getInstance();
            Tabuleiro instance2 = Tabuleiro.getInstance();
            assertSame(instance1, instance2, "getInstance deve sempre retornar a mesma instância.");
        }

        @Test
        @DisplayName("Garante que a instância não é nula")
        void testInstanciaNaoNula() {
            Tabuleiro instance = Tabuleiro.getInstance();
            assertNotNull(instance, "A instância retornada não deve ser nula.");
        }
    }
}