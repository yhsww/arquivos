
package kyj.poo.view;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import kyj.poo.models.Aluno;
import kyj.poo.models.Disciplina;

public class TelaDisciplinas {

    private final File arquivoDisciplinas = new File("dados/disciplinas.txt");
    private final File arquivoAlunos = new File("dados/alunos.txt");
    private final File pastaProvas = new File("dados/provas");
    private final File pastaGabaritos = new File("dados/gabaritos");
    private final ListView<Disciplina> lista = new ListView<>();
    private final ComboBox<Aluno> comboAlunos = new ComboBox<>();
    private final TextField campoNome = new TextField();
    private final TextField[] campoRespostas = new TextField[10];
    private final TextField[] campoGabarito = new TextField[10];

    public void mostrar(Stage stage) {

        campoNome.setPromptText("Nome da disciplina");

        configurarLista();
        configurarComboAlunos();

        GridPane gradeRespostas = criarCampos(campoRespostas);
        GridPane gradeGabarito = criarCampos(campoGabarito);

        Button btnCadastrar = new Button("Cadastrar disciplina");
        Button btnRemover = new Button("Excluir disciplina");
        Button btnAtualizarLista = new Button("Atualizar disciplinas");

        Button btnSalvarRespostas = new Button("Salvar respostas");
        Button btnLimparRespostas = new Button("Limpar respostas");

        Button btnCadastrarGabarito = new Button("Cadastrar gabarito");
        Button btnAtualizarGabarito = new Button("Atualizar gabarito");
        Button btnRemoverGabarito = new Button("Remover gabarito");

        btnCadastrar.setOnAction(e -> cadastrarDisciplina());
        btnRemover.setOnAction(e -> removerDisciplina());
        btnAtualizarLista.setOnAction(e -> carregarDisciplinas());

        btnSalvarRespostas.setOnAction(e -> salvarRespostas());
        btnLimparRespostas.setOnAction(e -> limparCampos(campoRespostas));

        btnCadastrarGabarito.setOnAction(e -> cadastrarGabarito());
        btnAtualizarGabarito.setOnAction(e -> atualizarGabarito());
        btnRemoverGabarito.setOnAction(e -> removerGabarito());

        lista.getSelectionModel().selectedItemProperty()
            .addListener((obs, antiga, selecionada) -> {
                carregarGabarito(selecionada);
                carregarRespostas();
            });

        comboAlunos.setOnAction(e -> carregarRespostas());

        VBox conteudo = new VBox(10);
        conteudo.setPadding(new Insets(15));

        conteudo.getChildren().addAll(
            new Label("Cadastro de disciplinas"),
            this.campoNome,
            btnCadastrar,
            btnRemover,
            btnAtualizarLista,

            new Label("Disciplinas cadastradas"),
            this.lista,

            new Label("Respostas da prova"),
            new Label("Selecione a disciplina e o aluno:"),
            this.comboAlunos,
            gradeRespostas,
            btnSalvarRespostas,
            btnLimparRespostas,

            new Label("Gabarito"),
            gradeGabarito,
            btnCadastrarGabarito,
            btnAtualizarGabarito,
            btnRemoverGabarito
        );

        Scene scene = new Scene(conteudo, 550, 900);

        var css = getClass().getResource("/estilo.css");

        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        stage.setTitle("KYJ");
        stage.setScene(scene);

        carregarDisciplinas();
        carregarAlunos();

        stage.show();
    }

    private void configurarLista() {

        this.lista.setCellFactory(lv -> new ListCell<Disciplina>() {

            protected void updateItem(Disciplina disciplina, boolean vazio) {
                super.updateItem(disciplina, vazio);

                if (vazio || disciplina == null) {
                    setText(null);
                } else {
                    setText(disciplina.getNome());
                }
            }
        });
    }

    private void configurarComboAlunos() {

        comboAlunos.setCellFactory(listView -> new ListCell<Aluno>() {

            protected void updateItem(Aluno aluno, boolean vazio) {
                super.updateItem(aluno, vazio);

                setText(vazio || aluno == null ? null : aluno.getNome());
            }
        });

        comboAlunos.setButtonCell(new ListCell<Aluno>() {

            protected void updateItem(Aluno aluno, boolean vazio) {
                super.updateItem(aluno, vazio);

                setText(vazio || aluno == null ? null : aluno.getNome());
            }
        });

        this.comboAlunos.setMaxWidth(Double.MAX_VALUE);
    }

