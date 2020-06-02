package com.yukicide.theacademiclinkandroid.Repositories.Models.Users;

import com.yukicide.theacademiclinkandroid.Repositories.Fixed.Gender;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;

import java.util.Date;

public class AdminModel extends UserModel {

    public AdminModel(String name, String surname, Gender gender, String email) {
        super(name, surname, UserType.ADMIN, gender, email);
    }

    public AdminModel() {
    }
}
