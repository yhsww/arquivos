package kyj.poo.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import kyj.poo.GeradorBoletim;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class TelaBoletins{

    private final File pathBoletins = new File("dados/boletins");
    private final ListView<String> listaAlunos = new ListView<>();
    private final TextArea boletim = new TextArea();

    public void mostrar(Stage stage){

        //cabecalho
        Label titulo = new Label("Boletins");
        titulo.getStyleClass().add("titulo-pagina");

        Label subtitulo = new Label("Acompanhe o desempenho dos alunos.");
        subtitulo.getStyleClass().add("subtitulo-pagina");

        VBox cabecalho = new VBox(titulo, subtitulo);
        VBox.setVgrow(titulo, Priority.ALWAYS);

        //lista de alunos
        Label tituloLista = new Label("Alunos");
        tituloLista.getStyleClass().add("titulo-secao");

        listaAlunos.setPlaceholder(new Label("Nenhum boletim disponível."));

        //boletim

        Label tituloBoletim = new Label("Visualizar boletim");
        tituloBoletim.getStyleClass().add("titulo-secao");

        boletim.setEditable(false);
        boletim.setWrapText(true);
        boletim.setPromptText("Selecione um aluno");

        listaAlunos.getSelectionModel()
                    .selectedItemProperty()
                    .addListener((prop, antes, aluno) -> {
                            if(aluno != null){
                                visualizarBoletim(aluno);
                            }
                    });


        //botoes

        Button btnGerar = new Button("Gerar boletins");
        Button btnCarregar = new Button("Atualizar lista");

        btnGerar.getStyleClass().add("botao-secundario");
        btnCarregar.getStyleClass().add("botao-secundario");

        btnGerar.setOnAction(e -> gerarBoletins());
        btnCarregar.setOnAction(e -> carregarBoletins());

        HBox botoes = new HBox(10, btnGerar, btnCarregar);
        botoes.setAlignment(Pos.CENTER_LEFT);

        //esquerdo: lista 

        VBox painelEsquerdo = new VBox(10, tituloLista, listaAlunos);

        VBox.setVgrow(listaAlunos, Priority.ALWAYS);
        painelEsquerdo.setPrefWidth(250);
        painelEsquerdo.setMinWidth(180);
        
        //direito: boletim
        
        VBox painelDireito = new VBox(10, tituloBoletim, boletim);
        
        VBox.setVgrow(boletim, Priority.ALWAYS);
        HBox.setHgrow(painelDireito, Priority.ALWAYS);

        //direita + esquerda
                    
        HBox conteudo = new HBox(20, painelEsquerdo, painelDireito);
        conteudo.setPrefHeight(350);

        HBox.setHgrow(
            painelEsquerdo,
            Priority.ALWAYS
        );

        VBox layout = new VBox(20, cabecalho, conteudo, botoes);
        layout.setPadding(new Insets(30));
        layout.getStyleClass().add("tela");

        BorderPane root = new BorderPane();
        root.setCenter(layout);

        Scene scene = new Scene(root, 1000, 650);

        var css = getClass().getResource("/estilo.css");

        if(css != null){
            scene.getStylesheets().add(css.toExternalForm());
        }

        stage.setTitle("KYJ");
        stage.setScene(scene);
        stage.show();
    }

    public void visualizarBoletim(String nomeAluno){

        File[] arquivos = pathBoletins.listFiles();

        if(arquivos == null){
            exibirMensagem(AlertType.ERROR, "Falha ao acessar boletins.");
            return;
        }

        for(File arquivo : arquivos){

            if(!arquivo.isFile() || !arquivo.getName().endsWith(".txt")){
                continue;
            }

            try(BufferedReader br = new BufferedReader(new FileReader(arquivo))){

                boolean encontrado = false;
                String linha;
                StringBuilder conteudo = new StringBuilder();

                while( (linha = br.readLine()) != null){

                    conteudo.append(linha).append(System.lineSeparator());

                    if(linha.equals("Aluno: " + nomeAluno)){
                        encontrado = true;
                    }
                }

                if(encontrado){
                    boletim.setText(conteudo.toString());
                    return;
                }

            }catch(IOException e){
                exibirMensagem(AlertType.ERROR, "Falha ao exibir boletim.");
                return;
            }

        }

        boletim.setText("Boletim não encontrado.");

    }

    public void gerarBoletins(){
        
        try{

            GeradorBoletim geradorBoletim = new GeradorBoletim("dados/resultados", "dados/boletins");

            geradorBoletim.gerarBoletins();
            carregarBoletins();

            exibirMensagem(AlertType.CONFIRMATION, "Boletins gerados com sucesso.");

        }catch(IOException e){

            exibirMensagem(AlertType.ERROR, "Falha ao gerar boletins.");

        }

    }

    public void carregarBoletins(){

        listaAlunos.getItems().clear();
        boletim.clear();

        if(!pathBoletins.exists() || !pathBoletins.isDirectory()){
            return;
        }

        File[] arquivos = pathBoletins.listFiles();

        if(arquivos == null){
            exibirMensagem(AlertType.ERROR, "Falha ao ler boletins.");
            return;
        }

        List<String> nomes = new ArrayList<>();

        for(File arquivo : arquivos){

            if(!arquivo.isFile() || !arquivo.getName().endsWith(".txt")) continue;
            
            try{

                BufferedReader br = new BufferedReader(new FileReader(arquivo));
                String linha;

                while( (linha = br.readLine()) != null){
                    if(linha.startsWith("Aluno: ")){
                        nomes.add(linha.substring(7).trim());
                        break;
                    }
                }

            }catch(IOException e){
                exibirMensagem(AlertType.ERROR, "Falha ao ler " + arquivo.getName());
            }

            Collections.sort(nomes, String.CASE_INSENSITIVE_ORDER);
            listaAlunos.getItems().setAll(nomes);

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