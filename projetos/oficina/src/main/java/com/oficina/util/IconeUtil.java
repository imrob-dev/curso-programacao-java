package com.oficina.util;

import java.awt.Image;
import java.awt.Window;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

/**
 * Utilitário para carregamento e aplicação do logotipo e ícones da aplicação.
 */
public final class IconeUtil {

    private static final String PACOTE_IMG = "/com/oficina/img/";
    private static List<Image> icones = null;
    private static Image logo = null;

    private IconeUtil() {
    }

    /**
     * Retorna a lista de ícones em várias resoluções (16, 32, 48, 64, 128, 256)
     * para uso na barra de tarefas e título das janelas.
     */
    public static synchronized List<Image> getIcones() {
        if (icones == null) {
            icones = new ArrayList<>();
            int[] tamanhos = {16, 32, 48, 64, 128, 256};
            for (int tam : tamanhos) {
                Image img = carregarImagem(PACOTE_IMG + "icone_oficina_" + tam + ".png");
                if (img != null) {
                    icones.add(img);
                }
            }
            Image imgPadrao = carregarImagem(PACOTE_IMG + "icone_oficina.png");
            if (imgPadrao != null) {
                icones.add(imgPadrao);
            }
        }
        return icones;
    }

    /**
     * Retorna o ícone em tamanho 48x48.
     */
    public static Image getIcone48() {
        return carregarImagem(PACOTE_IMG + "icone_oficina_48.png");
    }

    /**
     * Retorna o ícone principal da aplicação.
     */
    public static Image getIconePrincipal() {
        Image img = carregarImagem(PACOTE_IMG + "icone_oficina_64.png");
        if (img == null) {
            img = carregarImagem(PACOTE_IMG + "icone_oficina.png");
        }
        return img;
    }

    /**
     * Retorna a imagem original do logotipo da empresa.
     */
    public static synchronized Image getLogo() {
        if (logo == null) {
            logo = carregarImagem(PACOTE_IMG + "logo_oficina.png");
        }
        return logo;
    }

    /**
     * Aplica o ícone da aplicação a uma janela (JFrame ou JDialog).
     */
    public static void aplicarIcone(Window window) {
        if (window == null) {
            return;
        }
        List<Image> lista = getIcones();
        if (!lista.isEmpty()) {
            window.setIconImages(lista);
        } else {
            Image img = getIconePrincipal();
            if (img != null) {
                window.setIconImage(img);
            }
        }
    }

    private static Image carregarImagem(String caminhoRecurso) {
        try {
            URL url = IconeUtil.class.getResource(caminhoRecurso);
            if (url != null) {
                return new ImageIcon(url).getImage();
            }
            // Fallback para arquivo local no disco caso executado fora do jar/classpath padrão
            File arquivo = new File("src/main/resources" + caminhoRecurso);
            if (arquivo.exists()) {
                return new ImageIcon(arquivo.getAbsolutePath()).getImage();
            }
            arquivo = new File("src/main/java" + caminhoRecurso);
            if (arquivo.exists()) {
                return new ImageIcon(arquivo.getAbsolutePath()).getImage();
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
