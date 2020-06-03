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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.AppUI.adminUI.home.AdminHomeActivity;
import com.yukicide.theacademiclinkandroid.AppUI.studentUI.home.StudentHomeActivity;
import com.yukicide.theacademiclinkandroid.AppUI.teacherUI.home.TeacherHomeActivity;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.Gender;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;
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
    TextInputLayout password, email, fname, oname, sname, dname, dob, phone, address;
    Spinner gender;
    ImageView subjectSelect, btnDOB;
    TextView selectedList, lblSelected;

    Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profileUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.PROFILE_USER), UserModel.class);
        currentUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), UserModel.class);

        if (profileUser == null || currentUser == null)
            new AlertDialog.Builder(EditProfileActivity.this, R.style.CustomDialogTheme)
                    .setIcon(R.drawable.ic_error_outline)
                    .setTitle("Error")
                    .setMessage("Unable to load user information!")
                    .setCancelable(false)
                    .setPositiveButton("Ok", (dialog, which) -> finish())
                    .show();

        displayUserInfo();

        if (profileUser.getId().equals(currentUser.getId()) && currentUser.isNewUser()) {
            password.setVisibility(View.VISIBLE);

            if (currentUser.getUserType().equals(UserType.STUDENT)) {
                selectedList.setVisibility(View.VISIBLE);
                subjectSelect.setVisibility(View.VISIBLE);
                lblSelected.setVisibility(View.VISIBLE);
            }
        } else if (!currentUser.isNewUser()) {
            date = currentUser.getDob();
        }

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            UserModel tempUser = profileUser;

            if (Objects.requireNonNull(email.getEditText()).getText().toString().isEmpty()) {
                email.getEditText().setError("Email cannot be empty!");
                email.getEditText().requestFocus();
                return;
            }

            if (Objects.requireNonNull(fname.getEditText()).getText().toString().isEmpty()) {
                fname.getEditText().setError("First Name cannot be empty!");
                fname.getEditText().requestFocus();
                return;
            }

            if (Objects.requireNonNull(sname.getEditText()).getText().toString().isEmpty()) {
                sname.getEditText().setError("Surname cannot be empty!");
                sname.getEditText().requestFocus();
                return;
            }

            if (date == null) {
                dob.getEditText().setError("Date of birth cannot be empty!");
                dob.getEditText().requestFocus();
                return;
            }

            if (Objects.requireNonNull(phone.getEditText()).getText().toString().isEmpty()) {
                phone.getEditText().setError("Phone number cannot be empty!");
                phone.getEditText().requestFocus();
                return;
            }

            tempUser.setFirstName(fname.getEditText().getText().toString());
            tempUser.setSurname(sname.getEditText().getText().toString());

            tempUser.setPhone(phone.getEditText().getText().toString());

            tempUser.setOtherNames(oname.getEditText().getText().toString());
            tempUser.setDisplayName(dname.getEditText().getText().toString());
            tempUser.setAddress(address.getEditText().getText().toString());

            MyProgressDialog progressDialog = new MyProgressDialog(EditProfileActivity.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();

            if (currentUser.isNewUser()) {
                // TODO: 2020/04/24 PUT VALID DEFAULT PASSWORD
                FirebaseAuth.getInstance().signInWithEmailAndPassword(currentUser.getEmail(), "12345678")
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                currentUser = tempUser;
                                currentUser.setNewUser(false);
                                currentUser.setDob(date);

                                String pass = Objects.requireNonNull(password.getEditText()).getText().toString();
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                assert user != null;
                                user.updatePassword(pass)
                                        .addOnSuccessListener(aVoid -> FirebaseFirestore.getInstance().collection(CollectionName.USERS)
                                                .document(currentUser.getId())
                                                .set(currentUser)
                                                .addOnSuccessListener(aVoid1 -> {
                                                    progressDialog.dismiss();
                                                    new AlertDialog.Builder(EditProfileActivity.this, R.style.CustomDialogTheme)
                                                            .setIcon(R.drawable.ic_success)
                                                            .setTitle("Success")
                                                            .setMessage("Profile updated!")
                                                            .setPositiveButton("Ok", (dialog, which) -> {
                                                                if (currentUser.getUserType().equals(UserType.ADMIN)){
                                                                    startActivity(new Intent(EditProfileActivity.this, AdminHomeActivity.class)
                                                                            .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser)));
                                                                    finish();
                                                                } else if (currentUser.getUserType().equals(UserType.TEACHER)){
                                                                    startActivity(new Intent(EditProfileActivity.this, TeacherHomeActivity.class)
                                                                            .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser)));
                                                                    finish();
                                                                } else if (currentUser.getUserType().equals(UserType.STUDENT) ||
                                                                        currentUser.getUserType().equals(UserType.PARENT)){
                                                                    startActivity(new Intent(EditProfileActivity.this, StudentHomeActivity.class)
                                                                            .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser)));
                                                                    finish();
                                                                } else {
                                                                    new AlertDialog.Builder(EditProfileActivity.this, R.style.CustomDialogTheme)
                                                                            .setIcon(R.drawable.ic_error_outline)
                                                                            .setTitle("Error")
                                                                            .setMessage("User type not defined!\nPlease contact system admin to get this fixed.")
                                                                            .setPositiveButton("Ok", (dialog1, which1) -> finish())
                                                                            .show();
                                                                }
                                                            })
                                                            .show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    progressDialog.dismiss();
                                                    new AlertDialog.Builder(EditProfileActivity.this, R.style.CustomDialogTheme)
                                                            .setIcon(R.drawable.ic_error_outline)
                                                            .setTitle("Error")
                                                            .setMessage(e.getMessage())
                                                            .setPositiveButton("Ok", null)
                                                            .show();
                                                }))
                                        .addOnFailureListener(e -> {
                                            progressDialog.dismiss();
                                            new AlertDialog.Builder(EditProfileActivity.this, R.style.CustomDialogTheme)
                                                    .setIcon(R.drawable.ic_error_outline)
                                                    .setTitle("Error")
                                                    .setMessage(e.getMessage())
                                                    .setPositiveButton("Ok", null)
                                                    .show();
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                new AlertDialog.Builder(EditProfileActivity.this, R.style.CustomDialogTheme)
                                        .setIcon(R.drawable.ic_error_outline)
                                        .setTitle("Error")
                                        .setMessage(e.getMessage())
                                        .setPositiveButton("Ok", null)
                                        .show();
                            }
                        });
            } else {
                profileUser = tempUser;
                profileUser.setDob(date);

                FirebaseFirestore.getInstance().collection(CollectionName.USERS)
                        .document(profileUser.getId())
                        .set(profileUser)
                        .addOnSuccessListener(aVoid1 -> {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(EditProfileActivity.this, R.style.CustomDialogTheme)
                                    .setIcon(R.drawable.ic_success)
                                    .setTitle("Success")
                                    .setMessage("Profile updated!")
                                    .setPositiveButton("Ok", (dialog, which) -> {
                                        if (currentUser.getUserType().equals(UserType.ADMIN)){
                                            startActivity(new Intent(EditProfileActivity.this, AdminHomeActivity.class)
                                                    .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser)));
                                            finish();
                                        } else if (currentUser.getUserType().equals(UserType.TEACHER)){
                                            startActivity(new Intent(EditProfileActivity.this, TeacherHomeActivity.class)
                                                    .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser)));
                                            finish();
                                        } else if (currentUser.getUserType().equals(UserType.STUDENT) ||
                                                currentUser.getUserType().equals(UserType.PARENT)){
                                            startActivity(new Intent(EditProfileActivity.this, StudentHomeActivity.class)
                                                    .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser)));
                                            finish();
                                        } else {
                                            new AlertDialog.Builder(EditProfileActivity.this, R.style.CustomDialogTheme)
                                                    .setIcon(R.drawable.ic_error_outline)
                                                    .setTitle("Error")
                                                    .setMessage("User type not defined!\nPlease contact system admin to get this fixed.")
                                                    .setPositiveButton("Ok", (dialog1, which1) -> finish())
                                                    .show();
                                        }
                                    })
                                    .show();
                        })
                        .addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(EditProfileActivity.this, R.style.CustomDialogTheme)
                                    .setIcon(R.drawable.ic_error_outline)
                                    .setTitle("Error")
                                    .setMessage(e.getMessage())
                                    .setPositiveButton("Ok", null)
                                    .show();
                        });
            }
        });


        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCal.set(Calendar.YEAR, year);
                myCal.set(Calendar.YEAR, year);
                myCal.set(Calendar.YEAR, year);
                upDateLabel(myCal.getTime());
            }
        };

        btnDOB = findViewById(R.id.btnDOB);
        View.OnClickListener onClickListener = v -> new DatePickerDialog(EditProfileActivity.this, dateSetListener, myCal.get(Calendar.YEAR), myCal.get(Calendar.MONTH), myCal.get(Calendar.DAY_OF_MONTH)).show();
        btnDOB.setOnClickListener(onClickListener);
        Objects.requireNonNull(dob.getEditText()).setOnClickListener(onClickListener);
    }

    final Calendar myCal = Calendar.getInstance();
    private void upDateLabel(Date tempDate) {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        date = tempDate;

        Objects.requireNonNull(dob.getEditText()).setText(sdf.format(date.getTime()));
    }

    private void displayUserInfo() {
        password = findViewById(R.id.txtGrade);
        password.setVisibility(View.GONE);

        selectedList = findViewById(R.id.txtSelectedList);
        selectedList.setVisibility(View.GONE);

        lblSelected = findViewById(R.id.lblSelectedSubjects);
        lblSelected.setVisibility(View.GONE);

        subjectSelect = findViewById(R.id.btnAddSubject);
        subjectSelect.setVisibility(View.GONE);

        email = findViewById(R.id.txtEmail);
        fname = findViewById(R.id.txtClass);
        oname = findViewById(R.id.txtOName);
        sname = findViewById(R.id.txtSName);
        dname = findViewById(R.id.txtDName);
        dob = findViewById(R.id.txtDOB);
        phone = findViewById(R.id.txtPhone);
        address = findViewById(R.id.txtAddress);

        gender = findViewById(R.id.spGender);

        ArrayList<String> genderList = new ArrayList<>();

        genderList.add("Pick a gender");
        genderList.add(Gender.MALE.name());
        genderList.add(Gender.FEMALE.name());

        if (!genderList.isEmpty()){
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            gender.setAdapter(adapter);
        }

        if (!profileUser.getEmail().isEmpty())
            Objects.requireNonNull(email.getEditText()).setText(profileUser.getEmail());

        if ((!profileUser.getFirstName().isEmpty()))
            Objects.requireNonNull(fname.getEditText()).setText(profileUser.getFirstName());

        if (!profileUser.getOtherNames().isEmpty())
            Objects.requireNonNull(oname.getEditText()).setText(profileUser.getOtherNames());

        if (!profileUser.getSurname().isEmpty())
            Objects.requireNonNull(sname.getEditText()).setText(profileUser.getSurname());

        if (!profileUser.getDisplayName().isEmpty())
            Objects.requireNonNull(dname.getEditText()).setText(profileUser.getDisplayName());

        if (profileUser.getDob() != null)
            upDateLabel(profileUser.getDob());

        if (!profileUser.getPhone().isEmpty())
            Objects.requireNonNull(phone.getEditText()).setText(profileUser.getPhone());

        if (!profileUser.getAddress().isEmpty())
            Objects.requireNonNull(address.getEditText()).setText(profileUser.getAddress());

        if (profileUser.getGender() != null)
            if (!profileUser.getGender().equals(Gender.MALE)) {
                if (profileUser.getGender().equals(Gender.FEMALE))
                    gender.setSelection(2);
            } else {
                gender.setSelection(1);
            }
    }
}
