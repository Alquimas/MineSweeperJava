package org.minesweeper.navigator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.minesweeper.coordinator.CoordenadorListener;
import org.minesweeper.navigator.NavegadorTelaJogo;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.minesweeper.controller.ControllerTabuleiro;
import org.minesweeper.model.Localizacao;
import org.minesweeper.model.QuadradoFront;
import org.minesweeper.model.TabuleiroFront;
import org.minesweeper.view.ViewTelaJogo;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NavegadorTelaJogoTest {

    @Mock
    private JFrame mockFrame;
    @Mock
    private ViewTelaJogo mockView;
    @Mock
    private ControllerTabuleiro mockController;
    @Mock
    private CoordenadorListener mockCoordenadorListener;

    // A instância a ser testada. Será criada e configurada no setUp.
    private NavegadorTelaJogo navegador;

    @BeforeEach
    void setUp() throws Exception {
        // Como o construtor é vazio, instanciamos o navegador...
        navegador = new NavegadorTelaJogo();

        // ...e então usamos REFLEXÃO para injetar nossas dependências mockadas.
        // Isso nos dá controle total sobre a classe em teste.
        setField(navegador, "view", mockView);
        setField(navegador, "controller", mockController);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Nested
    @DisplayName("Testes para iniciar()")
    class IniciarTests {
        @Test
        @DisplayName("Garante que a view é configurada corretamente antes de criar o jogo")
        void iniciar_deveConfigurarView() {
            // Act
            navegador.iniciar(mockFrame);

            // Assert
            verify(mockView).subscribe(navegador);
            verify(mockFrame).add(mockView);
        }
    }

    @Nested
    @DisplayName("Testes para a lógica de criaJogo() (verificada através de iniciar)")
    class CriaJogoLogicTests {
        @Test
        @DisplayName("Garante que controller.iniciarNovoJogo() é chamado")
        void criaJogo_deveChamarController() {
            // Act: Chamamos o método público que aciona a lógica de criaJogo()
            navegador.iniciar(mockFrame);

            // Assert
            verify(mockController).iniciarNovoJogo(anyInt(), anyInt(), anyInt());
        }

        @Test
        @DisplayName("Caso controller retorne null, a view deve mostrar erro")
        void criaJogo_quandoControllerFalha_mostraErro() {
            // Arrange
            when(mockController.iniciarNovoJogo(anyInt(), anyInt(), anyInt())).thenReturn(null);

            // Act: Chamamos o método público
            navegador.iniciar(mockFrame);

            // Assert
            verify(mockView).mostraErroCriacaoJogo();
            verify(mockView, never()).mostraJogo(anyInt(), anyInt());
        }

        @Test
        @DisplayName("Caso controller retorne TabuleiroFront, a view deve mostrar o jogo")
        void criaJogo_quandoControllerFunciona_mostraJogo() {
            // Arrange
            TabuleiroFront tabuleiroFrontValido = new TabuleiroFront(15, 20);
            when(mockController.iniciarNovoJogo(anyInt(), anyInt(), anyInt())).thenReturn(tabuleiroFrontValido);

            // Act: Chamamos o método público
            navegador.iniciar(mockFrame);

            // Assert
            verify(mockView).mostraJogo(15, 20);
            verify(mockView, never()).mostraErroCriacaoJogo();
        }
    }

    @Nested
    @DisplayName("Testes para confirmouErro()")
    class ConfirmouErroTests {
        @Test
        @DisplayName("Garante que os recursos da view são limpos e os listeners são notificados")
        void confirmouErro_chamaDestruirENotificaListeners() {
            // Arrange
            navegador.subscribe(mockCoordenadorListener);

            // Act
            navegador.confirmouErro();

            // Assert: Verificamos os efeitos públicos do método privado destruir()
            verify(mockView).unsubscribe(navegador);
            verify(mockView).limparRecursos();
            verify(mockFrame).remove(mockView);

            // Verificamos a notificação ao listener
            verify(mockCoordenadorListener).encerraAplicacaoErroNaoCriouJogo();
        }
    }

    @Nested
    @DisplayName("Testes para onBotaoDireito(Localizacao)")
    class OnBotaoDireitoTests {

        // O @BeforeEach da classe principal já cria o 'navegador' e injeta os mocks via reflexão.

        @Test
        @DisplayName("Garante que controller.clicarBotaoDireito é sempre chamado")
        void onBotaoDireito_sempreChamaController() {
            Localizacao loc = new Localizacao(1, 1);
            // Act
            navegador.onBotaoDireito(loc);
            // Assert
            verify(mockController).clicarBotaoDireito(loc);
        }

        @Test
        @DisplayName("Caso controller retorne null, garante que nada aconteça na view")
        void onBotaoDireito_comRetornoNull_naoFazNadaNaView() {
            // Arrange
            Localizacao loc = new Localizacao(1, 1);
            when(mockController.clicarBotaoDireito(loc)).thenReturn(null);

            // Act
            navegador.onBotaoDireito(loc);

            // Assert
            // Garante que nenhum método de atualização da view foi chamado
            verify(mockView, never()).mostraQuadradoMarcado(any(Localizacao.class));
            verify(mockView, never()).mostraQuadradoDesmarcado(any(Localizacao.class));
        }

        @Test
        @DisplayName("Caso retorne um QuadradoFront marcado, chama view.mostraQuadradoMarcado")
        void onBotaoDireito_comRetornoMarcado_chamaViewCorreta() {
            // Arrange
            Localizacao loc = new Localizacao(2, 2);
            QuadradoFront qfMarcado = new QuadradoFront(false, -1, true, loc, false);
            when(mockController.clicarBotaoDireito(loc)).thenReturn(qfMarcado);

            // Act
            navegador.onBotaoDireito(loc);

            // Assert
            verify(mockView).mostraQuadradoMarcado(loc);
            verify(mockView, never()).mostraQuadradoDesmarcado(any(Localizacao.class));
        }

        @Test
        @DisplayName("Caso retorne um QuadradoFront não marcado, chama view.mostraQuadradoDesmarcado")
        void onBotaoDireito_comRetornoNaoMarcado_chamaViewCorreta() {
            // Arrange
            Localizacao loc = new Localizacao(3, 3);
            QuadradoFront qfDesmarcado = new QuadradoFront(false, -1, false, loc, false);
            when(mockController.clicarBotaoDireito(loc)).thenReturn(qfDesmarcado);

            // Act
            navegador.onBotaoDireito(loc);

            // Assert
            verify(mockView).mostraQuadradoDesmarcado(loc);
            verify(mockView, never()).mostraQuadradoMarcado(any(Localizacao.class));
        }
    }

    @Nested
    @DisplayName("Testes para onBotaoEsquerdo(Localizacao)")
    class OnBotaoEsquerdoTests {

        @Test
        @DisplayName("Garante que controller.clicarBotaoEsquerdo é sempre chamado")
        void onBotaoEsquerdo_sempreChamaController() {
            Localizacao loc = new Localizacao(1, 1);
            // Act
            navegador.onBotaoEsquerdo(loc);
            // Assert
            verify(mockController).clicarBotaoEsquerdo(loc);
        }

        @Test
        @DisplayName("Caso controller retorne null, garante que nada é feito na view")
        void onBotaoEsquerdo_comRetornoNull_naoFazNadaNaView() {
            // Arrange
            Localizacao loc = new Localizacao(1, 1);
            when(mockController.clicarBotaoEsquerdo(loc)).thenReturn(null);

            // Act
            navegador.onBotaoEsquerdo(loc);

            // Assert
            verifyNoInteractions(mockView); // Verifica que nenhuma interação ocorreu com a view
        }

        @Test
        @DisplayName("Caso retorne uma bomba, mostra a bomba, destrói e notifica derrota")
        void onBotaoEsquerdo_comBomba_executaFluxoDeDerrota() {
            // Arrange
            navegador.subscribe(mockCoordenadorListener);
            Localizacao locBomba = new Localizacao(2, 2);
            ArrayList<QuadradoFront> listaComBomba = new ArrayList<>();
            listaComBomba.add(new QuadradoFront(true, -1, false, locBomba, true));

            when(mockController.clicarBotaoEsquerdo(locBomba)).thenReturn(listaComBomba);

            // Act
            navegador.onBotaoEsquerdo(locBomba);

            // Assert
            verify(mockView).mostraQuadradoBomba(locBomba);
            // Verifica os efeitos do método privado destruir()
            verify(mockView).limparRecursos();
            verify(mockView).unsubscribe(navegador);
            // Verifica a notificação de fim de jogo
            verify(mockCoordenadorListener).fimJogo(false);
        }

        @Test
        @DisplayName("Caso retorne quadrados seguros sem vitória, apenas os abre na view")
        void onBotaoEsquerdo_comQuadradosSegurosSemVitoria_apenasAbreQuadrados() {
            // Arrange
            navegador.subscribe(mockCoordenadorListener);
            Localizacao locClick = new Localizacao(3, 3);
            ArrayList<QuadradoFront> listaSegura = new ArrayList<>();
            listaSegura.add(new QuadradoFront(true, 1, false, new Localizacao(3,3), false));
            listaSegura.add(new QuadradoFront(true, 2, false, new Localizacao(3,4), false));

            when(mockController.clicarBotaoEsquerdo(locClick)).thenReturn(listaSegura);
            when(mockController.ganhou()).thenReturn(false); // Simula que o jogo ainda não foi ganho

            // Act
            navegador.onBotaoEsquerdo(locClick);

            // Assert
            // Verifica que a view foi atualizada para cada quadrado
            verify(mockView).mostraQuadradoAberto(new Localizacao(3,3), 1);
            verify(mockView).mostraQuadradoAberto(new Localizacao(3,4), 2);
            // Garante que o estado de vitória foi verificado
            verify(mockController).ganhou();
            // Garante que o fluxo de fim de jogo NÃO foi acionado
            verify(mockCoordenadorListener, never()).fimJogo(anyBoolean());
            verify(mockView, never()).limparRecursos();
        }

        @Test
        @DisplayName("Caso retorne quadrados seguros com vitória, abre, destrói e notifica vitória")
        void onBotaoEsquerdo_comQuadradosSegurosComVitoria_executaFluxoDeVitoria() {
            // Arrange
            navegador.subscribe(mockCoordenadorListener);
            Localizacao locClick = new Localizacao(4, 4);
            ArrayList<QuadradoFront> listaSegura = new ArrayList<>();
            listaSegura.add(new QuadradoFront(true, 1, false, locClick, false));

            when(mockController.clicarBotaoEsquerdo(locClick)).thenReturn(listaSegura);
            when(mockController.ganhou()).thenReturn(true); // Simula que o jogo foi ganho

            // Act
            navegador.onBotaoEsquerdo(locClick);

            // Assert
            verify(mockView).mostraQuadradoAberto(locClick, 1);
            verify(mockController).ganhou();
            // Verifica os efeitos do método privado destruir()
            verify(mockView).limparRecursos();
            verify(mockView).unsubscribe(navegador);
            // Verifica a notificação de fim de jogo
            verify(mockCoordenadorListener).fimJogo(true);
        }
    }

    @Nested
    @DisplayName("Testes para subscribe() e unsubscribe()")
    class SubscribeUnsubscribeTests {

        // O @BeforeEach da classe principal já cria a instância 'navegador'
        // e os mocks necessários.

        /**
         * Método auxiliar que usa Reflexão para obter a lista privada de listeners
         * de dentro da instância do navegador.
         */
        @SuppressWarnings("unchecked")
        private List<CoordenadorListener> getListenersInternos() throws Exception {
            Field listenersField = NavegadorTelaJogo.class.getDeclaredField("listeners");
            listenersField.setAccessible(true);
            return (List<CoordenadorListener>) listenersField.get(navegador);
        }

        @Test
        @DisplayName("subscribe: Deve adicionar um listener que não está na lista")
        void subscribe_deveAdicionarListenerSeNaoExistir() throws Exception {
            // Arrange: Garante que a lista de listeners começa vazia
            List<CoordenadorListener> listeners = getListenersInternos();
            assertTrue(listeners.isEmpty(), "A lista de listeners deveria começar vazia.");

            // Act: Inscreve o listener
            navegador.subscribe(mockCoordenadorListener);

            // Assert: A lista agora deve conter 1 listener
            assertEquals(1, listeners.size(), "A lista deveria conter um listener após a inscrição.");
            assertSame(mockCoordenadorListener, listeners.get(0), "O listener adicionado deve ser o mock.");
        }

        @Test
        @DisplayName("subscribe: Não deve adicionar um listener que já está na lista")
        void subscribe_naoDeveAdicionarListenerSeJaExistir() throws Exception {
            // Arrange: Inscreve o listener uma vez
            navegador.subscribe(mockCoordenadorListener);
            List<CoordenadorListener> listeners = getListenersInternos();
            assertEquals(1, listeners.size(), "Pré-condição: A lista deveria conter um listener.");

            // Act: Tenta inscrever o mesmo listener novamente
            navegador.subscribe(mockCoordenadorListener);

            // Assert: O tamanho da lista não deve mudar
            assertEquals(1, listeners.size(), "A lista não deveria permitir listeners duplicados.");
        }

        @Test
        @DisplayName("unsubscribe: Deve remover um listener que está na lista")
        void unsubscribe_deveRemoverListenerSeExistir() throws Exception {
            // Arrange: Inscreve o listener para garantir que ele existe na lista
            navegador.subscribe(mockCoordenadorListener);
            List<CoordenadorListener> listeners = getListenersInternos();
            assertFalse(listeners.isEmpty(), "Pré-condição: A lista não deveria estar vazia.");

            // Act: Remove a inscrição do listener
            navegador.unsubscribe(mockCoordenadorListener);

            // Assert: A lista agora deve estar vazia
            assertTrue(listeners.isEmpty(), "A lista deveria estar vazia após remover o listener.");
        }

        @Test
        @DisplayName("unsubscribe: Não deve fazer nada (nem lançar erro) se o listener não estiver na lista")
        void unsubscribe_naoDeveFazerNadaSeListenerNaoExistir() throws Exception {
            // Arrange: A lista de listeners está vazia
            List<CoordenadorListener> listeners = getListenersInternos();
            assertTrue(listeners.isEmpty());

            // Act & Assert: Tentar remover um listener inexistente não deve lançar uma exceção
            assertDoesNotThrow(() -> navegador.unsubscribe(mockCoordenadorListener));

            // Garante que a lista permaneceu vazia
            assertTrue(listeners.isEmpty());
        }
    }
}