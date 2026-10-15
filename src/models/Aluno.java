package models;

public class Aluno {

    private String nome;
    private String respostas;

    public Aluno(String nome, String respostas){
        this.nome = nome;
        this.respostas = respostas;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public void setRespostas(String respostas){
        this.respostas = respostas;
    }

    public String getNome(){
        return this.nome;
    }

    public String getRespostas(){
        return this.respostas;
    }



}
