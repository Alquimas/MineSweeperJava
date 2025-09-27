package org.minesweeper.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.minesweeper.exceptions.ForaDoTabuleiroException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TabuleiroFrontTest {

    private TabuleiroFront tabuleiroFront;
    private final int LINHAS = 10;
    private final int COLUNAS = 15;

    @BeforeEach
    void setUp() {
        tabuleiroFront = new TabuleiroFront(LINHAS, COLUNAS);
    }

    /**
     * Método auxiliar para acessar a matriz interna do tabuleiro via reflexão.
     */
    private ArrayList<ArrayList<QuadradoFront>> getGridInterno(TabuleiroFront tab) throws Exception {
        Field tabuleiroField = TabuleiroFront.class.getDeclaredField("tabuleiro");
        tabuleiroField.setAccessible(true);
        // Suprimimos o warning pois sabemos que o tipo está correto
        @SuppressWarnings("unchecked")
        ArrayList<ArrayList<QuadradoFront>> grid = (ArrayList<ArrayList<QuadradoFront>>) tabuleiroField.get(tab);
        return grid;
    }

    @Nested
    @DisplayName("Testes para o Construtor TabuleiroFront(int, int)")
    class ConstrutorTests {
        @Test
        @DisplayName("Verifica se cria tabuleiro com as dimensões corretas e inicializa campos")
        void testDimensoesCorretas() throws Exception {
            assertEquals(LINHAS, tabuleiroFront.getLinha_size());
            assertEquals(COLUNAS, tabuleiroFront.getColuna_size());

            ArrayList<ArrayList<QuadradoFront>> grid = getGridInterno(tabuleiroFront);
            assertNotNull(grid);
            assertEquals(LINHAS, grid.size(), "O número de linhas na matriz interna deve ser correto.");
            assertEquals(COLUNAS, grid.get(0).size(), "O número de colunas na matriz interna deve ser correto.");
        }

        @Test
        @DisplayName("Verifica se a localização dos quadrados corresponde à sua posição na matriz")
        void testLocalizacaoCorretaDosQuadrados() throws Exception {
            ArrayList<ArrayList<QuadradoFront>> grid = getGridInterno(tabuleiroFront);
            for (int i = 0; i < LINHAS; i++) {
                for (int j = 0; j < COLUNAS; j++) {
                    Localizacao loc = grid.get(i).get(j).getLocalizacao();
                    assertNotNull(loc, "A Localizacao do quadrado em [" + i + "][" + j + "] não pode ser nula.");
                    assertEquals(i, loc.getLinha(), "A linha armazenada no quadrado [" + i + "][" + j + "] deve ser " + i);
                    assertEquals(j, loc.getColuna(), "A coluna armazenada no quadrado [" + i + "][" + j + "] deve ser " + j);
                }
            }
        }
    }

    @Nested
    @DisplayName("Testes para Métodos Privados")
    class MetodosPrivadosTests {
        @Test
        @DisplayName("criaQuadrado(Localizacao) deve criar um quadrado com a localização correta")
        void testCriaQuadrado() throws Exception {
            Method criaQuadradoMethod = TabuleiroFront.class.getDeclaredMethod("criaQuadrado", Localizacao.class);
            criaQuadradoMethod.setAccessible(true);

            Localizacao loc = new Localizacao(5, 7);
            QuadradoFront qf = (QuadradoFront) criaQuadradoMethod.invoke(tabuleiroFront, loc);

            assertNotNull(qf, "A localização do quadrado não pode ser nula");
            assertSame(loc, qf.getLocalizacao(), "A localização do quadrado deve ser correta");
        }

        @Test
        @DisplayName("quadradoExiste(int, int) deve validar os limites corretamente")
        void testQuadradoExiste() throws Exception {
            Method quadradoExisteMethod = TabuleiroFront.class.getDeclaredMethod("quadradoExiste", int.class, int.class);
            quadradoExisteMethod.setAccessible(true);

            // Casos de erro
            assertFalse((boolean) quadradoExisteMethod.invoke(tabuleiroFront, -1, 5), "Linha < 0 deve ser falso.");
            assertFalse((boolean) quadradoExisteMethod.invoke(tabuleiroFront, LINHAS, 5), "Linha >= linha_size deve ser falso.");
            assertFalse((boolean) quadradoExisteMethod.invoke(tabuleiroFront, 5, -1), "Coluna < 0 deve ser falso.");
            assertFalse((boolean) quadradoExisteMethod.invoke(tabuleiroFront, 5, COLUNAS), "Coluna >= coluna_size deve ser falso.");

            // Casos de sucesso
            assertTrue((boolean) quadradoExisteMethod.invoke(tabuleiroFront, 0, 0), "Coordenada (0,0) deve ser verdadeira.");
            assertTrue((boolean) quadradoExisteMethod.invoke(tabuleiroFront, LINHAS - 1, COLUNAS - 1), "Última coordenada deve ser verdadeira.");
            assertTrue((boolean) quadradoExisteMethod.invoke(tabuleiroFront, 5, 10), "Coordenada no meio deve ser verdadeira.");
        }
    }

    @Nested
    @DisplayName("Testes para atualizaQuadrado(QuadradoFront)")
    class AtualizaQuadradoTests {
        @Test
        @DisplayName("Novo quadrado deve ser igual ao quadrado informado e ser substituído na posição correta")
        void testAtualizaQuadrado() throws Exception {
            Localizacao loc = new Localizacao(2, 2);
            QuadradoFront novoQuadrado = new QuadradoFront(true, 5, true, loc, true);

            tabuleiroFront.atualizaQuadrado(novoQuadrado);

            ArrayList<ArrayList<QuadradoFront>> grid = getGridInterno(tabuleiroFront);
            QuadradoFront quadradoNoTabuleiro = grid.get(2).get(2);

            assertSame(novoQuadrado, quadradoNoTabuleiro, "O objeto no tabuleiro deve ser o mesmo que foi passado para atualização.");
        }
    }

    @Nested
    @DisplayName("Testes para Getters de Estado (isAberto, isMarcado, etc.)")
    class GettersDeEstadoTests {
        private final Localizacao locValida = new Localizacao(3, 3);
        private final Localizacao locInvalida = new Localizacao(LINHAS, COLUNAS);

        @BeforeEach
        void setupBoardState() {
            // Cria um quadrado customizado e o insere no tabuleiro para teste
            QuadradoFront quadradoCustom = new QuadradoFront(true, 8, true, locValida, true);
            tabuleiroFront.atualizaQuadrado(quadradoCustom);
        }

        // Testes para isAberto
        @Test
        @DisplayName("isAberto deve retornar true para um quadrado aberto")
        void testIsAbertoTrue() throws ForaDoTabuleiroException {
            assertTrue(tabuleiroFront.isAberto(locValida));
        }

        @Test
        @DisplayName("isAberto deve retornar false para um quadrado não aberto")
        void testIsAbertoFalse() throws ForaDoTabuleiroException {
            assertFalse(tabuleiroFront.isAberto(new Localizacao(0, 0))); // Posição não alterada
        }

        @Test
        @DisplayName("isAberto deve lançar ForaDoTabuleiroException para localização inválida")
        void testIsAbertoException() {
            assertThrows(ForaDoTabuleiroException.class, () -> tabuleiroFront.isAberto(locInvalida));
        }

        // Testes para isMarcado
        @Test
        @DisplayName("isMarcado deve retornar true para um quadrado marcado")
        void testIsMarcadoTrue() throws ForaDoTabuleiroException {
            assertTrue(tabuleiroFront.isMarcado(locValida));
        }

        @Test
        @DisplayName("isMarcado deve retornar false para um quadrado não marcado")
        void testIsMarcadoFalse() throws ForaDoTabuleiroException {
            assertFalse(tabuleiroFront.isMarcado(new Localizacao(0, 0)));
        }

        @Test
        @DisplayName("isMarcado deve lançar ForaDoTabuleiroException para localização inválida")
        void testIsMarcadoException() {
            assertThrows(ForaDoTabuleiroException.class, () -> tabuleiroFront.isMarcado(locInvalida));
        }

        // Testes para isBomba
        @Test
        @DisplayName("isBomba deve retornar true para um quadrado com bomba")
        void testIsBombaTrue() throws ForaDoTabuleiroException {
            assertTrue(tabuleiroFront.isBomba(locValida));
        }

        @Test
        @DisplayName("isBomba deve retornar false para um quadrado sem bomba")
        void testIsBombaFalse() throws ForaDoTabuleiroException {
            assertFalse(tabuleiroFront.isBomba(new Localizacao(0, 0)));
        }

        @Test
        @DisplayName("isBomba deve lançar ForaDoTabuleiroException para localização inválida")
        void testIsBombaException() {
            assertThrows(ForaDoTabuleiroException.class, () -> tabuleiroFront.isBomba(locInvalida));
        }

        // Testes para getVizinhosPerigosos
        @Test
        @DisplayName("getVizinhosPerigosos deve retornar o número correto")
        void testGetVizinhosPerigosos() throws ForaDoTabuleiroException {
            assertEquals(8, tabuleiroFront.getVizinhosPerigosos(locValida));
        }

        @Test
        @DisplayName("getVizinhosPerigosos deve lançar ForaDoTabuleiroException para localização inválida")
        void testGetVizinhosPerigososException() {
            assertThrows(ForaDoTabuleiroException.class, () -> tabuleiroFront.getVizinhosPerigosos(locInvalida));
        }
    }
}