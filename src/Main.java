import src.GeradorBoletim;
import java.io.IOException;

public class Main {
public static void main(String[] args) {
GeradorBoletim gerador = new GeradorBoletim("dados/resultados","dados/boletins");

    try {
        gerador.gerarBoletins();
        System.out.println("Boletins gerados com sucesso!");
    } catch (IOException e) {
        System.out.println("Erro: " + e.getMessage());
    }
}

}
