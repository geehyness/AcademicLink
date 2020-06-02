package com.yukicide.theacademiclinkandroid.Repositories.Models.Users;

import com.yukicide.theacademiclinkandroid.Repositories.Fixed.Gender;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;
import com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking.SubjectModel;

import java.util.ArrayList;
import java.util.Date;

public class StudentModel extends UserModel {
    private ArrayList<SubjectModel> subjects = new ArrayList<>();

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassId() {
        return classId;
    }

    private String classId;

    public StudentModel(String name, String surname, Gender gender, String email) {
        super(name, surname, UserType.STUDENT, gender, email);
    }

    public ArrayList<SubjectModel> getSubjects() {
        return subjects;
    }

    public void setSubjects(ArrayList<SubjectModel> subjects) {
        this.subjects = subjects;
    }

    public StudentModel() {
    }
}
