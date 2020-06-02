package com.yukicide.theacademiclinkandroid.Repositories.Models.Users;

public class ClassModel {
    private int grade;
    private String name, id;

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ClassModel() {
    }

    public ClassModel(int grade, String name) {
        this.grade = grade;
        this.name = name;
    }


}
