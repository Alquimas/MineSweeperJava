package org.minesweeper.navigator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.minesweeper.coordinator.CoordenadorListener;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.minesweeper.view.ViewTelaFimJogo;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NavegadorTelaFimJogoTest {

    @Mock
    private JFrame mockFrame;
    @Mock
    private CoordenadorListener mockCoordenadorListener;

    // Não usamos @InjectMocks porque o construtor é vazio e as dependências são criadas/injetadas depois

    @Nested
    @DisplayName("Testes para iniciar(JFrame, boolean)")
    class IniciarTests {

        @Test
        @DisplayName("Caso boolean seja true, deve mostrar a tela de vitória")
        void iniciar_comTrue_mostraTelaDeVitoria() {
            // Arrange
            NavegadorTelaFimJogo navegador = new NavegadorTelaFimJogo();

            // Intercepta a criação de QUALQUER 'new ViewTelaFimJogo()' dentro deste bloco try
            try (MockedConstruction<ViewTelaFimJogo> mockedViewConstruction = Mockito.mockConstruction(ViewTelaFimJogo.class)) {

                // Act
                navegador.iniciar(mockFrame, true); // Passa 'true' para ganhou

                // Assert
                // Pega a instância mockada da View que foi criada
                ViewTelaFimJogo mockView = mockedViewConstruction.constructed().get(0);

                verify(mockView).subscribe(navegador);
                verify(mockFrame).add(mockView);
                verify(mockView).mostraTelaGanhouJogo(); // Verifica se o caminho de vitória foi chamado
                verify(mockView, never()).mostraTelaPerdeuJogo(); // Garante que o de derrota NÃO foi
            }
        }

        @Test
        @DisplayName("Caso boolean seja false, deve mostrar a tela de derrota")
        void iniciar_comFalse_mostraTelaDeDerrota() {
            // Arrange
            NavegadorTelaFimJogo navegador = new NavegadorTelaFimJogo();
            try (MockedConstruction<ViewTelaFimJogo> mockedViewConstruction = Mockito.mockConstruction(ViewTelaFimJogo.class)) {

                // Act
                navegador.iniciar(mockFrame, false); // Passa 'false' para ganhou

                // Assert
                ViewTelaFimJogo mockView = mockedViewConstruction.constructed().get(0);

                verify(mockView).subscribe(navegador);
                verify(mockFrame).add(mockView);
                verify(mockView).mostraTelaPerdeuJogo(); // Verifica se o caminho de derrota foi chamado
                verify(mockView, never()).mostraTelaGanhouJogo(); // Garante que o de vitória NÃO foi
            }
        }
    }

    @Nested
    @DisplayName("Testes para os métodos de callback do listener da View")
    class CallbackTests {

        private NavegadorTelaFimJogo navegador;
        @Mock private ViewTelaFimJogo mockView; // Usamos um mock normal aqui

        @BeforeEach
        void setUp() throws Exception {
            navegador = new NavegadorTelaFimJogo();
            // Injeta os mocks via reflexão, pois não temos construtor para isso
            setField(navegador, "view", mockView);
            setField(navegador, "frame", mockFrame);
        }

        @Test
        @DisplayName("reiniciarJogo: Deve destruir a view e notificar o listener")
        void reiniciarJogo_deveDestruirENotificar() {
            // Arrange
            navegador.subscribe(mockCoordenadorListener);

            // Act
            navegador.reiniciarJogo();

            // Assert
            // Verifica os efeitos do método privado 'destruir'
            verify(mockView).unsubscribe(navegador);
            verify(mockView).limparRecursos();
            verify(mockFrame).remove(mockView);

            // Verifica a notificação ao listener "pai"
            verify(mockCoordenadorListener).reiniciaJogo();
        }

        @Test
        @DisplayName("finalizarJogo: Deve destruir a view e notificar o listener")
        void finalizarJogo_deveDestruirENotificar() {
            // Arrange
            navegador.subscribe(mockCoordenadorListener);

            // Act
            navegador.finalizarJogo();

            // Assert
            verify(mockView).unsubscribe(navegador);
            verify(mockView).limparRecursos();
            verify(mockFrame).remove(mockView);

            verify(mockCoordenadorListener).encerraAplicacao();
        }
    }

    @Nested
    @DisplayName("Testes para subscribe() e unsubscribe()")
    class SubscribeUnsubscribeTests {

        private NavegadorTelaFimJogo navegador;

        @BeforeEach
        void setUp() {
            // Cria uma nova instância limpa para cada teste deste grupo
            navegador = new NavegadorTelaFimJogo();
        }

        /**
         * Método auxiliar que usa Reflexão para obter a lista privada de listeners.
         */
        @SuppressWarnings("unchecked")
        private List<CoordenadorListener> getListenersInternos() throws Exception {
            Field listenersField = NavegadorTelaFimJogo.class.getDeclaredField("listeners");
            listenersField.setAccessible(true);
            return (List<CoordenadorListener>) listenersField.get(navegador);
        }

        @Test
        @DisplayName("subscribe: Deve adicionar um listener que não está na lista")
        void subscribe_deveAdicionarListenerSeNaoExistir() throws Exception {
            // Arrange
            assertTrue(getListenersInternos().isEmpty(), "A lista deveria começar vazia.");

            // Act
            navegador.subscribe(mockCoordenadorListener);

            // Assert
            List<CoordenadorListener> listeners = getListenersInternos();
            assertEquals(1, listeners.size(), "A lista deveria conter um listener.");
            assertSame(mockCoordenadorListener, listeners.get(0));
        }

        @Test
        @DisplayName("subscribe: Não deve adicionar um listener que já está na lista")
        void subscribe_naoDeveAdicionarListenerSeJaExistir() throws Exception {
            // Arrange
            navegador.subscribe(mockCoordenadorListener);
            assertEquals(1, getListenersInternos().size(), "Pré-condição: a lista já deve conter o listener.");

            // Act
            navegador.subscribe(mockCoordenadorListener);

            // Assert
            assertEquals(1, getListenersInternos().size(), "O tamanho da lista não deveria mudar.");
        }

        @Test
        @DisplayName("unsubscribe: Deve remover um listener que está na lista")
        void unsubscribe_deveRemoverListenerSeExistir() throws Exception {
            // Arrange
            navegador.subscribe(mockCoordenadorListener);
            assertFalse(getListenersInternos().isEmpty(), "Pré-condição: a lista não deve estar vazia.");

            // Act
            navegador.unsubscribe(mockCoordenadorListener);

            // Assert
            assertTrue(getListenersInternos().isEmpty(), "A lista deveria ficar vazia.");
        }

        @Test
        @DisplayName("unsubscribe: Não deve fazer nada se o listener não estiver na lista")
        void unsubscribe_naoDeveFazerNadaSeListenerNaoExistir() {
            // Arrange
            // A lista está vazia

            // Act & Assert
            assertDoesNotThrow(() -> {
                navegador.unsubscribe(mockCoordenadorListener);
                assertTrue(getListenersInternos().isEmpty(), "A lista deve permanecer vazia.");
            });
        }
    }

    // Método auxiliar para injetar mocks via reflexão
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}