    private void cadastrarDisciplina() {

        String nome = this.campoNome.getText().trim();

        if(nome.isEmpty()){
            exibirMensagem(AlertType.WARNING,"Digite o nome da disciplina.");
            return;
        }

        for (Disciplina d : lista.getItems()) {
            if(d.getNome().equalsIgnoreCase(nome)){
                exibirMensagem(AlertType.WARNING,"Essa disciplina já está cadastrada.");
                return;
            }
        }

        Disciplina disciplina = new Disciplina(nome);

        this.lista.getItems().add(disciplina);
        ordenarDisciplinas();

        if(!salvarDisciplinas()){
            lista.getItems().remove(disciplina);
            return;
        }

        this.campoNome.clear();

        exibirMensagem(AlertType.CONFIRMATION,"Disciplina cadastrada com sucesso.");
    }

    private void removerDisciplina() {

        Disciplina disciplina = disciplinaSelecionada();

        if(disciplina == null){
            return;
        }

        Alert confirmacao = new Alert(AlertType.CONFIRMATION, "Excluir a disciplina " + disciplina.getNome() + "?");

        confirmacao.setHeaderText("Confirmar exclusão");

        if(confirmacao.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK){
            return;
        }

        this.lista.getItems().remove(disciplina);

        if (!salvarDisciplinas()){
            this.lista.getItems().add(disciplina);
            ordenarDisciplinas();
            return;
        }

        limparCampos(campoGabarito);
        limparCampos(campoRespostas);

        exibirMensagem(AlertType.CONFIRMATION,"Disciplina removida do cadastro.");
    }

    private void carregarDisciplinas() {

        this.lista.getItems().clear();

        if(!arquivoDisciplinas.exists()){
            return;
        }

        try(BufferedReader br = new BufferedReader(new FileReader(arquivoDisciplinas))){

            String linha;

            while ((linha = br.readLine()) != null) {

                String nome = linha.trim();

                if(nome.isEmpty()){
                    continue;
                }

                boolean existe = false;

                for(Disciplina d : lista.getItems()){
                    if(d.getNome().equalsIgnoreCase(nome)){
                        existe = true;
                        break;
                    }
                }

                if(!existe){
                    this.lista.getItems().add(new Disciplina(nome));
                }
            }

            ordenarDisciplinas();

        }catch (IOException e){
            exibirMensagem(AlertType.ERROR,"Falha ao carregar as disciplinas.");
        }
    }

