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
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TelaDisciplinas {

    private final File arquivo = new File("dados/disciplinas.txt");
    private final ListView<String> lista = new ListView<>();
    private final TextField campoNome = new TextField();

    public void mostrar(Stage stage){

    Label titulo = new Label("Disciplinas");
    titulo.getStyleClass().add("titulo-pagina");

    Label subtitulo = new Label("Consultar e cadastrar disciplinas.");
    subtitulo.getStyleClass().add("subtitulo-pagina");

    Label rotulo = new Label("Nome da disciplina: ");
    rotulo.getStyleClass().add("aux-secao");
    campoNome.setPromptText("Insira o nome da disciplina");

    Label tituloLista = new Label("Disciplinas cadastradas");
    tituloLista.getStyleClass().add("titulo-secao");

    lista.setPlaceholder(new Label("Nenhum aluno cadastrado."));

    campoNome.setOnAction(e -> cadastrarDisciplinas());

    VBox conteudo = new VBox(14, titulo, subtitulo, rotulo, campoNome, tituloLista, lista);
    conteudo.getStyleClass().add("tela");
    conteudo.setPadding(new Insets(30));

    Scene scene = new Scene(conteudo, 650, 600);

    var css = getClass().getResource("/estilo.css");

    if(css != null){
        scene.getStylesheets().add(css.toExternalForm());
    }

    carregarDisciplinas();

    stage.setTitle("KYJ");
    stage.setScene(scene);
    stage.show();

    }

    public void cadastrarDisciplinas(){

        String nome = campoNome.getText().trim();

        if(nome.isEmpty()){
            exibirMensagem(AlertType.WARNING, "Insira o nome da disciplina");
            return;
        }

        for(String disciplina : lista.getItems()){

            if(disciplina.equalsIgnoreCase(nome)){
                exibirMensagem(AlertType.WARNING, "Disciplina já cadastrada.");
                return;
            }
        }

        List<String> nomes = new ArrayList<>(lista.getItems());
        nomes.add(nome);
        Collections.sort(nomes, String.CASE_INSENSITIVE_ORDER);

        try{

            File path = new File("dados");
            path.mkdirs();

            BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo));

            for(String disciplina : nomes){
                bw.write(disciplina);
                bw.newLine();
            }

            bw.close();

            lista.getItems().clear();
            lista.getItems().addAll(nomes);
            campoNome.clear();

            exibirMensagem(AlertType.CONFIRMATION, "Disciplina cadastrada com sucesso.");

        }catch(IOException e){

            exibirMensagem(AlertType.ERROR, "Falha ao cadastrar disciplina.");
        }
       
    }

    public void carregarDisciplinas(){
        
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

            exibirMensagem(AlertType.ERROR, "Falha ao carregar disciplinas");
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
