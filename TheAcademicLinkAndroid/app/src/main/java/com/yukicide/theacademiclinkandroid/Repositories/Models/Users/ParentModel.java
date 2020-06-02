package com.yukicide.theacademiclinkandroid.Repositories.Models.Users;

import com.yukicide.theacademiclinkandroid.Repositories.Fixed.Gender;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.UserModel;

public class ParentModel extends UserModel {
    String childId;

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public ParentModel(String firstName, String surname, UserType userType, Gender gender, String email) {
        super(firstName, surname, userType, gender, email);
    }

    public ParentModel() {
    }
}
