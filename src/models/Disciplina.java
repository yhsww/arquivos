package models;

import java.util.ArrayList;
import java.util.List;

public class Disciplina {

    private String nome;
    private List<Aluno> alunos;

    public void setNome(String nome){
        this.nome = nome;
    }

    public void setAlunos(List<Aluno> alunos){
        this.alunos = alunos;
    }

    public String getNome(){
        return this.nome;
    }

    public List<Aluno> getAlunos(){
        return this.alunos;
    }

    public Disciplina(String nome){
        this.nome = nome;
        this.alunos = new ArrayList<>();
    }

}