    private boolean salvarDisciplinas(){

        File pasta = arquivoDisciplinas.getParentFile();

        if(pasta != null && !pasta.exists()){
            pasta.mkdirs();
        }

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoDisciplinas))){

            for(Disciplina d : lista.getItems()){
                bw.write(d.getNome());
                bw.newLine();
            }

            return true;

        }catch(IOException e){

            exibirMensagem(AlertType.ERROR,"Falha ao salvar as disciplinas.");
            return false;
        }
    }

    private void ordenarDisciplinas(){

        this.lista.getItems().sort((d1, d2) -> d1.getNome().compareToIgnoreCase(d2.getNome()));
    }

    private void carregarAlunos() {

        this.comboAlunos.getItems().clear();

        if (!arquivoAlunos.exists()) {
            exibirMensagem(AlertType.WARNING,"Cadastre alunos primeiro.");
            return;
        }

        try(BufferedReader br = new BufferedReader(new FileReader(arquivoAlunos))){

            String linha;

            while ((linha = br.readLine()) != null) {

                String nome = linha.trim();

                if(nome.isEmpty()){
                    continue;
                }

                boolean existe = false;

                for (Aluno aluno : comboAlunos.getItems()) {
                    if(aluno.getNome().equalsIgnoreCase(nome)){
                        existe = true;
                        break;
                    }
                }

                if(!existe){
                    this.comboAlunos.getItems().add(new Aluno(nome, ""));
                }
            }

            this.comboAlunos.getItems().sort((a1, a2) -> a1.getNome().compareToIgnoreCase(a2.getNome()));

        }catch (IOException e){
            exibirMensagem(AlertType.ERROR,"Falha ao carregar os alunos.");
        }
    }

    private GridPane criarCampos(TextField[] campos) {

        GridPane grade = new GridPane();
        grade.setHgap(8);
        grade.setVgap(8);

        for(int i = 0; i < 10; i++){

            Label numero = new Label((i + 1) + ".");
            TextField campo = new TextField();

            campo.setPromptText("V/F");
            campo.setPrefWidth(55);
            campo.setMaxWidth(55);

            campos[i] = campo;

            int coluna = i % 5;
            int linha = i / 5;

            grade.add(numero, coluna*2, linha);
            grade.add(campo, coluna*2+1, linha);
        }

        return grade;
    }

    private File arquivoProva(Disciplina disciplina) {

        return new File(this.pastaProvas, nomeValido(disciplina.getNome()) + ".txt");
    }

    private File arquivoGabarito(Disciplina disciplina) {

        return new File(this.pastaGabaritos, nomeValido(disciplina.getNome()) + ".txt");
    }

    private String nomeValido(String nome) {
        return nome.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private Disciplina disciplinaSelecionada() {

        Disciplina disciplina = this.lista.getSelectionModel().getSelectedItem();

        if(disciplina == null){
            exibirMensagem(AlertType.WARNING,"Selecione uma disciplina primeiro.");
        }

        return disciplina;
    }

    private Aluno alunoSelecionado() {

        Aluno aluno = this.comboAlunos.getValue();

        if(aluno == null){
            exibirMensagem(AlertType.WARNING,"Selecione um aluno.");
        }

        return aluno;
    }

    private String obterSequencia(TextField[] campos) {

        StringBuilder sequencia = new StringBuilder();

        for(TextField campo : campos){

            String resposta = campo.getText().trim().toUpperCase();

            if (!resposta.matches("[VF]")) {
                exibirMensagem(AlertType.WARNING,"Preencha as dez questões somente com V ou F.");
                return null;
            }

            sequencia.append(resposta);
        }

        return sequencia.toString();
    }

    private void salvarRespostas() {

        Disciplina disciplina = disciplinaSelecionada();
        if(disciplina == null) return;

        Aluno aluno = alunoSelecionado();
        if(aluno == null) return;

        String respostas = obterSequencia(campoRespostas);
        if(respostas == null) return;

        File arquivo = arquivoProva(disciplina);

        if (!pastaProvas.exists() && !pastaProvas.mkdirs()) {
            exibirMensagem(AlertType.ERROR,"Não foi possível criar a pasta das provas.");
            return;
        }

        List<String> linhas = new ArrayList<>();
        boolean atualizado = false;

        try{

            if(arquivo.exists()){
                try(BufferedReader br =new BufferedReader(new FileReader(arquivo))){

                    String linha;

                    while((linha = br.readLine()) != null){

                        String[] partes = linha.split("\t", 2);

                        if(partes.length == 2 && partes[1].trim().equalsIgnoreCase(aluno.getNome())) {

                            linhas.add(respostas + "\t" + aluno.getNome());
                            atualizado = true;

                        }else{
                            linhas.add(linha);
                        }
                    }
                }
            }

            if(!atualizado){
                linhas.add(respostas + "\t" + aluno.getNome());
            }

            try(BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo))){

                for(String linha : linhas){
                    bw.write(linha);
                    bw.newLine();
                }
            }

            exibirMensagem(AlertType.INFORMATION, atualizado ? "Respostas atualizadas com sucesso." : "Respostas registradas com sucesso.");

        }catch(IOException e){
            exibirMensagem(AlertType.ERROR,"Falha ao salvar as respostas da prova.");
        }
    }

    private void carregarRespostas() {

        limparCampos(campoRespostas);

        Disciplina disciplina = this.lista.getSelectionModel().getSelectedItem();

        Aluno aluno = this.comboAlunos.getValue();

        if(disciplina == null || aluno == null){
            return;
        }

        File arquivo = arquivoProva(disciplina);

        if(!arquivo.exists()) {
            return;
        }

        try(BufferedReader br = new BufferedReader(new FileReader(arquivo))) {

            String linha;

            while((linha = br.readLine()) != null){

                String[] partes = linha.split("\t", 2);

                if(partes.length != 2){
                    continue;
                }

                if(partes[1].trim().equalsIgnoreCase(aluno.getNome())){

                    String respostas = partes[0].trim().toUpperCase();

                    if (!respostas.matches("[VF]{10}")) {
                        exibirMensagem(AlertType.ERROR,"As respostas para esse aluno são inválidas.");
                        return;
                    }

                    for(int i = 0; i < 10; i++){
                        campoRespostas[i].setText(String.valueOf(respostas.charAt(i)));
                    }

                    return;
                }
            }

        }catch (IOException e){
            exibirMensagem(AlertType.ERROR,"Falha ao carregar as respostas.");
        }
    }

    private void cadastrarGabarito() {

        Disciplina disciplina = disciplinaSelecionada();
        if(disciplina == null) return;

        File arquivo = arquivoGabarito(disciplina);

        if(arquivo.exists()){
            exibirMensagem(AlertType.WARNING, "Essa disciplina já possui gabarito.");
            return;
        }

        String gabarito = obterSequencia(campoGabarito);
        if(gabarito == null) return;

        if(!pastaGabaritos.exists() && !pastaGabaritos.mkdirs()){
            exibirMensagem(AlertType.ERROR,"Não foi possível criar a pasta de gabaritos.");
            return;
        }

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo))){

            bw.write(gabarito);
            exibirMensagem(AlertType.CONFIRMATION, "Gabarito cadastrado com sucesso.");

        }catch (IOException e){
            exibirMensagem(AlertType.ERROR,"Falha ao salvar o gabarito.");
        }
    }

    private void carregarGabarito(Disciplina disciplina) {

        limparCampos(campoGabarito);

        if(disciplina == null) return;

        File arquivo = arquivoGabarito(disciplina);

        if(!arquivo.exists()) return;

        try(BufferedReader br = new BufferedReader(new FileReader(arquivo))) {

            String gabarito = br.readLine();

            if(gabarito == null || !gabarito.matches("[VF]{10}") || br.readLine() != null) {

                exibirMensagem(AlertType.ERROR,"O arquivo do gabarito é inválido.");
                return;
            }

            for(int i = 0; i < 10; i++){
                campoGabarito[i].setText(String.valueOf(gabarito.charAt(i)));
            }

        }catch (IOException e){
            exibirMensagem(AlertType.ERROR,"Falha ao carregar o gabarito.");
        }
    }

    private void atualizarGabarito() {

        Disciplina disciplina = disciplinaSelecionada();
        if(disciplina == null) return;

        File arquivo = arquivoGabarito(disciplina);

        if(!arquivo.exists()){
            exibirMensagem(AlertType.WARNING, "Cadastre o gabarito antes de atualizá-lo.");
            return;
        }

        String gabarito = obterSequencia(campoGabarito);
        if(gabarito == null) return;

        Alert confirmacao = new Alert(AlertType.CONFIRMATION, "Deseja atualizar o gabarito de " + disciplina.getNome() + "?");

        confirmacao.setHeaderText("Confirmar atualização");

        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL)!= ButtonType.OK) {
            return;
        }

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo))){

            bw.write(gabarito);
            exibirMensagem(AlertType.CONFIRMATION, "Gabarito atualizado com sucesso.");

        }catch(IOException e){
            exibirMensagem(AlertType.ERROR,"Falha ao atualizar o gabarito.");
        }
    }

    private void removerGabarito(){

        Disciplina disciplina = disciplinaSelecionada();
        if(disciplina == null) return;

        File arquivo = arquivoGabarito(disciplina);

        if(!arquivo.exists()){
            exibirMensagem(AlertType.WARNING,"Essa disciplina não possui gabarito.");
            return;
        }

        Alert confirmacao = new Alert(
            AlertType.CONFIRMATION,"Deseja remover o gabarito de "+ disciplina.getNome() + "?"
        );

        confirmacao.setHeaderText("Confirmar remoção");

        if(confirmacao.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        if(arquivo.delete()){

            limparCampos(campoGabarito);
            exibirMensagem(AlertType.INFORMATION,"Gabarito removido com sucesso.");

        }else{
            exibirMensagem(AlertType.ERROR,"Não foi possível remover o gabarito.");
        }
    }

    private void limparCampos(TextField[] campos) {

        for(TextField campo : campos){
            if (campo != null) {
                campo.clear();
            }
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
