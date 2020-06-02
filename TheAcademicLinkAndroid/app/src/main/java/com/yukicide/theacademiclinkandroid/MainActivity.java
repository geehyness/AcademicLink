package com.yukicide.theacademiclinkandroid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.AppUI.teacherUI.home.TeacherHomeActivity;
import com.yukicide.theacademiclinkandroid.AppUI.globalUI.user_management.EditProfileActivity;
import com.yukicide.theacademiclinkandroid.AppUI.globalUI.user_management.LoginActivity;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.ParentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.AdminModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.StudentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.TeacherModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.UserModel;
import com.yukicide.theacademiclinkandroid.AppUI.adminUI.home.AdminHomeActivity;
import com.yukicide.theacademiclinkandroid.AppUI.studentUI.home.StudentHomeActivity;

import java.util.Objects;

import static com.yukicide.theacademiclinkandroid.R.layout.activity_main;

public class MainActivity extends AppCompatActivity {

    UserModel currentUser = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);

        new Handler().postDelayed(() -> {
            FirebaseAuth fbAuth = FirebaseAuth.getInstance();

            if (fbAuth != null) {
                if (fbAuth.getUid() != null) {

                    FirebaseFirestore ff = FirebaseFirestore.getInstance();
                    ff.collection(CollectionName.USERS)
                            .document(Objects.requireNonNull(fbAuth.getUid()))
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                currentUser = documentSnapshot.toObject(UserModel.class);
                                currentUser.setId(fbAuth.getUid());

                                if (currentUser != null) {
                                    // TODO: 2020/03/25 CHECK NEW USERS
                                    if (!currentUser.isNewUser()) {
                                        if (currentUser.getUserType().equals(UserType.ADMIN)){
                                            currentUser = documentSnapshot.toObject(AdminModel.class);
                                            assert currentUser != null;
                                            currentUser.setId(documentSnapshot.getId());
                                            startActivity(new Intent(MainActivity.this, AdminHomeActivity.class)
                                                    .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser)));
                                            finish();

                                    /*currentUser = documentSnapshot.toObject(TeacherModel.class);
                                    assert currentUser != null;
                                    currentUser.setId(documentSnapshot.getId());
                                    currentUser.setUserType(UserType.TEACHER);
                                    ((TeacherModel)currentUser).setClassId("1B");
                                    startActivity(new Intent(MainActivity.this, TeacherHomeActivity.class)
                                            .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser)));
                                    finish();*/
                                        }

                                        if (currentUser.getUserType().equals(UserType.TEACHER)){
                                            currentUser = documentSnapshot.toObject(TeacherModel.class);
                                            assert currentUser != null;
                                            currentUser.setId(documentSnapshot.getId());

                                            startActivity(new Intent(MainActivity.this, TeacherHomeActivity.class)
                                                    .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser)));
                                            finish();
                                        }

                                        if (currentUser.getUserType().equals(UserType.STUDENT) ||
                                                currentUser.getUserType().equals(UserType.PARENT)){
                                            assert currentUser != null;
                                            currentUser.setId(documentSnapshot.getId());

                                            if (currentUser.getUserType().equals(UserType.PARENT)) {
                                                currentUser = documentSnapshot.toObject(ParentModel.class);
                                            } else {
                                                currentUser = documentSnapshot.toObject(StudentModel.class);
                                            }

                                            startActivity(new Intent(MainActivity.this, StudentHomeActivity.class)
                                                    .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser)));
                                            finish();
                                        }
                                    } else {
                                        startActivity(new Intent(this, EditProfileActivity.class)
                                                .putExtra(StringExtras.PROFILE_USER, (new Gson()).toJson(currentUser))
                                                .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser)));
                                    }
                                }
                            })
                            .addOnFailureListener(e -> {
                                new AlertDialog.Builder(MainActivity.this, R.style.CustomDialogTheme)
                                        .setIcon(R.drawable.ic_error_outline)
                                        .setTitle("Error")
                                        .setMessage(e.getMessage())
                                        .setPositiveButton("Ok", null)
                                        .show();
                                fbAuth.signOut();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                finish();
                            });
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            } else {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        }, 1500);
    }
}