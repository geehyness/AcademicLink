package com.yukicide.theacademiclinkandroid.AppUI.globalUI.user_management;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.AppUI.adminUI.subjectsCRUD.MandatorySubjectsActivity;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;
import com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking.SubjectModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.AdminModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.ClassModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.ParentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.StudentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.TeacherModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.UserModel;
import com.yukicide.theacademiclinkandroid.Repositories.UIElements.MyProgressDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ViewUserActivity extends AppCompatActivity {
    UserModel profileUser, currentUser;
    MyProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        ImageView btnEdit = findViewById(R.id.btnEdit);
        TextView logout = findViewById(R.id.btnLogout);
        btnEdit.setVisibility(View.GONE);
        logout.setVisibility(View.GONE);

        profileUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.PROFILE_USER), UserModel.class);
        currentUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), UserModel.class);

        if (profileUser == null || currentUser == null)
            new AlertDialog.Builder(ViewUserActivity.this, R.style.CustomDialogTheme)
                    .setIcon(R.drawable.ic_error_outline)
                    .setTitle("Error")
                    .setMessage("Unable to load user information!")
                    .setCancelable(false)
                    .setPositiveButton("Ok", (dialog, which) -> {
                        finish();
                    })
                    .show();

        if (currentUser.getId().equals(profileUser.getId())) {
            btnEdit.setVisibility(View.VISIBLE);
            logout.setVisibility(View.VISIBLE);
        }

        if (currentUser.getUserType().equals(UserType.ADMIN))
            btnEdit.setVisibility(View.VISIBLE);

        btnEdit.setOnClickListener(v -> {
            //Toast.makeText(this, currentUser.getId() + "\n" + profileUser.getId(), Toast.LENGTH_SHORT).show();
            if (currentUser.getId().equals(profileUser.getId())) {
                startActivity(new Intent(this, EditProfileActivity.class)
                        .putExtra(StringExtras.PROFILE_USER, (new Gson()).toJson(profileUser))
                        .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser)));
            } else if (currentUser.getUserType().equals(UserType.ADMIN)) {
                if (!profileUser.getUserType().equals(UserType.ADMIN))
                    startActivity(new Intent(this, EditProfileActivity.class)
                            .putExtra(StringExtras.PROFILE_USER, (new Gson()).toJson(profileUser))
                            .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser)));
            }
        });

        logout.setOnClickListener(v -> new AlertDialog.Builder(ViewUserActivity.this, R.style.CustomDialogTheme)
                .setIcon(R.drawable.ic_error_outline)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton("No", null)
                .show());


        Log.d("ids", profileUser.getId() + " - "  + currentUser.getId());

        if (profileUser == null) {
            Toast.makeText(this, "Error Loading!", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (profileUser.getUserType().equals(UserType.PARENT))
            profileUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.PROFILE_USER), ParentModel.class);
        else if (profileUser.getUserType().equals(UserType.STUDENT))
            profileUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.PROFILE_USER), StudentModel.class);
        else if (profileUser.getUserType().equals(UserType.TEACHER))
            profileUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.PROFILE_USER), TeacherModel.class);
        else if (profileUser.getUserType().equals(UserType.ADMIN))
            profileUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.PROFILE_USER), AdminModel.class);


        displayUserInfo();
    }

    private void displayUserInfo() {
        TextView name = findViewById(R.id.txtName),
                dob = findViewById(R.id.txtDOB),
                gender = findViewById(R.id.txtGender),
                userType = findViewById(R.id.txtUserType),
                child = findViewById(R.id.txtChild),
                lblChild = findViewById(R.id.lblChild),
                address = findViewById(R.id.txtAddress),
                phone = findViewById(R.id.txtPhone),
                email = findViewById(R.id.txtEmail),
                grade = findViewById(R.id.txtGrade),
                lblGrade = findViewById(R.id.lblGrade),
                classroom = findViewById(R.id.txtClass),
                lblClass = findViewById(R.id.lblClass),
                subjects = findViewById(R.id.txtSubjects),
                lblSubjects = findViewById(R.id.lblSubjects);

        ImageView btnEditClass = findViewById(R.id.btnEditClass),
                btnEditSubjects = findViewById(R.id.btnEditSubjects);

        lblChild.setVisibility(View.GONE);
        child.setVisibility(View.GONE);
        lblGrade.setVisibility(View.GONE);
        grade.setVisibility(View.GONE);
        lblClass.setVisibility(View.GONE);
        classroom.setVisibility(View.GONE);
        lblSubjects.setVisibility(View.GONE);
        subjects.setVisibility(View.GONE);
        btnEditClass.setVisibility(View.GONE);
        btnEditSubjects.setVisibility(View.GONE);

        if (profileUser.getUserType().equals(UserType.PARENT)) {
            lblChild.setVisibility(View.VISIBLE);
            child.setVisibility(View.VISIBLE);

            if (((ParentModel)profileUser).getChildId() != null) {
                FirebaseFirestore.getInstance().collection(CollectionName.USERS).document(((ParentModel) profileUser).getChildId())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            StudentModel studentModel =documentSnapshot.toObject(StudentModel.class);
                            assert studentModel != null;
                            if (studentModel.getFirstName() != null && studentModel.getSurname() != null)
                                child.setText(String.format("%s %s", studentModel.getFirstName(), studentModel.getSurname()));

                            lblGrade.setVisibility(View.VISIBLE);
                            grade.setVisibility(View.VISIBLE);
                            lblClass.setVisibility(View.VISIBLE);
                            classroom.setVisibility(View.VISIBLE);
                            lblSubjects.setVisibility(View.VISIBLE);
                            subjects.setVisibility(View.VISIBLE);

                            grade.setText(R.string.loading);
                            classroom.setText(R.string.loading);
                            subjects.setText(R.string.loading);

                            getClassInfo(studentModel, grade, classroom, subjects);
                        })
                        .addOnFailureListener(e -> new AlertDialog.Builder(ViewUserActivity.this, R.style.CustomDialogTheme)
                                .setIcon(R.drawable.ic_error_outline)
                                .setTitle("Error")
                                .setMessage("Unable to get child info\n" + e.getMessage())
                                .setPositiveButton("Ok", null)
                                .show());
            }
        }

        if (profileUser.getUserType().equals(UserType.STUDENT)) {
            lblGrade.setVisibility(View.VISIBLE);
            grade.setVisibility(View.VISIBLE);
            lblClass.setVisibility(View.VISIBLE);
            classroom.setVisibility(View.VISIBLE);
            lblSubjects.setVisibility(View.VISIBLE);
            subjects.setVisibility(View.VISIBLE);

            if (currentUser.getUserType().equals(UserType.ADMIN)) {
                btnEditClass.setVisibility(View.VISIBLE);
                btnEditSubjects.setVisibility(View.VISIBLE);
            }

            grade.setText(R.string.loading);
            classroom.setText(R.string.loading);
            subjects.setText(R.string.loading);

            getClassInfo((StudentModel) profileUser, grade, classroom, subjects);
        }

        if (profileUser.getUserType().equals(UserType.TEACHER)) {
            lblClass.setVisibility(View.VISIBLE);
            classroom.setVisibility(View.VISIBLE);
            lblSubjects.setVisibility(View.VISIBLE);
            subjects.setVisibility(View.VISIBLE);

            if (currentUser.getUserType().equals(UserType.ADMIN)) {
                btnEditClass.setVisibility(View.VISIBLE);
                btnEditSubjects.setVisibility(View.VISIBLE);
            }

            getTeacherInfo((TeacherModel) profileUser, classroom, subjects);
        }

        if (profileUser.getFirstName() != null && profileUser.getOtherNames() != null && profileUser.getSurname() != null)
            name.setText(String.format("%s %s %s", profileUser.getFirstName(), profileUser.getOtherNames(), profileUser.getSurname()));
        else if (profileUser.getFirstName() != null && profileUser.getSurname() != null)
            name.setText(String.format("%s %s", profileUser.getFirstName(), profileUser.getSurname()));
        else if (profileUser.getFirstName() != null)
            name.setText(profileUser.getFirstName());
        else
            name.setText(R.string.profile_unavailable);

        if (profileUser.getDob() != null) {
            String myFormat = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

            dob.setText(sdf.format(profileUser.getDob()));
        } else
            dob.setText(R.string.profile_unavailable);

        if (profileUser.getGender() != null)
            gender.setText(profileUser.getGender().name());
        else
            gender.setText(R.string.profile_unavailable);

        userType.setText(profileUser.getUserType().name());

        if (profileUser.getAddress() != null)
            address.setText(profileUser.getAddress());
        else
            address.setText(R.string.profile_unavailable);

        if (profileUser.getPhone() != null)
            phone.setText(profileUser.getPhone());
        else
            phone.setText(R.string.profile_unavailable);

        if (profileUser.getEmail() != null)
            email.setText(profileUser.getEmail());
        else
            email.setText(R.string.profile_unavailable);

        btnEditClass.setOnClickListener(v -> {
            ArrayList<ClassModel> classList = new ArrayList<>();
            final MyProgressDialog progressDialog = new MyProgressDialog(ViewUserActivity.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            FirebaseFirestore.getInstance().collection(CollectionName.CLASS)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots != null) {
                            for (DocumentSnapshot d : queryDocumentSnapshots) {
                                ClassModel classModel = d.toObject(ClassModel.class);
                                assert classModel != null;
                                classModel.setId(d.getId());
                                classList.add(classModel);
                            }

                            if (!(classList.size() == 0)) {
                                progressDialog.dismiss();
                                setClass(classList);
                            } else {
                                progressDialog.dismiss();
                                new AlertDialog.Builder(ViewUserActivity.this, R.style.CustomDialogTheme)
                                        .setIcon(R.drawable.ic_error_outline)
                                        .setTitle("Error")
                                        .setMessage("Unable to load classes!")
                                        .setPositiveButton("Ok", null)
                                        .show();
                            }
                        }
                    })
                    .addOnFailureListener(e -> new AlertDialog.Builder(ViewUserActivity.this, R.style.CustomDialogTheme)
                            .setIcon(R.drawable.ic_error_outline)
                            .setTitle("Error")
                            .setMessage(e.getMessage())
                            .setPositiveButton("Ok", null)
                            .show());
        });

        btnEditSubjects.setOnClickListener(v -> {
            // TODO: 2020/04/24 Assign teachers to subjects

            if (profileUser.getUserType().equals(UserType.STUDENT))
                startActivity(new Intent(this, MandatorySubjectsActivity.class)
                    .putExtra(StringExtras.PROFILE_USER, (new Gson()).toJson((StudentModel) profileUser))
                    .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser))
                    .putExtra(StringExtras.ASSIGN_SUBJECT, (new Gson()).toJson(true)));
            else
                startActivity(new Intent(this, MandatorySubjectsActivity.class)
                        .putExtra(StringExtras.PROFILE_USER, (new Gson()).toJson((TeacherModel) profileUser))
                        .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser))
                        .putExtra(StringExtras.ASSIGN_SUBJECT, (new Gson()).toJson(true)));

        });
    }

    private void setClass(ArrayList<ClassModel> classList) {
        final Spinner spClasses = new Spinner(this);

        ArrayList<String> classesNames = new ArrayList<>();
        classesNames.add("Pick a class");
        for (ClassModel t : classList)
            classesNames.add(t.getName());

        if (!classesNames.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classesNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spClasses.setAdapter(adapter);
        }

        new AlertDialog.Builder(this)
                .setTitle("Assign A Class")
                .setMessage("Select the class to assign to " + profileUser.getFirstName() + " " + profileUser.getSurname())
                .setView(spClasses)
                .setCancelable(false)
                .setPositiveButton("Assign", (dialog, whichButton) -> {
                    if (!(spClasses.getSelectedItemPosition() <= 0)) {
                        progressDialog = new MyProgressDialog(this);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        ClassModel classModel = classList.get(spClasses.getSelectedItemPosition() - 1);

                        if (profileUser.getUserType().equals(UserType.TEACHER)) {
                            FirebaseFirestore.getInstance().collection(CollectionName.USERS)
                                    .whereEqualTo("userType", UserType.TEACHER.name())
                                    .whereEqualTo("classId", classModel.getId())
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            TeacherModel oldTeacher = queryDocumentSnapshots.getDocuments().get(0).toObject(TeacherModel.class);
                                            updateClassTeacher(oldTeacher, profileUser, classModel);
                                        } else {
                                            updateClassTeacher(null, profileUser, classModel);
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        new AlertDialog.Builder(ViewUserActivity.this, R.style.CustomDialogTheme)
                                            .setIcon(R.drawable.ic_error_outline)
                                            .setTitle("Error")
                                            .setMessage("Unable to update Class Teacher!\n" + e.getMessage())
                                            .setPositiveButton("Ok", null)
                                            .show();
                                        progressDialog.dismiss();
                                    });
                        } else if (profileUser.getUserType().equals(UserType.STUDENT)) {
                            ClassModel oldClass = null;
                            if (((StudentModel) profileUser).getClassId() != null || !((StudentModel) profileUser).getClassId().isEmpty()) {
                                for (ClassModel c : classList) {
                                    if (c.getId().equals(((StudentModel) profileUser).getClassId())) {
                                        oldClass = c;
                                        break;
                                    }
                                }
                            }

                            if (oldClass != null) {
                                if (oldClass.getGrade() == classModel.getGrade()) {
                                    for (SubjectModel s : ((StudentModel) profileUser).getSubjects()) {
                                        if (s.getClassId() != null) {
                                            s.setClassId(classModel.getId());
                                        }
                                    }

                                    ((StudentModel) profileUser).setClassId(classModel.getId());
                                    FirebaseFirestore.getInstance().collection(CollectionName.USERS)
                                            .document(profileUser.getId())
                                            .set(profileUser)
                                            .addOnSuccessListener(aVoid -> {
                                                new AlertDialog.Builder(ViewUserActivity.this, R.style.CustomDialogTheme)
                                                        .setIcon(R.drawable.ic_success)
                                                        .setTitle("Success")
                                                        .setMessage(profileUser.getFirstName() + " " + profileUser.getSurname() + " added to " + classModel.getName())
                                                        .setPositiveButton("Ok", null)
                                                        .show();
                                                progressDialog.dismiss();
                                            })
                                            .addOnFailureListener(e -> {
                                                new AlertDialog.Builder(ViewUserActivity.this, R.style.CustomDialogTheme)
                                                        .setIcon(R.drawable.ic_error_outline)
                                                        .setTitle("Error")
                                                        .setMessage(e.getMessage())
                                                        .setPositiveButton("Ok", null)
                                                        .show();
                                                progressDialog.dismiss();
                                            });
                                } else {
                                    new AlertDialog.Builder(ViewUserActivity.this, R.style.CustomDialogTheme)
                                            .setIcon(R.drawable.ic_info)
                                            .setTitle("Warning")
                                            .setMessage("Moving a student to another grade will reset their subjects and marks!\n" +
                                                    "Do you wish to continue?")
                                            .setPositiveButton("Yes", (dialog12, which) -> {
                                                // TODO: 2020/04/03 SAVE OLD SUBJECTS AND MARKS

                                                ((StudentModel) profileUser).setClassId(classModel.getId());
                                                ((StudentModel) profileUser).setSubjects(null);
                                                FirebaseFirestore.getInstance().collection(CollectionName.USERS)
                                                        .document(profileUser.getId())
                                                        .set(profileUser)
                                                        .addOnSuccessListener(aVoid -> new AlertDialog.Builder(ViewUserActivity.this, R.style.CustomDialogTheme)
                                                                .setIcon(R.drawable.ic_success)
                                                                .setTitle("Success")
                                                                .setMessage(profileUser.getFirstName() + " " + profileUser.getSurname() + " added to " + classModel.getName())
                                                                .setPositiveButton("Ok", null)
                                                                .show())
                                                        .addOnFailureListener(e -> new AlertDialog.Builder(ViewUserActivity.this, R.style.CustomDialogTheme)
                                                                .setIcon(R.drawable.ic_error_outline)
                                                                .setTitle("Error")
                                                                .setMessage(e.getMessage())
                                                                .setPositiveButton("Ok", null)
                                                                .show());
                                            })
                                            .setNegativeButton("No", null)
                                            .show();
                                    progressDialog.dismiss();

                                }
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateClassTeacher(TeacherModel oldTeacher, UserModel profileUser, ClassModel classModel) {
        ((TeacherModel) profileUser).setClassId(classModel.getId());
        if (oldTeacher == null) {
            FirebaseFirestore.getInstance().collection(CollectionName.USERS)
                    .document(profileUser.getId())
                    .set(profileUser)
                    .addOnSuccessListener(aVoid -> {
                        new AlertDialog.Builder(ViewUserActivity.this, R.style.CustomDialogTheme)
                                .setIcon(R.drawable.ic_success)
                                .setTitle("Success")
                                .setMessage(profileUser.getFirstName() + " " + profileUser.getSurname() + " assigned to " + classModel.getName())
                                .setPositiveButton("Ok", null)
                                .show();
                        progressDialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        new AlertDialog.Builder(ViewUserActivity.this, R.style.CustomDialogTheme)
                                .setIcon(R.drawable.ic_error_outline)
                                .setTitle("Error")
                                .setMessage("Unable to update Class Teacher!\n" + e.getMessage())
                                .setPositiveButton("Ok", null)
                                .show();
                        progressDialog.dismiss();
                    });
        } else {
            oldTeacher.setClassId("");
            FirebaseFirestore.getInstance().collection(CollectionName.USERS)
                    .document(oldTeacher.getId())
                    .set(oldTeacher)
                    .addOnSuccessListener(aVoid -> FirebaseFirestore.getInstance().collection(CollectionName.USERS)
                            .document(profileUser.getId())
                            .set(profileUser)
                            .addOnSuccessListener(aVoid1 -> {
                                new AlertDialog.Builder(ViewUserActivity.this, R.style.CustomDialogTheme)
                                        .setIcon(R.drawable.ic_success)
                                        .setTitle("Success")
                                        .setMessage(profileUser.getFirstName() + " " + profileUser.getSurname() + " assigned to " + classModel.getName())
                                        .setPositiveButton("Ok", null)
                                        .show();
                                progressDialog.dismiss();
                            })
                            .addOnFailureListener(e -> {
                                new AlertDialog.Builder(ViewUserActivity.this, R.style.CustomDialogTheme)
                                        .setIcon(R.drawable.ic_error_outline)
                                        .setTitle("Error")
                                        .setMessage("Unable to update Class Teacher!\n" + e.getMessage())
                                        .setPositiveButton("Ok", null)
                                        .show();
                                progressDialog.dismiss();
                            }))
                    .addOnFailureListener(e -> {
                        new AlertDialog.Builder(ViewUserActivity.this, R.style.CustomDialogTheme)
                                .setIcon(R.drawable.ic_error_outline)
                                .setTitle("Error")
                                .setMessage("Unable to update Class Teacher!\n" + e.getMessage())
                                .setPositiveButton("Ok", null)
                                .show();
                        progressDialog.dismiss();
                    });
        }
    }

    private void getTeacherInfo(TeacherModel teacher, TextView classroom, TextView subjects) {
        if (teacher.getClassId() != null) {
            FirebaseFirestore.getInstance().collection(CollectionName.CLASS).document(teacher.getClassId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        ClassModel classModel = documentSnapshot.toObject(ClassModel.class);
                        assert classModel != null;
                        classroom.setText(classModel.getName());
                    })
                    .addOnFailureListener(e -> new AlertDialog.Builder(ViewUserActivity.this, R.style.CustomDialogTheme)
                            .setIcon(R.drawable.ic_error_outline)
                            .setTitle("Error")
                            .setMessage("Unable to get class info\n" + e.getMessage())
                            .setPositiveButton("Ok", null)
                            .show());
        }

        if (!teacher.getSubjects().isEmpty()) {
            subjects.setText("");
            FirebaseFirestore.getInstance().collection(CollectionName.SUBJECTS)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            SubjectModel subjectModel = documentSnapshot.toObject(SubjectModel.class);
                            assert subjectModel != null;
                            subjectModel.setId(documentSnapshot.getId());
                            for (String id : teacher.getSubjects()) {
                                if (id.equals(subjectModel.getId())) {
                                    if (subjectModel.getClassId() != null)
                                        subjects.append(subjectModel.getName() + " (Grade " + subjectModel.getClassId() + ")\n");
                                    else
                                        subjects.append(subjectModel.getName() + " (Grade " + subjectModel.getGrade() + ")\n");

                                    break;
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> new AlertDialog.Builder(ViewUserActivity.this, R.style.CustomDialogTheme)
                            .setIcon(R.drawable.ic_error_outline)
                            .setTitle("Error")
                            .setMessage("Unable to get class info\n" + e.getMessage())
                            .setPositiveButton("Ok", null)
                            .show());
        }
    }

    private void getClassInfo(StudentModel student, TextView grade, TextView classroom, TextView subjects) {
        if (student.getClassId() != null) {
            FirebaseFirestore.getInstance().collection(CollectionName.CLASS).document(((StudentModel) profileUser).getClassId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        ClassModel classModel = documentSnapshot.toObject(ClassModel.class);
                        assert classModel != null;
                        grade.setText(String.valueOf(classModel.getGrade()));
                        classroom.setText(classModel.getName());
                    })
                    .addOnFailureListener(e -> new AlertDialog.Builder(ViewUserActivity.this, R.style.CustomDialogTheme)
                            .setIcon(R.drawable.ic_error_outline)
                            .setTitle("Error")
                            .setMessage("Unable to get class info\n" + e.getMessage())
                            .setPositiveButton("Ok", null)
                            .show());
        }

        if (student.getSubjects() != null) {
            subjects.setText("");

            for (SubjectModel s : student.getSubjects()) {
                subjects.append(s.getName() + "\n");
            }
        } else {
            subjects.setText(String.valueOf(R.string.error_subject_list));
        }
    }
}
