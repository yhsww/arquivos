package kyj.poo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import kyj.poo.models.Aluno;
import kyj.poo.models.Boletim;
import kyj.poo.models.Disciplina;
import kyj.poo.models.Nota;

public class GeradorBoletim {

    private File pathResultados; //leitura
    private File pathBoletins; //escrita

    public GeradorBoletim(String pathResultados, String pathBoletins) {
        this.pathResultados = new File(pathResultados);
        this.pathBoletins = new File(pathBoletins);
    }

    private Boletim buscarBoletim(List<Boletim> boletins, String nomeAluno) {
        for (Boletim boletim : boletins) {
            if (boletim.getAluno().getNome().equals(nomeAluno)) {
                return boletim;
            }
        }

        return null;
    }

    private void salvarBoletim(Boletim boletim) throws IOException {
        String nomeAluno = boletim.getAluno().getNome();
        String nomeArquivo = nomeAluno.replaceAll("[\\/:*?\\\"<>|]", "_");

        File arquivo = new File(this.pathBoletins, nomeArquivo + ".txt");

        BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo));

        bw.write("-----------------------");
        bw.newLine();
        bw.write("Boletim Geral do Aluno");
        bw.newLine();
        bw.write("Nome: " + nomeAluno);
        bw.newLine();
        for (Nota nota : boletim.getNotas()) {
            bw.write(
                "Disciplina: " +
                    nota.getDisciplina().getNome() +
                    " Nota: %.2f\n" +
                    nota.getQtdAcertos()
            );
            bw.newLine();
        }

        bw.newLine();
        bw.write(String.format("Média final: %.2f\n", boletim.calcularMedia()));
        bw.newLine();

        bw.write(
            boletim.isAprovado()
                ? "Situação: aprovado(a)"
                : "Situação: reprovado(a)"
        );
        bw.newLine();
        bw.write("-----------------------");
        bw.close();
    }

    public void gerarBoletins() throws IOException {
        if (
            !this.pathResultados.exists() || !this.pathResultados.isDirectory()
        ) {
            throw new IOException("O diretório buscado não foi encontrado.");
        }

        this.pathBoletins.mkdirs();

        File[] arquivos = pathResultados.listFiles();

        if (arquivos == null) {
            throw new IOException("Falha ao ler resultados.");
        }

        List<Boletim> boletins = new ArrayList<>();

        for (File arquivo : arquivos) {
            if (
                !arquivo.isFile() || !arquivo.getName().endsWith("_notas.txt")
            ) continue;

            String nomeArquivo = arquivo.getName();

            String nomeDisciplina = nomeArquivo.substring(
                0,
                nomeArquivo.length() - "_notas.txt".length()
            );

            Disciplina disciplina = new Disciplina(nomeDisciplina);

            BufferedReader br = new BufferedReader(new FileReader(arquivo));

            String linha;

            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;

                String[] dadosLinha = linha.split("\\t", 2);

                if (dadosLinha.length != 2) continue;

                String nomeAluno = dadosLinha[0].trim();
                int acertos = Integer.parseInt(dadosLinha[1].trim());

                Boletim boletim = buscarBoletim(boletins, nomeAluno);

                if (boletim == null) {
                    Aluno aluno = new Aluno(nomeAluno, "");
                    boletim = new Boletim(aluno);
                    boletins.add(boletim);

                    Nota nota = new Nota(
                        boletim.getAluno(),
                        disciplina,
                        acertos
                    );

                    boletim.addNotas(nota);
                }
            }

            br.close();
        }

        for (Boletim boletim : boletins) {
            salvarBoletim(boletim);
        }
    }
}
