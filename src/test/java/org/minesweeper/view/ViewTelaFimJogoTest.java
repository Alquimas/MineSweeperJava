package org.minesweeper.view;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.minesweeper.navigator.NavegadorTelaFimJogoListener;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ViewTelaFimJogoTest {

    private FrameFixture window;
    private ViewTelaFimJogo view;

    @Mock
    private NavegadorTelaFimJogoListener mockListener;

    @BeforeEach
    void setUp() {
        // 1. Cria a sua View (o JPanel) de forma segura na thread do Swing.
        view = GuiActionRunner.execute(ViewTelaFimJogo::new);
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

    @Nested
    @DisplayName("Testes para mostraTelaPerdeuJogo()")
    class MostraTelaPerdeuJogo {
        @Test
        @DisplayName("Garante que a mensagem e os botões corretos são mostrados")
        void testMostraTelaPerdeuJogo_Componentes() {
            GuiActionRunner.execute(() -> view.mostraTelaPerdeuJogo());

            window.label("lblMensagem").requireText("Você Perdeu!");
            window.button("btnNovoJogo").requireVisible().requireEnabled().requireText("Novo Jogo");
            window.button("btnFinalizarJogo").requireVisible().requireEnabled().requireText("Finalizar Jogo");
        }

        @Test
        @DisplayName("Garante que os botões notificam os listeners corretamente")
        void testMostraTelaPerdeuJogo_AcoesDosBotoes() {
            GuiActionRunner.execute(() -> view.mostraTelaPerdeuJogo());

            // Simula clique no botão "Novo Jogo"
            window.button("btnNovoJogo").click();
            verify(mockListener).reiniciarJogo(); // Verifica se o listener foi notificado

            // Simula clique no botão "Finalizar Jogo"
            window.button("btnFinalizarJogo").click();
            verify(mockListener).finalizarJogo(); // Verifica se o listener foi notificado
        }
    }

    @Nested
    @DisplayName("Testes para mostraTelaGanhouJogo()")
    class MostraTelaGanhouJogo {
        @Test
        @DisplayName("Garante que a mensagem e os botões corretos são mostrados")
        void testMostraTelaGanhouJogo_Componentes() {
            GuiActionRunner.execute(() -> view.mostraTelaGanhouJogo());

            window.label("lblMensagem").requireText("Você Ganhou!");
            window.button("btnNovoJogo").requireVisible().requireEnabled().requireText("Novo Jogo");
            window.button("btnFinalizarJogo").requireVisible().requireEnabled().requireText("Finalizar Jogo");
        }

        @Test
        @DisplayName("Garante que os botões notificam os listeners corretamente")
        void testMostraTelaGanhouJogo_AcoesDosBotoes() {
            GuiActionRunner.execute(() -> view.mostraTelaGanhouJogo());

            window.button("btnNovoJogo").click();
            verify(mockListener).reiniciarJogo();

            window.button("btnFinalizarJogo").click();
            verify(mockListener).finalizarJogo();
        }
    }

    @Test
    @DisplayName("limparRecursos(): Garante que a janela é fechada")
    void testLimparRecursos() {
        window.requireVisible();
        GuiActionRunner.execute(() -> view.limparRecursos());
        window.requireNotVisible();
    }

    @Test
    @DisplayName("subscribe/unsubscribe: Deve adicionar e remover listeners corretamente")
    void testSubscribeUnsubscribe() throws Exception {
        // Usa reflection para acessar a lista privada de listeners
        Field listenersField = ViewTelaFimJogo.class.getDeclaredField("listeners");
        listenersField.setAccessible(true);
        List<NavegadorTelaFimJogoListener> listeners = (List<NavegadorTelaFimJogoListener>) listenersField.get(view);

        // O listener mock já foi adicionado no @BeforeEach
        assertEquals(1, listeners.size());

        // Tentar adicionar de novo não deve fazer nada
        GuiActionRunner.execute(() -> view.subscribe(mockListener));
        assertEquals(1, listeners.size());

        // Remove o listener
        GuiActionRunner.execute(() -> view.unsubscribe(mockListener));
        assertEquals(0, listeners.size());

        // Tentar remover de novo não deve fazer nada
        GuiActionRunner.execute(() -> view.unsubscribe(mockListener));
        assertEquals(0, listeners.size());
    }
}