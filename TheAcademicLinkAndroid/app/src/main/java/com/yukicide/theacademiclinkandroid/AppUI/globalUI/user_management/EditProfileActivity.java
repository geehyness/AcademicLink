package com.yukicide.theacademiclinkandroid.AppUI.globalUI.user_management;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.AppUI.adminUI.home.AdminHomeActivity;
import com.yukicide.theacademiclinkandroid.AppUI.studentUI.home.StudentHomeActivity;
import com.yukicide.theacademiclinkandroid.AppUI.teacherUI.home.TeacherHomeActivity;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.Gender;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.AdminModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.ParentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.StudentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.TeacherModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.UserModel;
import com.yukicide.theacademiclinkandroid.Repositories.UIElements.MyProgressDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {
    UserModel currentUser, profileUser;
    StudentModel studentModel;
    TeacherModel teacherModel;
    AdminModel adminModel;
    ParentModel parentModel;

    TextInputLayout password, email, fname, oname, sname, dname, dob, phone, address;
    Spinner gender;
    ImageView subjectSelect, btnDOB;
    TextView selectedList, lblSelected;

    MyProgressDialog progressDialog;

    Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profileUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.PROFILE_USER), UserModel.class);
        currentUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), UserModel.class);

        displayInfo();
    }

    private void displayInfo() {
        
    }
}
