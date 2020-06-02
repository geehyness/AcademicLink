package com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking;

import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.ClassModel;

import java.util.ArrayList;

public class GradeModel {
    int Grade;
    ArrayList<ClassModel> classes = new ArrayList<>();

    public GradeModel(int grade, ArrayList<ClassModel> classes) {
        Grade = grade;
        this.classes = classes;
    }

    public ArrayList<ClassModel> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<ClassModel> classes) {
        this.classes = classes;
    }

    public int getGrade() {
        return Grade;
    }

    public void setGrade(int grade) {
        Grade = grade;
    }
}
