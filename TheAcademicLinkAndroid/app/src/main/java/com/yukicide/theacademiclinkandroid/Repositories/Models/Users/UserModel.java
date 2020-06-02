package com.yukicide.theacademiclinkandroid.Repositories.Models.Users;

import com.yukicide.theacademiclinkandroid.Repositories.Fixed.Gender;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;

import java.util.ArrayList;
import java.util.Date;

public class UserModel {
    private String id;
    private String firstName;
    private String otherNames = "";
    private String surname = "";
    private String displayName = "";
    private String address = "";
    private String phone = "";
    private String email = "";
    private Date dob;
    private Gender gender;
    private UserType userType;
    private ArrayList<String> notificationsRead = new ArrayList<>();

    public boolean isNewUser() {
        return newUser;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }

    private boolean newUser;

    public ArrayList<String> getNotificationsRead() { return notificationsRead; }
    public void setNotificationsRead(ArrayList<String> notificationsRead) { this.notificationsRead = notificationsRead; }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getOtherNames() {
        return otherNames;
    }
    public void setOtherNames(String otherNames) {
        this.otherNames = otherNames;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Date getDob() {
        return dob;
    }
    public void setDob(Date dob) {
        this.dob = dob;
    }
    public Gender getGender() {
        return gender;
    }
    public void setGender(Gender gender) {
        this.gender = gender;
    }
    public UserType getUserType() {
        return userType;
    }
    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public UserModel(String firstName, String surname, UserType userType, Gender gender, String email) {
        this.firstName = firstName;
        this.surname = surname;
        this.userType = userType;
        this.dob = dob;
        this.gender = gender;
        this.email = email;
        newUser = true;
    }

    public UserModel() {
        newUser = true;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
