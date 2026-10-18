package kyj.poo.view;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TelaAlunos {

private final File arquivo = new File("dados/alunos.txt");
private final ListView<String> lista = new ListView<>();
private final TextField campoNome = new TextField();

public void mostrar(Stage stage){

    Label titulo = new Label("Alunos");
    titulo.getStyleClass().add("titulo-pagina");

    Label subtitulo = new Label("Consultar e cadastrar alunos.");
    subtitulo.getStyleClass().add("subtitulo-pagina");

    Label rotulo = new Label("Nome completo do aluno:");
    rotulo.getStyleClass().add("aux-secao");
    campoNome.setPromptText("Insira o nome do aluno");

    Label tituloLista = new Label("Alunos cadastrados");
    tituloLista.getStyleClass().add("titulo-secao");

    lista.setPlaceholder(new Label("Nenhum aluno cadastrado."));

    campoNome.setOnAction(e -> cadastrarAlunos());

    VBox conteudo = new VBox(14, titulo, subtitulo, rotulo, campoNome, tituloLista, lista);
    conteudo.getStyleClass().add("tela");
    conteudo.setPadding(new Insets(30));

    Scene scene = new Scene(conteudo, 650, 600);

    var css = getClass().getResource("/estilo.css");

    if(css != null){
        scene.getStylesheets().add(css.toExternalForm());
    }

    carregarAlunos();

    stage.setTitle("KYJ");
    stage.setScene(scene);
    stage.show();

}


private void cadastrarAlunos(){

    String nome = campoNome.getText().trim();

    if(nome.isEmpty()){
        exibirMensagem(AlertType.WARNING, "Insira o nome do aluno.");
        return;
    }

    for(String nomeCadastrado : lista.getItems()){
        if(nome.equalsIgnoreCase(nomeCadastrado)){
            exibirMensagem(AlertType.WARNING, "Aluno já cadastrado.");
            return;
        }
    }

    List<String> nomes = new ArrayList<>(lista.getItems());
    nomes.add(nome);
    Collections.sort(nomes);

    try{

        File path = new File("dados");
        path.mkdirs();

        BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo));

        for(String aluno: nomes){
            bw.write(aluno);
            bw.newLine();
        }

        bw.close();

        lista.getItems().clear();
        lista.getItems().addAll(nomes);
        campoNome.clear();

        exibirMensagem(AlertType.CONFIRMATION, "Aluno cadastrado com sucesso.");

    }catch(IOException e){

        exibirMensagem(AlertType.ERROR, "Falha ao cadastrar aluno.");
    }

}

private void carregarAlunos(){

    try{

        File path = new File("dados");
        path.mkdirs();

        if(!arquivo.exists()) return;

        BufferedReader br = new BufferedReader(new FileReader(arquivo));
        String linha;

        while((linha = br.readLine()) != null){
            lista.getItems().add(linha);
        }

        br.close();

    }catch(IOException e){

        exibirMensagem(AlertType.ERROR, "Falha ao carregar alunos");
    }
}

private void exibirMensagem(AlertType tipo, String mensagem) {

    Alert alerta = new Alert(AlertType.ERROR);
    alerta.setTitle("Gerenciador de Boletins");
    alerta.setHeaderText(null);
    alerta.setContentText(mensagem);
    alerta.show();
}

}
