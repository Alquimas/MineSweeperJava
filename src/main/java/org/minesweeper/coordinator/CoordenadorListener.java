package org.minesweeper.coordinator;

public interface CoordenadorListener {
    void fimJogo(boolean ganhou);
    void reiniciaJogo();
    void encerraAplicacao();
    void encerraAplicacaoErroNaoCriouJogo();
}
