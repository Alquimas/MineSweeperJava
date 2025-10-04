package org.minesweeper.coordinator;

public interface CoordenadorListener {
    public abstract void fimJogo(boolean ganhou);
    public abstract void reiniciaJogo();
    public abstract void encerraAplicacao();
    public abstract void encerraAplicacaoErroNaoCriouJogo();
}
