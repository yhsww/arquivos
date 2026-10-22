package kyj.poo.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TelaResultados {

    private static final String SUFIXO_ARQUIVO = "_notas.txt";
    private static final String TEXTO_MEDIA_VAZIA = "Média: ";

    private final File pathResultados = new File("dados/resultados");
    private final ComboBox<String> disciplinas = new ComboBox<>();
    private final TableView<Resultado> tabelaDisciplina = new TableView<>();
    private final Label media = new Label(TEXTO_MEDIA_VAZIA);

    public void mostrar(Stage stage) {

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
        // Requer JavaFX 20+. Em versões anteriores, use TableView.CONSTRAINED_RESIZE_POLICY
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

        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        stage.setTitle("KYJ");
        stage.setScene(scene);
        stage.show();
    }

    private void limparResultados() {
        tabelaDisciplina.getItems().clear();
        media.setText(TEXTO_MEDIA_VAZIA);
    }

    private void carregarResultadoSelecionado() {

        String nomeDisciplina = disciplinas.getValue();

        if (nomeDisciplina == null) {
            limparResultados();
            return;
        }

        File arquivo = new File(pathResultados, nomeDisciplina + SUFIXO_ARQUIVO);

        if (!arquivo.isFile()) {
            limparResultados();
            exibirMensagem(AlertType.WARNING, "Arquivo de resultados não encontrado.");
            return;
        }

        List<Resultado> resultados = new ArrayList<>();
        String textoMedia = "Média: não disponível";
        int linhasInvalidas = 0;

        try (BufferedReader br = Files.newBufferedReader(arquivo.toPath(), StandardCharsets.UTF_8)) {

            String linha;

            while ((linha = br.readLine()) != null) {

                linha = linha.trim();

                if (linha.isEmpty()) {
                    continue;
                }

                if (linha.startsWith("Média:")) {
                    textoMedia = linha;
                    continue;
                }

                String[] partes = linha.split("\\t");

                if (partes.length != 2) {
                    linhasInvalidas++;
                    continue;
                }

                String nomeAluno = partes[0].trim();

                try {
                    int qtdAcertos = Integer.parseInt(partes[1].trim());
                    resultados.add(new Resultado(nomeAluno, qtdAcertos));
                } catch (NumberFormatException e) {
                    linhasInvalidas++;
                }
            }

            tabelaDisciplina.getItems().setAll(resultados);
            media.setText(textoMedia);

            if (linhasInvalidas > 0) {
                exibirMensagem(AlertType.WARNING,
                        linhasInvalidas + " linha(s) inválida(s) foram ignoradas no arquivo.");
            }

        } catch (IOException e) {
            limparResultados();
            exibirMensagem(AlertType.ERROR, "Falha ao carregar os resultados da disciplina.");
        }
    }

    private void carregarDisciplinas() {

        disciplinas.getItems().clear();

        if (!pathResultados.exists() || !pathResultados.isDirectory()) {
            return;
        }

        File[] arquivos = pathResultados.listFiles();

        if (arquivos == null) {
            exibirMensagem(AlertType.ERROR, "Não foi possível acessar os arquivos de resultados.");
            return;
        }

        for (File arquivo : arquivos) {

            String nomeArquivo = arquivo.getName();

            if (arquivo.isFile() && nomeArquivo.endsWith(SUFIXO_ARQUIVO)) {
                String nomeDisciplina = nomeArquivo.substring(0, nomeArquivo.length() - SUFIXO_ARQUIVO.length());
                disciplinas.getItems().add(nomeDisciplina);
            }
        }

        disciplinas.getItems().sort(String.CASE_INSENSITIVE_ORDER);
    }

    private void exibirMensagem(AlertType tipo, String mensagem) {

        Alert alerta = new Alert(tipo);
        alerta.setTitle("Gerenciador de Boletins");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.show();
    }

    public static class Resultado {

        private final String nomeAluno;
        private final int qtdAcertos;

        public Resultado(String nomeAluno, int qtdAcertos) {
            this.nomeAluno = nomeAluno;
            this.qtdAcertos = qtdAcertos;
        }

        public int getQtdAcertos() {
            return this.qtdAcertos;
        }

        public String getNomeAluno() {
            return this.nomeAluno;
        }
    }
}