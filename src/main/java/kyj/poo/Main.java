package kyj.poo;

import javafx.application.Application;
import javafx.stage.Stage;
import kyj.poo.view.TelaPrincipal;

public class Main extends Application {

    public void start(Stage stage) {
        TelaPrincipal tela = new TelaPrincipal();
        tela.mostrar(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
