package com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking;

import java.util.ArrayList;

public class SubjectModel {
    private String  id,
            name,
            classId;
    private int grade;
    private boolean isMandatory = false;
    private ArrayList<MarksModel> marks = new ArrayList<>();

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public int getGrade() {
        return grade;
    }
    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getClassId() {
        return classId;
    }
    public void setClassId(String classId) {
        this.classId = classId;
    }

    public ArrayList<MarksModel> getMarks() {
        return marks;
    }
    public void setMarks(ArrayList<MarksModel> marks) {
        this.marks = marks;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public SubjectModel() {}
    public SubjectModel(int grade, String name) {
        this.grade = grade;
        this.name = name;
    }

    public SubjectModel(int grade, String classId, String name) {
        this.classId = classId;
        this.grade = grade;
        this.name = name;
    }

    public boolean isMandatory() { return isMandatory; }
    public void setMandatory(boolean mandatory) { isMandatory = mandatory; }
}
