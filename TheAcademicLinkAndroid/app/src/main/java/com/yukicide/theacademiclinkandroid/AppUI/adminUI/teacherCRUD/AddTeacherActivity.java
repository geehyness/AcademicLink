package com.yukicide.theacademiclinkandroid.AppUI.adminUI.teacherCRUD;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.Gender;
import com.yukicide.theacademiclinkandroid.Repositories.GMailSender;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.TeacherModel;
import com.yukicide.theacademiclinkandroid.Repositories.UIElements.MyProgressDialog;

import java.util.ArrayList;
import java.util.Objects;

public class AddTeacherActivity extends AppCompatActivity {
    TextInputLayout txtFirstName;
    TextInputLayout txtSurname;
    TextInputLayout txtEmail;

    MyProgressDialog progressDialog;

    FirebaseAuth myAuth;
    FirebaseAuth tempAuth;
    Spinner spGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);

        myAuth = FirebaseAuth.getInstance();

        txtFirstName = findViewById(R.id.txtFirstName);
        txtSurname = findViewById(R.id.txtSurname);
        txtEmail = findViewById(R.id.txtName);

        FirebaseApp f = FirebaseApp.getInstance();

        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setApiKey(f.getOptions().getApiKey())
                .setApplicationId(f.getOptions().getApplicationId()).build();

        try {
            FirebaseApp myApp = FirebaseApp.initializeApp(getApplicationContext(), firebaseOptions, String.valueOf(R.string.app_name));
            tempAuth = FirebaseAuth.getInstance(myApp);
        } catch (IllegalStateException e) {
            tempAuth = FirebaseAuth.getInstance(FirebaseApp.getInstance(String.valueOf(R.string.app_name)));
        }
        //tempAuth =

        initSpinner();

        Button add = findViewById(R.id.btnAdd);
        add.setOnClickListener(v -> addUser());

        ImageView back = findViewById(R.id.btnBack);
        back.setOnClickListener(v -> finish());
    }

    private void initSpinner() {
        spGender = findViewById(R.id.spGender);

        ArrayList<String> genderList = new ArrayList<>();

        genderList.add("Pick a gender");
        genderList.add(Gender.MALE.name());
        genderList.add(Gender.FEMALE.name());

        if (!genderList.isEmpty()){
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spGender.setAdapter(adapter);
        }
    }

    private void addUser() {
        if (TextUtils.isEmpty(Objects.requireNonNull(txtFirstName.getEditText()).getText().toString())) {
            txtFirstName.getEditText().setError("First name cannot be empty!");
            Objects.requireNonNull(txtFirstName.getEditText()).requestFocus();
            return;
        }

        if (TextUtils.isEmpty(Objects.requireNonNull(txtSurname.getEditText()).getText().toString())) {
            txtSurname.getEditText().setError("Surname cannot be empty!");
            Objects.requireNonNull(txtSurname.getEditText()).requestFocus();
            return;
        }

        if (TextUtils.isEmpty(Objects.requireNonNull(txtEmail.getEditText()).getText().toString())) {
            txtEmail.getEditText().setError("Email cannot be empty!");
            Objects.requireNonNull(txtEmail.getEditText()).requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(Objects.requireNonNull(txtEmail.getEditText()).getText().toString()).matches()) {
            txtEmail.getEditText().setError("Email is invalid!");
            Objects.requireNonNull(txtEmail.getEditText()).requestFocus();
            return;
        }

        Gender gender = null;
        if (spGender.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Pick a gender", Toast.LENGTH_SHORT).show();
            return;
        } else if (spGender.getSelectedItemPosition() == 1) {
            gender = Gender.MALE;
        } else if (spGender.getSelectedItemPosition() == 2) {
            gender = Gender.FEMALE;
        }

        String name = txtFirstName.getEditText().getText().toString();
        String surname = txtSurname.getEditText().getText().toString();
        String email = txtEmail.getEditText().getText().toString();

        TeacherModel newTeacher = new TeacherModel(name, surname, gender, email);
        progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        tempAuth.createUserWithEmailAndPassword(email, "12345678")
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = tempAuth.getCurrentUser();
                    assert user != null;
                    user.updatePassword(Objects.requireNonNull(tempAuth.getCurrentUser().getEmail()))
                            .addOnSuccessListener(aVoid -> {
                                FirebaseFirestore ff = FirebaseFirestore.getInstance();
                                ff.collection(CollectionName.USERS).document(tempAuth.getUid())
                                        .set(newTeacher)
                                        .addOnSuccessListener(aVoid1 -> {
                                            // TODO: 2020/02/29 SEND EMAIL

                                            tempAuth.signOut();

                                            newTeacher.setId(tempAuth.getUid());
                                            sendEmail(newTeacher);

                                        })
                                        .addOnFailureListener(e -> {
                                            progressDialog.dismiss();
                                            new AlertDialog.Builder(AddTeacherActivity.this, R.style.CustomDialogTheme)
                                                    .setIcon(R.drawable.ic_error_outline)
                                                    .setTitle("Error")
                                                    .setMessage(e.getMessage())
                                                    .setPositiveButton("Ok", null)
                                                    .show();
                                        });
                            }).addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                new AlertDialog.Builder(AddTeacherActivity.this, R.style.CustomDialogTheme)
                                        .setIcon(R.drawable.ic_error_outline)
                                        .setTitle("Error")
                                        .setMessage(e.getMessage())
                                        .setPositiveButton("Ok", null)
                                        .show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(AddTeacherActivity.this, R.style.CustomDialogTheme)
                            .setIcon(R.drawable.ic_error_outline)
                            .setTitle("Error")
                            .setMessage(e.getMessage())
                            .setPositiveButton("Ok", null)
                            .show();
                });
    }

    private void sendEmail(TeacherModel newStudent) {
        try {
            GMailSender sender = new GMailSender("yukiyuuki2.0@gmail.com", "callmeYuki0k?");
            sender.sendMail("New academic link account", "Email: " + newStudent.getEmail() +
                    "\nPassword: " + newStudent.getId(), "geehyness22@gmail.com", newStudent.getEmail());

            progressDialog.dismiss();
            new AlertDialog.Builder(AddTeacherActivity.this, R.style.CustomDialogTheme)
                    .setIcon(R.drawable.ic_success)
                    .setTitle("Success")
                    .setMessage(newStudent.getFirstName() + " " + newStudent.getSurname() + " added.\n" + "Would you like to add another Teacher?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        spGender.setSelection(0);
                        txtEmail.getEditText().setText("");
                        txtFirstName.getEditText().setText("");
                        txtSurname.getEditText().setText("");
                    })
                    .setNegativeButton("No", (dialog, which) -> finish())
                    .show();
        } catch (Exception e) {
            progressDialog.dismiss();
            new AlertDialog.Builder(AddTeacherActivity.this, R.style.CustomDialogTheme)
                    .setIcon(R.drawable.ic_error_outline)
                    .setTitle("Error")
                    .setMessage(e.getMessage())
                    .setPositiveButton("Ok", null)
                    .show();
        }
    }
}
