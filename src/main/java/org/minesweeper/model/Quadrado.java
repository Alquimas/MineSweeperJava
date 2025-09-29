package org.minesweeper.model;

public class Quadrado {
    private boolean bomba;
    private boolean marcado;
    private boolean aberto;
    private Localizacao localizacao;

    public Quadrado(boolean bomba, boolean marcado, boolean aberto) {
        this.bomba = bomba;
        this.marcado = marcado;
        this.aberto = aberto;
        localizacao = null;
    }

    public Quadrado(Localizacao localizacao) {
        this.localizacao = localizacao;
        this.bomba = false;
        this.marcado = false;
        this.aberto = false;
    }

    public boolean isBomba() {
        return bomba;
    }

    public void setBomba(boolean bomba) {
        this.bomba = bomba;
    }

    public boolean isMarcado() {
        return marcado;
    }

    public void setMarcado(boolean marcado) {
        this.marcado = marcado;
    }

    public boolean isAberto() {
        return aberto;
    }

    public void setAberto(boolean aberto) {
        this.aberto = aberto;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }
}
