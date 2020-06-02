package com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking;

import java.util.ArrayList;

public class SubjectCollectionModel {
    private ArrayList<SubjectModel> subjectList = new ArrayList<>();
    private int grade;

    public ArrayList<SubjectModel> getSubjectList() {
        return subjectList;
    }

    public void setSubjectList(ArrayList<SubjectModel> subjectList) {
        this.subjectList = subjectList;
    }

    public int getCollectionGrade() {
        return grade;
    }

    public void setCollectionGrade(int grade) {
        this.grade = grade;
    }

    public SubjectCollectionModel() {
    }

    public SubjectCollectionModel(int grade, ArrayList<SubjectModel> subjectList) {
        this.subjectList = subjectList;
        this.grade = grade;
    }
}
