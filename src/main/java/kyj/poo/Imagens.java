package kyj.poo;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;

public class Imagens {

     private static final Map<String, Image> cache = new HashMap<>();

    private Imagens() {
    }

    public static Image carregar(String nome) {
        if (cache.containsKey(nome)) return cache.get(nome);

        Image imagem = null;
        InputStream arquivo = Imagens.class.getResourceAsStream("/images/" + nome);
        if (arquivo != null) {
            imagem = new Image(arquivo);
        } else {
            System.err.println("Imagem não encontrada: /images/" + nome);
        }

        cache.put(nome, imagem);
        return imagem;
    }
    
}
