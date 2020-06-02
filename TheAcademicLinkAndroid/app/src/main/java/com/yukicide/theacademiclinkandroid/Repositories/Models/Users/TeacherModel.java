package com.yukicide.theacademiclinkandroid.Repositories.Models.Users;

import com.yukicide.theacademiclinkandroid.Repositories.Fixed.Gender;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;
import com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking.SubjectModel;

import java.util.ArrayList;
import java.util.Date;

public class TeacherModel extends UserModel {
    private ArrayList<String> subjects = new ArrayList<>();
    String classId;

    public ArrayList<String> getSubjects() { return subjects; }
    public void setSubjects(ArrayList<String> subjects) {
        this.subjects = subjects;
    }
    public String getClassId() {
        return classId;
    }
    public void setClassId(String classId) {
        this.classId = classId;
    }

    public TeacherModel(String name, String surname, Gender gender, String email) {
        super(name, surname, UserType.TEACHER, gender, email);
    }

    public TeacherModel() {
    }
}
