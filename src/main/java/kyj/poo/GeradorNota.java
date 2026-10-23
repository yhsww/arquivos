package kyj.poo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import kyj.poo.models.Aluno;
import kyj.poo.models.Disciplina;
import kyj.poo.models.Nota;

public class GeradorNota {

    private File pathResultados; // escrita

    public GeradorNota(String pathResultados) {
        this.pathResultados = new File(pathResultados);
    }

    public void gerarResultados(File pastaProvas, File pastaGabaritos)
        throws IOException {
        if (!this.pathResultados.exists() && !this.pathResultados.mkdirs()) {
            throw new IOException(
                "Não foi possível criar a pasta de resultados."
            );
        }

        File[] provas = pastaProvas.listFiles();

        if (provas == null) {
            return;
        }

        for (File prova : provas) {
            if (!prova.isFile() || !prova.getName().endsWith(".txt")) continue;

            File gabarito = new File(pastaGabaritos, prova.getName());

            if (!gabarito.isFile()) continue;

            String nomeDisciplina = prova
                .getName()
                .substring(0, prova.getName().length() - ".txt".length());

            Disciplina disciplina = lerDisciplina(nomeDisciplina, prova);

            notasAlfabeticas(disciplina, gabarito.getPath());
            notasPorNota(disciplina, gabarito.getPath());
        }
    }

    private Disciplina lerDisciplina(String nome, File arquivo)
        throws IOException {
        Disciplina disciplina = new Disciplina(nome);

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;

            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split("\\t", 2);

                if (partes.length != 2) continue;

                String respostas = partes[0].trim().toUpperCase();
                String nomeAluno = partes[1].trim();

                disciplina.getAlunos().add(new Aluno(nomeAluno, respostas));
            }
        }

        return disciplina;
    }

    private String lerGabarito(String pathGabarito) throws IOException {
        try (
            BufferedReader br = new BufferedReader(new FileReader(pathGabarito))
        ) {
            String gabarito = br.readLine();

            if (gabarito == null || !gabarito.strip().matches("[VF]{10}")) {
                throw new IOException("Gabarito inválido.");
            }

            return gabarito.strip();
        }
    }

    public void notasAlfabeticas(Disciplina disciplina, String pathGabarito)
        throws IOException {
        String nomeArquivo = disciplina.getNome();

        if (
            !this.pathResultados.exists() || !this.pathResultados.isDirectory()
        ) {
            throw new IOException("O diretório buscado não foi encontrado.");
        }
        File arquivo = new File(
            this.pathResultados,
            nomeArquivo + "_alfabetico.txt"
        );

        String gabarito = lerGabarito(pathGabarito);

        List<Aluno> alunos = new ArrayList<>(disciplina.getAlunos());
        alunos.sort(Comparator.comparing(Aluno::getNome));

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo))) {
            for (Aluno aluno : alunos) {
                Nota nota = new Nota(aluno, disciplina, gabarito);
                bw.write(aluno.getNome() + "\t" + nota.getQtdAcertos());
                bw.newLine();
            }
        }
    }

    public void notasPorNota(Disciplina disciplina, String pathGabarito)
        throws IOException {
        String nomeArquivo = disciplina.getNome();

        if (
            !this.pathResultados.exists() || !this.pathResultados.isDirectory()
        ) {
            throw new IOException("O diretório buscado não foi encontrado.");
        }
        File arquivo = new File(
            this.pathResultados,
            nomeArquivo + "_notas.txt"
        );

        String gabarito = lerGabarito(pathGabarito);

        List<Nota> notas = new ArrayList<>();
        for (Aluno aluno : disciplina.getAlunos()) {
            notas.add(new Nota(aluno, disciplina, gabarito));
        }

        notas.sort(
            Comparator.comparingInt(Nota::getQtdAcertos)
                .reversed()
                .thenComparing(
                    n -> n.getAluno().getNome(),
                    String.CASE_INSENSITIVE_ORDER
                )
        );

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo))) {
            int soma = 0;
            for (Nota nota : notas) {
                bw.write(
                    nota.getAluno().getNome() + "\t" + nota.getQtdAcertos()
                );
                bw.newLine();
                soma += nota.getQtdAcertos();
            }
            double media = notas.isEmpty() ? 0 : (double) soma / notas.size();
            bw.write(String.format("Média: %.1f", media));
            bw.newLine();
        }
    }
}
