package models;

import models.Aluno;
import models.Nota;
import java.util.ArrayList;
import java.util.List;

public class Boletim {

    private Aluno aluno;
    private List<Nota> notas;

    public Boletim(Aluno aluno){
        this.aluno = aluno;
        this.notas = new ArrayList<>();
    }

      public Aluno getAluno(){
        return this.aluno;
    }

    public List<Nota> getNotas(){
        return this.notas;
    }

    public void addNotas(Nota nota){

        if(nota.getAluno().equals(this.aluno)){
            this.notas.add(nota);
        }
    }
    
    public double calcularMedia() {

        if (this.notas.isEmpty()) {
            return 0;
        }

        double soma = 0;

        for (Nota nota : this.notas) {
            soma += nota.getQtdAcertos();
        }

        return soma/this.notas.size();
    }

    public boolean isAprovado() {

        return !this.notas.isEmpty() && calcularMedia() >= 7.0;
    }

}
