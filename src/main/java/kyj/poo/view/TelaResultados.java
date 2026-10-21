package kyj.poo.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TelaResultados {

    private final File pathResultados = new File("dados/resultados");
    private final ComboBox<String> disciplinas = new ComboBox<>();
    private final TableView<Resultado> tabelaDisciplina = new TableView<>();
    private final Label media = new Label("Média: ");

    public void mostrar(Stage stage){

        Label titulo = new Label("Resultados");
        Label subtitulo = new Label("Consulte os boletins de cada disciplina");

        titulo.getStyleClass().add("titulo-pagina");
        subtitulo.getStyleClass().add("subtitulo-pagina");

        Label tituloDisciplina = new Label("Disciplina");
        tituloDisciplina.getStyleClass().add("aux-secao");

        disciplinas.setPromptText("Selecione uma disciplina");
        disciplinas.setMaxWidth(Double.MAX_VALUE);

        TableColumn<Resultado, String> colunaAlunos = new TableColumn<>("Alunos");
        colunaAlunos.setCellValueFactory(dados -> new SimpleStringProperty(dados.getValue().getNomeAluno()));

        TableColumn<Resultado, Number> colunaAcertos = new TableColumn<>("Acertos");
        colunaAcertos.setCellValueFactory(dados -> new SimpleIntegerProperty(dados.getValue().getQtdAcertos()));

        tabelaDisciplina.getColumns().addAll(colunaAlunos, colunaAcertos);
        tabelaDisciplina.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tabelaDisciplina.setPlaceholder(new Label("Selecione uma disciplina para consultar"));

        VBox conteudo = new VBox(14, titulo, subtitulo, tituloDisciplina, disciplinas, tabelaDisciplina, media);
        conteudo.setPadding(new Insets(30));
        conteudo.getStyleClass().add("tela");

        VBox.setVgrow(tabelaDisciplina, Priority.ALWAYS);
        
        disciplinas.setOnAction(e -> carregarResultadoSelecionado());
        carregarDisciplinas();

        Scene scene = new Scene(conteudo, 750, 600);

        var css = getClass().getResource("/estilo.css");

        if(css != null){
            scene.getStylesheets().add(css.toExternalForm());
        }
        
        stage.setTitle("KYJ");
        stage.setScene(scene);
        stage.show();

    }

    private void carregarResultadoSelecionado(){

        String nomeDisciplina = disciplinas.getValue();

        if (nomeDisciplina == null){
            tabelaDisciplina.getItems().clear();
            media.setText("Média: ");
            return;
        }

        File arquivo = new File(pathResultados, nomeDisciplina + "_notas.txt");

        if (!arquivo.isFile()) {
            exibirMensagem(AlertType.WARNING,
                "Arquivo de resultados não encontrado.");
            return;
        }

        List<Resultado> resultados = new ArrayList<>();
        String textoMedia = "Média: não disponível";

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))){

            String linha;

            while ((linha = br.readLine()) != null) {

                linha = linha.trim();

                if(linha.isEmpty()) {
                    continue;
                }

                if (linha.startsWith("Média:")) {
                    textoMedia = linha;
                    continue;
                }

                String[] partes = linha.split("\\t");

                if (partes.length != 2) {
                    continue;
                }

                String nomeAluno = partes[0].trim();

                try {
                        int qtdAcertos = Integer.parseInt(
                        partes[1].trim()
                    );

                    resultados.add(new Resultado(nomeAluno, qtdAcertos));

                } catch (NumberFormatException e) {
                    exibirMensagem(
                        AlertType.WARNING,
                        "Uma nota inválida foi encontrada no arquivo."
                    );
                }
            }

            tabelaDisciplina.getItems().setAll(resultados);
            media.setText(textoMedia);

        } catch (IOException e) {

            exibirMensagem(AlertType.ERROR,"Falha ao carregar os resultados da disciplina.");
        }

    }

    private void carregarDisciplinas(){

        disciplinas.getItems().clear();

        if (!pathResultados.exists() || !pathResultados.isDirectory()) {
            return;
        }

        File[] arquivos = pathResultados.listFiles();

        if (arquivos == null) {
            exibirMensagem(AlertType.ERROR,"Não foi possível acessar os arquivos de resultados.");
            return;
        }

        for (File arquivo : arquivos) {

            String nomeArquivo = arquivo.getName();

            if (arquivo.isFile() && nomeArquivo.endsWith("_notas.txt")){

                String nomeDisciplina = nomeArquivo.substring(0, nomeArquivo.length() - "_notas.txt".length());
                disciplinas.getItems().add(nomeDisciplina);
            }
        }

        disciplinas.getItems().sort(String.CASE_INSENSITIVE_ORDER);
   
    }
    

    private void exibirMensagem(AlertType tipo, String mensagem) {

        Alert alerta = new Alert(AlertType.ERROR);
        alerta.setTitle("Gerenciador de Boletins");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.show();
    }


    public static class Resultado{

        private String nomeAluno;
        private int qtdAcertos;

        public Resultado(String nomeAluno, int qtdAcertos){
            this.nomeAluno = nomeAluno;
            this.qtdAcertos = qtdAcertos;
        }

        public int getQtdAcertos(){
            return this.qtdAcertos;
        }

        public String getNomeAluno(){
            return this.nomeAluno;
        }

    }
}


