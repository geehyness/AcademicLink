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
import com.google.firebase.firestore.SetOptions;
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













        /*new AlertDialog.Builder(EditProfileActivity.this, R.style.CustomDialogTheme)
                .setIcon(R.drawable.ic_info)
                .setTitle("Warning")
                .setMessage("This interface is under construction!")
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
                .show();*/











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
        } else if (!profileUser.isNewUser()) {
            date = profileUser.getDob();
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
            tempUser.setOtherNames(oname.getEditText().getText().toString());
            tempUser.setDisplayName(dname.getEditText().getText().toString());
            tempUser.setPhone(phone.getEditText().getText().toString());
            tempUser.setAddress(address.getEditText().getText().toString());

            progressDialog = new MyProgressDialog(EditProfileActivity.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();

            if ((currentUser.getId() == profileUser.getId()) && profileUser.isNewUser()) {
                if (Objects.requireNonNull(password.getEditText()).getText().toString().isEmpty()) {
                    password.getEditText().setError("Password cannot be empty!");
                    password.getEditText().requestFocus();
                    return;
                }

                FirebaseAuth.getInstance().signInWithEmailAndPassword(currentUser.getEmail(), currentUser.getEmail())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                String pass = Objects.requireNonNull(password.getEditText()).getText().toString();
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                assert user != null;
                                user.updatePassword(pass)
                                        .addOnSuccessListener(aVoid -> saveUser(tempUser))
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
            }
            else {
                /*profileUser = tempUser;
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
                        });*/
                saveUser(tempUser);
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

    private void saveUser(UserModel userModel) {
        FirebaseFirestore.getInstance().collection(CollectionName.USERS)
                .document(userModel.getId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null) {
                            /*if (userModel.getUserType().equals(UserType.STUDENT)) {
                                studentModel = documentSnapshot.toObject(StudentModel.class);
                                studentModel.setId(documentSnapshot.getId());

                                Toast.makeText(EditProfileActivity.this, studentModel.getClassId(), Toast.LENGTH_LONG).show();

                                studentModel.setFirstName(userModel.getFirstName());
                                studentModel.setOtherNames(userModel.getOtherNames());
                                studentModel.setSurname(userModel.getSurname());
                                studentModel.setDisplayName(userModel.getDisplayName());
                                studentModel.setDob(userModel.getDob());

                                // TODO: 2020/06/10 GET GENDER

                                studentModel.setAddress(userModel.getAddress());
                                studentModel.setPhone(userModel.getPhone());
                                studentModel.setDob(userModel.getDob());

                                studentModel.setNewUser(false);

                                FirebaseFirestore.getInstance().collection(CollectionName.USERS)
                                        .document(studentModel.getId())
                                        .set(studentModel, SetOptions.merge())
                                        .addOnSuccessListener(aVoid -> finishProfileUpdate())
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
                            else if (userModel.getUserType().equals(UserType.PARENT)) {
                                parentModel = documentSnapshot.toObject(ParentModel.class);

                                parentModel = documentSnapshot.toObject(ParentModel.class);
                                parentModel.setId(documentSnapshot.getId());

                                parentModel.setFirstName(userModel.getFirstName());
                                parentModel.setOtherNames(userModel.getOtherNames());
                                parentModel.setSurname(userModel.getSurname());
                                parentModel.setDisplayName(userModel.getDisplayName());
                                parentModel.setDob(userModel.getDob());

                                // TODO: 2020/06/10 GET GENDER

                                parentModel.setAddress(userModel.getAddress());
                                parentModel.setPhone(userModel.getPhone());
                                parentModel.setDob(userModel.getDob());

                                parentModel.setNewUser(userModel.isNewUser());

                                FirebaseFirestore.getInstance().collection(CollectionName.USERS)
                                        .document(parentModel.getId())
                                        .set(parentModel)
                                        .addOnSuccessListener(aVoid -> finishProfileUpdate())
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
                            else if (userModel.getUserType().equals(UserType.ADMIN)) {
                                adminModel = documentSnapshot.toObject(AdminModel.class);

                                adminModel = documentSnapshot.toObject(AdminModel.class);
                                adminModel.setId(documentSnapshot.getId());

                                adminModel.setFirstName(userModel.getFirstName());
                                adminModel.setOtherNames(userModel.getOtherNames());
                                adminModel.setSurname(userModel.getSurname());
                                adminModel.setDisplayName(userModel.getDisplayName());
                                adminModel.setDob(userModel.getDob());

                                // TODO: 2020/06/10 GET GENDER

                                adminModel.setAddress(userModel.getAddress());
                                adminModel.setPhone(userModel.getPhone());
                                adminModel.setDob(userModel.getDob());

                                adminModel.setNewUser(userModel.isNewUser());

                                FirebaseFirestore.getInstance().collection(CollectionName.USERS)
                                        .document(adminModel.getId())
                                        .set(adminModel)
                                        .addOnSuccessListener(aVoid -> finishProfileUpdate())
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
                            else if (userModel.getUserType().equals(UserType.TEACHER)) {
                                teacherModel = documentSnapshot.toObject(TeacherModel.class);

                                teacherModel = documentSnapshot.toObject(TeacherModel.class);
                                teacherModel.setId(documentSnapshot.getId());

                                teacherModel.setFirstName(userModel.getFirstName());
                                teacherModel.setOtherNames(userModel.getOtherNames());
                                teacherModel.setSurname(userModel.getSurname());
                                teacherModel.setDisplayName(userModel.getDisplayName());
                                teacherModel.setDob(userModel.getDob());

                                // TODO: 2020/06/10 GET GENDER

                                teacherModel.setAddress(userModel.getAddress());
                                teacherModel.setPhone(userModel.getPhone());
                                teacherModel.setDob(userModel.getDob());

                                teacherModel.setNewUser(userModel.isNewUser());

                                FirebaseFirestore.getInstance().collection(CollectionName.USERS)
                                        .document(teacherModel.getId())
                                        .set(teacherModel)
                                        .addOnSuccessListener(aVoid -> finishProfileUpdate())
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
                            else {
                                progressDialog.dismiss();
                                new AlertDialog.Builder(EditProfileActivity.this, R.style.CustomDialogTheme)
                                        .setIcon(R.drawable.ic_error_outline)
                                        .setTitle("Error")
                                        .setMessage("Unable to get your user-type!\nContact system admin to get this rectified.")
                                        .setPositiveButton("Ok", null)
                                        .show();
                            }*/



                            FirebaseFirestore.getInstance().collection(CollectionName.USERS)
                                    .document(profileUser.getId())
                                    .set(userModel, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> finishProfileUpdate())
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
    }

    private void finishProfileUpdate() {
        progressDialog.dismiss();

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
