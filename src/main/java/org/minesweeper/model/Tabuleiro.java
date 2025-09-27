package org.minesweeper.model;

import org.minesweeper.service.AcaoTabuleiro;

import java.util.ArrayList;
import java.util.Random;

public class Tabuleiro implements TabuleiroInterface {
    private ArrayList<ArrayList<Quadrado>> tabuleiro;
    private int linha_size;
    private int coluna_size;
    private static Tabuleiro instance;

    private Tabuleiro(){
        this.tabuleiro = new ArrayList<>();
        this.linha_size = 0;
        this.coluna_size = 0;
    }

    @Override
    public <R> R accept(AcaoTabuleiro acaoTabuleiro) {
        return acaoTabuleiro.visitTabuleiro();
    }

    /**
     * Cria um novo tabuleiro para o jogo.
     *
     * @param linha A quantidade de linhas do tabuleiro.
     * @param coluna A quantidade de colunas do tabuleiro.
     * @param bombas A quantidade de bombas do tabuleiro.
     */
    public void criaTabuleiro(int linha, int coluna, int bombas){
        int numQuadrados = linha * coluna;
        Random r = new Random();
        ArrayList<Quadrado> quadrados = new ArrayList<>();

        // Cria quadrados sem bomba
        for (int i = 0; i < numQuadrados - bombas; ++i){
            quadrados.add(new Quadrado(false, false, false));
        }

        // Cria quadrados com bomba
        for (int i = 0; i < bombas; ++i){
            quadrados.add(new Quadrado(true, false, false));
        }

        // Conta quantos quadrados foram adicionados
        int cont = 0;

        // Adiciona os quadrados de forma aletória no tabuleiro
        for (int i = 0; i < linha; ++i){
            tabuleiro.add(new ArrayList<>());

            for (int j = 0; j < coluna; ++j){
                // Reduz o limite de geração para ficar de acordo com o novo
                // tamanho do array após a remoção do elemento sorteado
                int num = r.nextInt(numQuadrados - cont++);
                Quadrado atual = quadrados.remove(num);

                atual.setLocalizacao(new Localizacao(i, j));
                tabuleiro.get(i).add(atual);
            }
        }

        this.linha_size = linha;
        this.coluna_size = coluna;
    }

    /**
     * Função responsável por abrir um quadrado do tabuleiro.
     *
     * @param localizacao Contém a localização do quadrado a ser aberto.
     * @return
     * <ul>
     * <li>-1: O quadrado já está aberto.</li>
     * <li>-2: O quadrado está marcado e não foi aberto.</li>
     * <li>-3: O quadrado foi aberto e contém uma bomba em seu interior.</li>
     * <li>Qualquer valor não negativo: O quadrado foi aberto e o valor retornado corresponde
     * à quantidade de bombas em seus quadrados vizinhos.</li>
     * </ul>
     */
    public int abrirQuadrado(Localizacao localizacao) {
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();

        if (tabuleiro.get(linha).get(coluna).isAberto()) return -1;
        if (tabuleiro.get(linha).get(coluna).isMarcado()) return -2;

        tabuleiro.get(linha).get(coluna).setAberto(true);

        if (tabuleiro.get(linha).get(coluna).isBomba()) return -3;
        return quantVizinhosPerigosos(localizacao);
    }

    /**
     * Função responsável por contar quantos vizinhos de um quadrado
     * contém bombas.
     *
     * @param localizacao O quadrado que terá seus vizinhos consultados.
     * @return Um valor entre 0 e 8 que diz a quantidade de vizinhos ao
     * redor do quadrado inicial que contém bombas.
     */
    private int quantVizinhosPerigosos(Localizacao localizacao){
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();

        int cont = 0;

        if (quadradoExiste(linha-1, coluna))
            if (tabuleiro.get(linha-1).get(coluna).isBomba())
                cont++;

        if (quadradoExiste(linha-1, coluna-1))
            if (tabuleiro.get(linha-1).get(coluna-1).isBomba())
                cont++;

        if (quadradoExiste(linha-1, coluna+1))
            if (tabuleiro.get(linha-1).get(coluna+1).isBomba())
                cont++;

        if (quadradoExiste(linha+1, coluna))
            if (tabuleiro.get(linha+1).get(coluna).isBomba())
                cont++;

        if (quadradoExiste(linha+1, coluna-1))
            if (tabuleiro.get(linha+1).get(coluna-1).isBomba())
                cont++;

        if (quadradoExiste(linha+1, coluna+1))
            if (tabuleiro.get(linha+1).get(coluna+1).isBomba())
                cont++;

        if (quadradoExiste(linha, coluna-1))
            if (tabuleiro.get(linha).get(coluna-1).isBomba())
                cont++;

        if (quadradoExiste(linha, coluna+1))
            if (tabuleiro.get(linha).get(coluna+1).isBomba())
                cont++;

        return cont;
    }

    private boolean quadradoExiste(int linha, int coluna){
        if (coluna < 0 || coluna > coluna_size) return false;
        if (linha < 0 || linha > linha_size) return false;

        return true;
    }

    /**
     * Função responsável por marcar um quadrado.
     *
     * @param localizacao Contém a localização do quadrado a ser marcado.
     * @return
     * <ul>
     *     <li>-1: O quadrado já está aberto e não foi marcado.</li>
     *     <li>0: O quadrado não estava marcado e foi marcado.</li>
     *     <li>1: O quadrado estava marcado e foi desmarcado.</li>
     * </ul>
     */
    public int marcaQuadrado(Localizacao localizacao){
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();

        if (tabuleiro.get(linha).get(coluna).isAberto()) return -1;

        if (tabuleiro.get(linha).get(coluna).isMarcado()) {
            tabuleiro.get(linha).get(coluna).setMarcado(false);
            return 1;
        } else {
            tabuleiro.get(linha).get(coluna).setMarcado(true);
            return 0;
        }
    }


    public boolean isBomba(Localizacao localizacao){
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();
        return tabuleiro.get(linha).get(coluna).isBomba();
    }

    public boolean isAberto(Localizacao localizacao){
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();
        return tabuleiro.get(linha).get(coluna).isAberto();
    }

    public boolean isMarcado(Localizacao localizacao){
        int linha = localizacao.getLinha();
        int coluna = localizacao.getColuna();
        return tabuleiro.get(linha).get(coluna).isMarcado();
    }

    public int getLinha_size() {
        return linha_size;
    }

    public int getColuna_size() {
        return coluna_size;
    }

    public static Tabuleiro getInstance(){
        if (instance == null)
            instance = new Tabuleiro();

        return instance;
    }
}
