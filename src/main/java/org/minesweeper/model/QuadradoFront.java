package org.minesweeper.model;

public class QuadradoFront {
    private boolean aberto;
    private int numero;
    private boolean marcado;
    private Localizacao localizacao;
    private boolean bomba;

    public QuadradoFront(Localizacao localizacao){
        this.aberto = false;
        this.numero = -1;
        this.marcado = false;
        this.localizacao = localizacao;
        this.bomba = false;
    }

    public QuadradoFront(boolean aberto, int numero, boolean marcado, Localizacao localizacao, boolean bomba) {
        this.aberto = aberto;
        this.numero = numero;
        this.marcado = marcado;
        this.localizacao = localizacao;
        this.bomba = bomba;
    }

    public boolean isAberto() {
        return aberto;
    }

    public int getNumero() {
        return numero;
    }

    public boolean isMarcado() {
        return marcado;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public boolean isBomba() {
        return bomba;
    }
}
