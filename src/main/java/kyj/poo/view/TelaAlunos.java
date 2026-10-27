package kyj.poo.view;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import kyj.poo.GeradorNota;
import kyj.poo.models.Aluno;
import java.util.ArrayList;
import java.util.List;

public class TelaAlunos {

    private final File arquivoAlunos = new File("dados/alunos.txt");

    private final ListView<Aluno> lista = new ListView<>();

    private final TextField campoNome = new TextField();

    public void mostrar(Stage stage) {
        this.campoNome.setPromptText("Nome completo do aluno");
        configurarLista();

        Button btnCadastrar = new Button("Cadastrar aluno");
        Button btnRemover = new Button("Excluir aluno");
        Button btnAtualizar = new Button("Atualizar lista");

        btnCadastrar.setOnAction(e -> cadastrarAluno());
        btnRemover.setOnAction(e -> removerAluno());
        btnAtualizar.setOnAction(e -> carregarAlunos());

        VBox conteudo = new VBox(10);
        conteudo.setPadding(new Insets(15));

        conteudo
            .getChildren()
            .addAll(
                new Label("Alunos"),
                this.campoNome,
                btnCadastrar,
                btnRemover,
                btnAtualizar,
                new Label("Alunos cadastrados"),
                this.lista
            );

        Scene scene = new Scene(conteudo, 450, 550);

        var css = getClass().getResource("/estilo.css");

        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        stage.setTitle("KYJ");
        stage.setScene(scene);

        carregarAlunos();

        stage.show();
    }

    private void configurarLista() {
        this.lista.setCellFactory(listView ->
            new ListCell<Aluno>() {
                protected void updateItem(Aluno aluno, boolean vazio) {
                    super.updateItem(aluno, vazio);

                    if (vazio || aluno == null) {
                        setText(null);
                    } else {
                        setText(aluno.getNome());
                    }
                }
            }
        );
    }

    private void cadastrarAluno() {
        String nome = this.campoNome.getText().trim();

        if (nome.isEmpty()) {
            exibirMensagem(AlertType.WARNING, "Digite o nome do aluno.");
            return;
        }

        for (Aluno aluno : this.lista.getItems()) {
            if (aluno.getNome().equalsIgnoreCase(nome)) {
                exibirMensagem(
                    AlertType.WARNING,
                    "Esse aluno já está cadastrado."
                );
                return;
            }
        }

        Aluno aluno = new Aluno(nome, "");

        lista.getItems().add(aluno);
        ordenarAlunos();

        if (!salvarAlunos()) {
            this.lista.getItems().remove(aluno);
            return;
        }

        this.campoNome.clear();

        exibirMensagem(AlertType.CONFIRMATION, "Aluno cadastrado com sucesso.");
    }

    private void removerAluno() {
        Aluno selecionado = this.lista.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            exibirMensagem(AlertType.WARNING, "Selecione um aluno.");
            return;
        }

        Alert confirmacao = new Alert(
            AlertType.CONFIRMATION,
            "Deseja excluir " + selecionado.getNome() + " do cadastro?"
        );

        confirmacao.setHeaderText("Confirmar exclusão");

        if (
            confirmacao.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK
        ) {
            return;
        }

        this.lista.getItems().remove(selecionado);

        if (!salvarAlunos()) {
            this.lista.getItems().add(selecionado);
            ordenarAlunos();
            return;
        }

        removerDadosDoAluno(selecionado.getNome());

        exibirMensagem(AlertType.CONFIRMATION, "Aluno removido do cadastro.");
    }

    private void carregarAlunos() {
        this.lista.getItems().clear();

        if (!arquivoAlunos.exists()) {
            return;
        }

        try (
            BufferedReader br = new BufferedReader(
                new FileReader(arquivoAlunos)
            )
        ) {
            String linha;

            while ((linha = br.readLine()) != null) {
                String nome = linha.trim();

                if (nome.isEmpty()) {
                    continue;
                }

                boolean existe = false;

                for (Aluno aluno : this.lista.getItems()) {
                    if (aluno.getNome().equalsIgnoreCase(nome)) {
                        existe = true;
                        break;
                    }
                }

                if (!existe) {
                    this.lista.getItems().add(new Aluno(nome, ""));
                }
            }

            ordenarAlunos();
        } catch (IOException e) {
            exibirMensagem(AlertType.ERROR, "Falha ao carregar os alunos.");
        }
    }

    private boolean salvarAlunos() {
        File pasta = this.arquivoAlunos.getParentFile();

        if (pasta != null && !pasta.exists()) {
            pasta.mkdirs();
        }

        try (
            BufferedWriter bw = new BufferedWriter(
                new FileWriter(arquivoAlunos)
            )
        ) {
            for (Aluno aluno : this.lista.getItems()) {
                bw.write(aluno.getNome());
                bw.newLine();
            }

            return true;
        } catch (IOException e) {
            exibirMensagem(AlertType.ERROR, "Falha ao salvar os alunos.");
            return false;
        }
    }

    private void ordenarAlunos() {
        this.lista
            .getItems()
            .sort((a1, a2) -> a1.getNome().compareToIgnoreCase(a2.getNome()));
    }

    private void removerDadosDoAluno(String nome) {
       //tira as respostas do aluno das provas
        File[] provas = new File("dados/provas").listFiles();

        if (provas != null) {
            for (File prova : provas) {
                if (!prova.isFile() || !prova.getName().endsWith(".txt")) continue;

                List<String> mantidas = new ArrayList<>();
                boolean mudou = false;

                try (BufferedReader br = new BufferedReader(new FileReader(prova))) {
                    String linha;

                    while ((linha = br.readLine()) != null) {
                        String[] partes = linha.split("\t", 2);

                        if (partes.length == 2 && partes[1].trim().equalsIgnoreCase(nome)) {
                            mudou = true;
                        } else {
                            mantidas.add(linha);
                        }
                    }
                } catch (IOException e) {
                    continue;
                }

                if (mudou) {
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(prova))) {
                        for (String l : mantidas) {
                            bw.write(l);
                            bw.newLine();
                        }
                    } catch (IOException e) {
                        exibirMensagem(AlertType.ERROR, "Falha ao atualizar " + prova.getName());
                    }
                }
            }
        }

        //apaga o boletim
        File[] boletins = new File("dados/boletins").listFiles();

        if (boletins != null) {
            for (File boletim : boletins) {
                if (!boletim.isFile() || !boletim.getName().endsWith(".txt")) continue;

                boolean doAluno = false;

                try (BufferedReader br = new BufferedReader(new FileReader(boletim))) {
                    String linha;

                    while ((linha = br.readLine()) != null) {
                        if (linha.trim().equalsIgnoreCase("Aluno: " + nome)) {
                            doAluno = true;
                            break;
                        }
                    }
                } catch (IOException e) {
                    continue;
                }

                if (doAluno) boletim.delete();
            }
        }

        //refac os resultados sem o aluno
        try {
            new GeradorNota("dados/resultados")
                    .gerarResultados(new File("dados/provas"), new File("dados/gabaritos"));
        } catch (IOException e) {
            exibirMensagem(AlertType.ERROR, "Falha ao atualizar os resultados.");
        }
    }

    private void exibirMensagem(AlertType tipo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Gerenciador de Boletins");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
