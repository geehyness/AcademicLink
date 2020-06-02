package com.yukicide.theacademiclinkandroid.AppUI.globalUI.user_management;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.AppUI.adminUI.home.AdminHomeActivity;
import com.yukicide.theacademiclinkandroid.AppUI.studentUI.home.StudentHomeActivity;
import com.yukicide.theacademiclinkandroid.AppUI.teacherUI.home.TeacherHomeActivity;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.AdminModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.ParentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.StudentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.TeacherModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.UserModel;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        TextInputLayout txtEmail = findViewById(R.id.txtName);
        TextInputLayout txtPassword = findViewById(R.id.txtGrade);

        boolean error = false;

        if (TextUtils.isEmpty(Objects.requireNonNull(txtEmail.getEditText()).getText().toString())) {
            error = true;
            txtEmail.getEditText().setError("Email cannot be empty!");
            txtEmail.getEditText().requestFocus();
        }

        if (TextUtils.isEmpty(Objects.requireNonNull(txtPassword.getEditText()).getText().toString())) {
            error = true;
            txtPassword.getEditText().setError("Password cannot be empty!");
            txtPassword.getEditText().requestFocus();
        }

        if (error)
            return;

        String email = txtEmail.getEditText().getText().toString();
        String password = txtPassword.getEditText().getText().toString();

        FirebaseAuth fbAuth = FirebaseAuth.getInstance();
        fbAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> FirebaseFirestore.getInstance()
                        .collection(CollectionName.USERS)
                        .document(Objects.requireNonNull(fbAuth.getUid()))
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            UserModel currentUser = documentSnapshot.toObject(UserModel.class);

                            if (currentUser != null) {
                                if (!currentUser.isNewUser()) {
                                    if (currentUser.getUserType().equals(UserType.ADMIN)){
                                        currentUser = documentSnapshot.toObject(AdminModel.class);
                                        assert currentUser != null;
                                        currentUser.setId(documentSnapshot.getId());
                                        startActivity(new Intent(LoginActivity.this, AdminHomeActivity.class)
                                                .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser)));
                                        finish();
                                    }

                                    if (currentUser.getUserType().equals(UserType.TEACHER)){
                                        currentUser = documentSnapshot.toObject(TeacherModel.class);
                                        assert currentUser != null;
                                        currentUser.setId(documentSnapshot.getId());

                                        startActivity(new Intent(LoginActivity.this, TeacherHomeActivity.class)
                                                .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser)));
                                        finish();
                                    }

                                    if (currentUser.getUserType().equals(UserType.STUDENT) ||
                                            currentUser.getUserType().equals(UserType.PARENT)){
                                        currentUser.setId(documentSnapshot.getId());

                                        if (currentUser.getUserType().equals(UserType.PARENT)) {
                                            currentUser = documentSnapshot.toObject(ParentModel.class);
                                        } else {
                                            currentUser = documentSnapshot.toObject(StudentModel.class);
                                        }

                                        startActivity(new Intent(LoginActivity.this, StudentHomeActivity.class)
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
                            new AlertDialog.Builder(LoginActivity.this, R.style.CustomDialogTheme)
                                    .setIcon(R.drawable.ic_error_outline)
                                    .setTitle("Error")
                                    .setMessage(e.getMessage() + "\n\nIf you continue to get this error contact the school admin.")
                                    .setPositiveButton("Ok", null)
                                    .show();
                            fbAuth.signOut();
                        }))
                .addOnFailureListener(e -> new AlertDialog.Builder(LoginActivity.this, R.style.CustomDialogTheme)
                        .setIcon(R.drawable.ic_error_outline)
                        .setTitle("Error")
                        .setMessage(e.getMessage())
                        .setPositiveButton("Ok", null)
                        .show());
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(LoginActivity.this, R.style.CustomDialogTheme)
                .setIcon(R.drawable.ic_error_outline)
                .setTitle("Exit")
                .setMessage("Would you like to close the app?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }
}
