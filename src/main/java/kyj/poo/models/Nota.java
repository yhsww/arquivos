package kyj.poo.models;

public class Nota {

    private Aluno aluno;
    private Disciplina disciplina;
    private int qtdAcertos;

    //calcular nota do zero
    public Nota(Aluno aluno, Disciplina disciplina, String gabarito) {
        this.aluno = aluno;
        this.disciplina = disciplina;
        this.qtdAcertos = calcularAcertos(aluno.getRespostas(), gabarito);
    }

    //ler do arquivo
    public Nota(Aluno aluno, Disciplina disciplina, int qtdAcertos) {
        this.aluno = aluno;
        this.disciplina = disciplina;
        this.qtdAcertos = qtdAcertos;
    }

    private int calcularAcertos(String respostas, String gabarito) {
        if (
            respostas == null ||
            gabarito == null ||
            !respostas.matches("[VF]{10}") ||
            !gabarito.matches("[VF]{10}")
        ) {
            return 0;
        }

        if (
            respostas.matches("VVVVVVVVVV") || respostas.matches("FFFFFFFFFF")
        ) {
            return 0;
        }

        int total = 0;

        for (int i = 0; i < 10; i++) {
            if (respostas.charAt(i) == gabarito.charAt(i)) {
                total++;
            }
        }

        return total;
    }

    public Aluno getAluno() {
        return this.aluno;
    }

    public int getQtdAcertos() {
        return this.qtdAcertos;
    }

    public Disciplina getDisciplina() {
        return this.disciplina;
    }
}
