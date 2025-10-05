package org.minesweeper.view;

import org.assertj.swing.core.MouseButton;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JOptionPaneFixture;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.minesweeper.model.Icons;
import org.minesweeper.navigator.NavegadorTelaJogoListener;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.minesweeper.model.Localizacao;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ViewTelaJogoTest {

    private FrameFixture window; // O FrameFixture ainda controla a janela...
    private ViewTelaJogo view;   // ...mas a View agora é o JPanel.

    @Mock
    private NavegadorTelaJogoListener mockListener;

    @BeforeEach
    void setUp() {
        // 1. Cria a sua View (o JPanel) de forma segura na thread do Swing.
        //    O GuiActionRunner.execute garante que o construtor seja chamado na EDT.
        view = GuiActionRunner.execute(ViewTelaJogo::new);
        view.subscribe(mockListener); // Subscreve o listener mockado

        // 2. Cria um JFrame de teste que irá "hospedar" a sua View durante o teste.
        JFrame testFrame = GuiActionRunner.execute(() -> {
            JFrame frame = new JFrame("Janela de Teste");
            frame.add(view); // Adiciona a sua view (JPanel) dentro da janela
            return frame;
        });

        // 3. Usa a referência direta do JFrame que acabamos de criar para o FrameFixture.
        window = new FrameFixture(testFrame);
        window.show(); // Mostra a janela para que os testes possam interagir com ela
    }

    @AfterEach
    void tearDown() {
        window.cleanUp();
    }

    @Test
    @DisplayName("mostraJogo: Garante que a quantidade correta de botões é criada")
    void testMostraJogo_CriaQuantidadeCorretaDeQuadrados() {
        GuiActionRunner.execute(() -> view.mostraJogo(8, 8));

        assertEquals(64, window.panel("painelGrid").target().getComponentCount());

        JButtonFixture botao = window.button("btn-3-3");
        botao.requireEnabled();
        botao.requireText("");
    }

    @Test
    @DisplayName("mostraJogo: Garante que os eventos de mouse direito notificam os listeners")
    void testMostraJogo_EventosDeMouseDireito() {
        GuiActionRunner.execute(() -> view.mostraJogo(5, 5));

        window.button("btn-2-2").click(MouseButton.RIGHT_BUTTON);

        ArgumentCaptor<Localizacao> captorDireito = ArgumentCaptor.forClass(Localizacao.class);
        verify(mockListener).onBotaoDireito(captorDireito.capture());
        assertEquals(2, captorDireito.getValue().getLinha());
        assertEquals(2, captorDireito.getValue().getColuna());
    }

    @Test
    @DisplayName("mostraJogo: Garante que os eventos de mouse esquerdo notificam os listeners")
    void testMostraJogo_EventosDeMouseEsquerdo() {
        GuiActionRunner.execute(() -> view.mostraJogo(5, 5));

        window.button("btn-2-2").click(MouseButton.LEFT_BUTTON);

        ArgumentCaptor<Localizacao> captorEsquerdo = ArgumentCaptor.forClass(Localizacao.class);
        verify(mockListener).onBotaoEsquerdo(captorEsquerdo.capture());
        assertEquals(2, captorEsquerdo.getValue().getLinha());
        assertEquals(2, captorEsquerdo.getValue().getColuna());
    }

    @Test
    @DisplayName("limparRecursos: Garante que a janela é fechada")
    void testLimparRecursos() {
        window.requireVisible();
        GuiActionRunner.execute(() -> view.limparRecursos());
        window.requireNotVisible();
    }

    @Test
    @DisplayName("mostraQuadradoAberto: Deve mudar a cor do botão e mostrar o número")
    void testMostraQuadradoAberto() {
        GuiActionRunner.execute(() -> view.mostraJogo(5, 5));
        Localizacao loc = new Localizacao(2, 3);

        GuiActionRunner.execute(() -> view.mostraQuadradoAberto(loc, 3));

        JButtonFixture botao = window.button("btn-2-3");
        botao.requireEnabled();
        botao.background().requireEqualTo(new Color(255, 255, 255));
        botao.requireText("3");
    }

    @Test
    @DisplayName("mostraQuadradoBomba: Deve desabilitar o botão e mostrar 'B'")
    void testMostraQuadradoBomba() {
        GuiActionRunner.execute(() -> view.mostraJogo(5, 5));
        Localizacao loc = new Localizacao(4, 0);

        GuiActionRunner.execute(() -> view.mostraQuadradoBomba(loc));

        JButtonFixture botao = window.button("btn-4-0");
        botao.requireDisabled();
        botao.requireText("B");
    }

    @Test
    @DisplayName("mostraQuadradoMarcado/Desmarcado: Deve mostrar/remover a bandeira")
    void testMostraQuadradoMarcadoDesmarcado() {
        GuiActionRunner.execute(() -> view.mostraJogo(5, 5));
        Localizacao loc = new Localizacao(1, 1);
        JButtonFixture botao = window.button("btn-1-1");

        // Marca
        GuiActionRunner.execute(() -> view.mostraQuadradoMarcado(loc));
        botao.requireEnabled();
        botao.requireText("");
        assertThat(botao.target().getIcon()).isSameAs(Icons.FLAG_ICON);

        // Desmarca
        GuiActionRunner.execute(() -> view.mostraQuadradoDesmarcado(loc));
        botao.requireEnabled();
        botao.requireText("");
        assertThat(botao.target().getIcon()).isNull();
    }

    @Test
    @DisplayName("mostraErroCriacaoJogo: Deve mostrar JOptionPane e notificar no OK")
    void testMostraErroCriacaoJogo() {
        // Ação que mostra o JOptionPane precisa rodar em outra thread para não bloquear o teste
        Thread acaoMostraErro = new Thread(() -> view.mostraErroCriacaoJogo());
        acaoMostraErro.start();

        // Encontra o JOptionPane que apareceu
        JOptionPaneFixture optionPane = window.optionPane().requireVisible();
        optionPane.requireErrorMessage().requireMessage("Problemas na criação do jogo.");

        // Simula o clique no botão OK
        optionPane.okButton().click();

        // Verifica se o listener foi notificado
        verify(mockListener).confirmouErro();
    }

    @Nested
    @DisplayName("Testes para subscribe() e unsubscribe()")
    class SubscribeUnsubscribeTests {

        // O @BeforeEach da classe principal já cria a 'view' e o 'mockListener'

        @SuppressWarnings("unchecked")
        private List<NavegadorTelaJogoListener> getListenersInternos() throws Exception {
            Field listenersField = ViewTelaJogo.class.getDeclaredField("listeners");
            listenersField.setAccessible(true);
            return (List<NavegadorTelaJogoListener>) listenersField.get(view);
        }

        @Test
        @DisplayName("subscribe: Não deve adicionar um listener que já está na lista")
        void subscribe_naoDeveAdicionarListenerSeJaExistir() throws Exception {
            // Arrange: o @BeforeEach principal já adiciona o listener uma vez.
            assertEquals(1, getListenersInternos().size(), "Pré-condição: a lista já deve conter o listener.");

            // Act
            GuiActionRunner.execute(() -> view.subscribe(mockListener));

            // Assert
            assertEquals(1, getListenersInternos().size(), "O tamanho da lista não deveria mudar.");
        }

        @Test
        @DisplayName("unsubscribe: Deve remover um listener que está na lista")
        void unsubscribe_deveRemoverListenerSeExistir() throws Exception {
            // Arrange
            assertEquals(1, getListenersInternos().size(), "Pré-condição: a lista deve conter um listener.");

            // Act
            GuiActionRunner.execute(() -> view.unsubscribe(mockListener));

            // Assert
            assertTrue(getListenersInternos().isEmpty(), "A lista deveria ficar vazia.");
        }

        @Test
        @DisplayName("unsubscribe: Não deve fazer nada se o listener não estiver na lista")
        void unsubscribe_naoDeveFazerNadaSeListenerNaoExistir() throws Exception {
            // Arrange: primeiro, removemos o listener existente
            GuiActionRunner.execute(() -> view.unsubscribe(mockListener));
            assertTrue(getListenersInternos().isEmpty(), "Pré-condição: a lista deve estar vazia.");

            // Act & Assert
            assertDoesNotThrow(() -> {
                GuiActionRunner.execute(() -> view.unsubscribe(mockListener));
                assertTrue(getListenersInternos().isEmpty(), "A lista deve permanecer vazia.");
            });
        }
    }
}