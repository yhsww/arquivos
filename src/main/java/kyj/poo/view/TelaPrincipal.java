package kyj.poo.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import kyj.poo.Imagens;

public class TelaPrincipal{

    public void mostrar(Stage stage){

        BorderPane tela = new BorderPane();
        tela.getStyleClass().add("root");

        tela.setLeft(criarMenu());
        tela.setCenter(criarConteudo());

        Scene scene = new Scene(tela, 1100, 700);
        var css = getClass().getResource("/estilo.css");

        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.err.println("CSS não encontrado: /estilo.css");
        }

        stage.setTitle("JYK");
        stage.setMinWidth(850);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }

    private ImageView criarImagem(String nome, double largura, double altura) {

    Image image = Imagens.carregar(nome);

    if (image == null) {
        return new ImageView();
    }

    ImageView view = new ImageView(image);
    view.setFitWidth(largura);
    view.setFitHeight(altura);
    view.setPreserveRatio(true);

    return view;
}

    private VBox criarConteudo(){

        VBox conteudo = new VBox(26);
        conteudo.getStyleClass().add("tela");
        conteudo.setPadding(new Insets(32, 36, 24, 36));

        Label titulo = new Label("Painel geral");
        titulo.getStyleClass().add("titulo-pagina");

        Label subtitulo = new Label("Organize os dados acadêmicos.");
        subtitulo.getStyleClass().add("subtitulo-pagina");

        VBox cabecalho = new VBox(8, titulo, subtitulo);
        HBox cards = new HBox(16);

        VBox cardAlunos = criarCard("pessoa.png", "Alunos cadastrados", 0 , "Total de alunos");
        VBox cardDisciplinas = criarCard("disciplina.png", "Disciplinas cadastradas", 0 ,"Total de disciplinas");
        VBox cardBoletins = criarCard("resultado.png", "Boletins registrados", 0, "Total de boletins");

        cards.getChildren().addAll(cardAlunos, cardDisciplinas, cardBoletins);

        for(var card : cards.getChildren()){
            HBox.setHgrow(card, Priority.ALWAYS);
        }

        //painel de acoes

        Label tituloAcoes = new Label("Acesso rápido");
        tituloAcoes.getStyleClass().add("titulo-secao");

        Label subtituloAcoes = new Label("Selecione uma opção");
        subtituloAcoes.getStyleClass().add("aux-secao");

        VBox cabecalhoAcoes = new VBox(6, tituloAcoes, subtituloAcoes);

        //painel boletins

        ImageView iconGerarBoletins = criarImagem("disciplina.png", 44, 44);
        Label nomeGerarBoletins = new Label("Gerar boletins");

        iconGerarBoletins.getStyleClass().add("acao-icon");
        nomeGerarBoletins.getStyleClass().add("acao-titulo");

        VBox gerarBoletim = new VBox(14, iconGerarBoletins, nomeGerarBoletins);
        
        Button botaoGerar = new Button("Gerar");
        botaoGerar.getStyleClass().add("botao-principal");
        botaoGerar.setMaxWidth(Double.MAX_VALUE);

        HBox acaoBoletins = new HBox(14, gerarBoletim, botaoGerar);
        acaoBoletins.getStyleClass().add("painel-acao");

        //painel consultar resultados

        ImageView iconConsulta = criarImagem("resultado.png", 44, 44);
        Label nomeConsulta = new Label("Consultar resultados");

        iconConsulta.getStyleClass().add("acao-icon");
        nomeConsulta.getStyleClass().add("acao-titulo");

        VBox consultar = new VBox(12, iconConsulta, nomeConsulta);

        Button botaoConsultar = new Button("Consultar");
        botaoConsultar.getStyleClass().add("botao-principal");
        botaoConsultar.setMaxWidth(Double.MAX_VALUE);

        HBox acaoConsultas = new HBox(14, consultar, botaoConsultar);
        acaoConsultas.getStyleClass().add("painel-acao");

        // boletins + consulta

        HBox painelAcoes = new HBox(18, acaoBoletins, acaoConsultas);
        painelAcoes.setHgrow(acaoBoletins, Priority.ALWAYS);
        painelAcoes.setHgrow(acaoConsultas, Priority.ALWAYS);

        Region espacoBase = new Region();
        VBox.setVgrow(espacoBase, Priority.ALWAYS);

        //eventos botoes

        botaoGerar.setOnAction(e -> exibirMensagem("teste"));
        botaoConsultar.setOnAction(e -> exibirMensagem("teste"));

        conteudo.getChildren().addAll(cabecalho, cards, cabecalhoAcoes, painelAcoes, espacoBase);

        return conteudo;


    }

    private VBox criarMenu(){

        //menu lateral
        VBox menu = new VBox(8);

        menu.getStyleClass().add("menu-lateral");
        menu.setPadding(new Insets(20, 15, 20, 15));
        menu.setMinWidth(220);
        menu.setMaxWidth(220);

        Label mIcon = new Label("KYJ");
        mIcon.getStyleClass().add("logo");

        Label nome = new Label("Acadêmico");
        nome.getStyleClass().add("logo-nome");

        Label desc = new Label("Gerenciador de Boletins");
        desc.getStyleClass().add("logo-desc");

        VBox identidade = new VBox(4, nome, desc);
        HBox cabecalho = new HBox(10, mIcon, identidade);
        cabecalho.setAlignment(Pos.CENTER_LEFT);
        cabecalho.setPadding(new Insets(0, 0, 28, 0));

        //menu principal
        Label rotuloMenu = new Label("Menu principal");
        rotuloMenu.getStyleClass().add("menu-secao");

        Button btnInicio = criarBotaoMenu("casa.png", "Início", false);
        Button btnAlunos = criarBotaoMenu("pessoa.png", "Alunos", false);
        Button btnDisciplinas = criarBotaoMenu("disciplina.png", "Disciplinas", false);
        Button btnResultados = criarBotaoMenu("resultado.png", "Resultados", false);

        Region espaco = new Region();
        VBox.setVgrow(espaco, Priority.ALWAYS);

        Label tituloRodape = new Label("KYJ - Gerenciador de Boletins");
        tituloRodape.getStyleClass().add("menu-texto");
        ImageView iconRodape = criarImagem("box.png", 22, 22);
        iconRodape.getStyleClass().add("menu-icon");
        HBox rodape = new HBox(14, tituloRodape, iconRodape);
        rodape.setAlignment(Pos.CENTER_LEFT);
        rodape.getStyleClass().add("menu-rodape");

        menu.getChildren().addAll(cabecalho, rotuloMenu, btnInicio, btnAlunos, btnDisciplinas, btnResultados, espaco, rodape);

        btnInicio.setOnAction(e -> exibirMensagem("Página Inicial"));
        btnAlunos.setOnAction(e -> exibirMensagem("alunos..."));
        btnDisciplinas.setOnAction(e -> exibirMensagem("disciplinas..."));
        btnResultados.setOnAction(e -> exibirMensagem("resultados..."));

        return menu;

    }


    private Button criarBotaoMenu(String icon, String texto, boolean selecionado){

        ImageView bIcon = criarImagem(icon, 22, 22);
        bIcon.getStyleClass().add("menu-icon");

        Label bTexto = new Label(texto);
        bTexto.getStyleClass().add("menu-texto");

        HBox botaoConteudo = new HBox(12, bIcon, bTexto);
        botaoConteudo.setAlignment(Pos.CENTER_LEFT);

        Button botao = new Button();
        botao.setGraphic(botaoConteudo);
        botao.setMaxWidth(Double.MAX_VALUE);
        botao.setAlignment(Pos.CENTER_LEFT);
        botao.getStyleClass().add("botao-menu");

        if(selecionado){
            botao.getStyleClass().add("selecionado");
        }

        return botao;

    }

    private VBox criarCard(String icon, String titulo, int valor, String desc){

        ImageView cIcon = criarImagem(icon, 40, 40);
        cIcon.getStyleClass().add("card-icon");

        Label cTitulo = new Label(titulo);
        cTitulo.getStyleClass().add("card-titulo");

        Label cValor = new Label(String.valueOf(valor));
        cValor.getStyleClass().add("card-valor");

        Label cDesc = new Label(desc);
        cDesc.getStyleClass().add("card-desc");

        VBox card = new VBox(12, cIcon, cTitulo, cValor, cDesc);
        card.getStyleClass().add("card");
        card.setMinWidth(0);
        card.setPadding(new Insets(20));

        return card;

    }

    private void exibirMensagem(String mensagem){

        Alert msg = new Alert(AlertType.INFORMATION);
        msg.setTitle("Gerenciador de Boletins");
        msg.setHeaderText(null);
        msg.setContentText(mensagem);
        msg.show();
    }
    
}
