package com.yukicide.theacademiclinkandroid.AppUI.adminUI.classCRUD;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.AppUI.adminUI.studentCRUD.AddBatchStudentsActivity;
import com.yukicide.theacademiclinkandroid.AppUI.adminUI.studentCRUD.AddStudentActivity;
import com.yukicide.theacademiclinkandroid.AppUI.globalUI.user_management.ViewUserActivity;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Adapters.UserAdapter;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.ClassModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.StudentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.TeacherModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.UserModel;
import com.yukicide.theacademiclinkandroid.Repositories.UIElements.MyProgressDialog;

import java.util.ArrayList;

import static com.yukicide.theacademiclinkandroid.R.string.loading;
import static com.yukicide.theacademiclinkandroid.R.string.teacher_404;

public class ViewClassActivity extends AppCompatActivity {
    UserModel currentUser;
    TeacherModel classTeacher = null;

    private UserAdapter userAdapter;

    private ClassModel selectedClass = null;
    private ArrayList<StudentModel> studentList = new ArrayList<>();
    private ArrayList<UserModel> displayStudentList = new ArrayList<>();

    private ArrayList<TeacherModel> teachersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_class);

        FloatingActionButton manage = findViewById(R.id.btnManage);
        manage.setVisibility(View.GONE);

        selectedClass = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CLASS), ClassModel.class);
        if (selectedClass == null) {
            new AlertDialog.Builder(ViewClassActivity.this, R.style.CustomDialogTheme)
                    .setIcon(R.drawable.ic_error_outline)
                    .setTitle("Error")
                    .setMessage("Unable to get Class Information!")
                    .setPositiveButton("Ok", null)
                    .show();
            finish();
        }
        currentUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), UserModel.class);

        if (currentUser.getUserType().equals(UserType.ADMIN)) {
            manage.setVisibility(View.VISIBLE);
        }

        if (currentUser.getUserType().equals(UserType.TEACHER)) {
            currentUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), TeacherModel.class);
            if (((TeacherModel) currentUser).getClassId().equals(selectedClass.getId())) {
                manage.setVisibility(View.VISIBLE);
            }
        }

        manage.setOnClickListener(this::showMenu);

        displayClassInfo();
        initStudentRecycler();

        FirebaseFirestore.getInstance().collection(CollectionName.USERS)
                .whereEqualTo("userType", UserType.STUDENT)
                .whereEqualTo("classId", selectedClass.getId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot d : queryDocumentSnapshots) {
                        StudentModel studentModel = d.toObject(StudentModel.class);
                        assert studentModel != null;
                        studentModel.setId(d.getId());
                        if (studentModel.getUserType().equals(UserType.STUDENT))
                            studentList.add(studentModel);
                    }
                    // TODO: 2020/02/22 SORT BY NAME
                    displayStudentList.addAll(studentList);
                    userAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> new AlertDialog.Builder(ViewClassActivity.this, R.style.CustomDialogTheme)
                        .setIcon(R.drawable.ic_error_outline)
                        .setTitle("Error")
                        .setMessage(e.getMessage())
                        .setPositiveButton("Ok", null)
                        .show());

        ImageView back = findViewById(R.id.btnBack);
        back.setOnClickListener(v -> finish());
    }

    private void assignTeacher() {
        final Spinner spTeacher = new Spinner(this);

        ArrayList<String> teachers = new ArrayList<>();
        teachers.add("Pick a teacher");
        for (TeacherModel t : teachersList)
            teachers.add(t.getFirstName() + " " + t.getSurname());

        if (!teachers.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teachers);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spTeacher.setAdapter(adapter);
        }

        new AlertDialog.Builder(this)
                .setTitle("Assign Teacher")
                .setMessage("Select the teacher to assign " + selectedClass.getName() + " to:")
                .setView(spTeacher)
                .setCancelable(false)
                .setPositiveButton("Assign", (dialog, whichButton) -> {
                    if (!(spTeacher.getSelectedItemPosition() <= 0)) {
                        final MyProgressDialog progressDialog = new MyProgressDialog(this);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        TeacherModel selectedTeacher = teachersList.get(spTeacher.getSelectedItemPosition() - 1);

                        boolean replaced = false;

                        for (TeacherModel t : teachersList)
                            if (t.getClassId() != null)
                                if (t.getClassId().equals(selectedClass.getId())) {
                                    t.setClassId("");
                                    selectedTeacher.setClassId(selectedClass.getId());

                                    FirebaseFirestore.getInstance().collection(CollectionName.USERS)
                                            .document(t.getId())
                                            .set(t)
                                            .addOnSuccessListener(aVoid -> FirebaseFirestore.getInstance().collection(CollectionName.USERS)
                                                    .document(selectedTeacher.getId())
                                                    .set(selectedTeacher)
                                                    .addOnSuccessListener(aVoid2 -> {
                                                        new AlertDialog.Builder(ViewClassActivity.this, R.style.CustomDialogTheme)
                                                                .setIcon(R.drawable.ic_success)
                                                                .setTitle("Success")
                                                                .setMessage(selectedTeacher.getFirstName() + " " + selectedTeacher.getSurname() + " set as class teacher.")
                                                                .setPositiveButton("Ok", null)
                                                                .show();
                                                        progressDialog.dismiss();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        new AlertDialog.Builder(ViewClassActivity.this, R.style.CustomDialogTheme)
                                                                .setIcon(R.drawable.ic_error_outline)
                                                                .setTitle("Error")
                                                                .setMessage(e.getMessage())
                                                                .setPositiveButton("Ok", null)
                                                                .show();
                                                        progressDialog.dismiss();
                                                    }))
                                            .addOnFailureListener(e -> {
                                                new AlertDialog.Builder(ViewClassActivity.this, R.style.CustomDialogTheme)
                                                        .setIcon(R.drawable.ic_error_outline)
                                                        .setTitle("Error")
                                                        .setMessage(e.getMessage())
                                                        .setPositiveButton("Ok", null)
                                                        .show();
                                                progressDialog.dismiss();
                                            });
                                    replaced = true;
                                    break;
                                }

                        if (!replaced) {
                            selectedTeacher.setClassId(selectedClass.getId());
                            FirebaseFirestore.getInstance().collection(CollectionName.USERS)
                                    .document(selectedTeacher.getId())
                                    .set(selectedTeacher)
                                    .addOnSuccessListener(aVoid -> {
                                        new AlertDialog.Builder(ViewClassActivity.this, R.style.CustomDialogTheme)
                                                .setIcon(R.drawable.ic_success)
                                                .setTitle("Success")
                                                .setMessage(selectedTeacher.getFirstName() + " " + selectedTeacher.getSurname() + " set as class teacher.")
                                                .setPositiveButton("Ok", null)
                                                .show();
                                        progressDialog.dismiss();
                                    })
                                    .addOnFailureListener(e -> {
                                        new AlertDialog.Builder(ViewClassActivity.this, R.style.CustomDialogTheme)
                                                .setIcon(R.drawable.ic_error_outline)
                                                .setTitle("Error")
                                                .setMessage(e.getMessage())
                                                .setPositiveButton("Ok", null)
                                                .show();
                                        progressDialog.dismiss();
                                    });
                        }
                    }
                })
                .setNegativeButton("Cancel", (dialog, whichButton) -> {
                })
                .show();
    }

    private void displayClassInfo() {
        TextView txtClassName = findViewById(R.id.txtClassName),
                txtClassTeacher = findViewById(R.id.txtClassTeacher);

        txtClassName.setText(selectedClass.getName());
        txtClassTeacher.setText(loading);

        FirebaseFirestore.getInstance().collection(CollectionName.USERS)
                .whereEqualTo("userType", UserType.TEACHER)
                .whereEqualTo("classId", selectedClass.getId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot d : queryDocumentSnapshots) {
                        classTeacher = d.toObject(TeacherModel.class);
                    }

                    if (classTeacher == null)
                        txtClassTeacher.setText(teacher_404);
                    else
                        txtClassTeacher.setText(String.format("%s %s", classTeacher.getFirstName(), classTeacher.getSurname()));
                })
                .addOnFailureListener(e -> new AlertDialog.Builder(ViewClassActivity.this, R.style.CustomDialogTheme)
                            .setIcon(R.drawable.ic_error_outline)
                            .setTitle("Error")
                            .setMessage("Unable to get class teacher!\n\n" + e.getMessage())
                            .setPositiveButton("Ok", (dialog, which) -> txtClassTeacher.setText(teacher_404))
                            .show());
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.add_student){
                startActivity(new Intent(ViewClassActivity.this, AddStudentActivity.class)
                        .putExtra(StringExtras.CLASS_ID, selectedClass.getId()));
            } else if (item.getItemId() == R.id.add_batch){
                startActivity(new Intent(ViewClassActivity.this, AddBatchStudentsActivity.class)
                    .putExtra(StringExtras.CLASS_ID, selectedClass.getId()));
            } else if (item.getItemId() == R.id.assignTeacher){
                final MyProgressDialog progressDialog = new MyProgressDialog(this);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
                FirebaseFirestore.getInstance().collection(CollectionName.USERS)
                        .whereEqualTo("userType", UserType.TEACHER.name())
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (DocumentSnapshot d : queryDocumentSnapshots){
                                boolean exists = false;

                                for (UserModel u : teachersList) {
                                    if (d.getId().equals(u.getId())) {
                                        exists = true;
                                        break;
                                    }
                                }

                                if (!exists) {
                                    TeacherModel temp = d.toObject(TeacherModel.class);
                                    assert temp != null;
                                    temp.setId(d.getId());
                                    teachersList.add(temp);
                                }
                            }

                            progressDialog.dismiss();
                            assignTeacher();
                        })
                        .addOnFailureListener(e -> {
                            new AlertDialog.Builder(ViewClassActivity.this, R.style.CustomDialogTheme)
                                .setIcon(R.drawable.ic_error_outline)
                                .setTitle("Error")
                                .setMessage(e.getMessage())
                                .setPositiveButton("Ok", null)
                                .show();
                            progressDialog.dismiss();
                        });
            }

            return false;
        });

        if (currentUser.getUserType().equals(UserType.ADMIN))
            popup.inflate(R.menu.admin_manage_class_menu);
        else
            popup.inflate(R.menu.manage_student_menu);

        popup.show();
    }

    private void initStudentRecycler() {
        RecyclerView notificationsRecycler = findViewById(R.id.studentsRecycler);
        notificationsRecycler.setHasFixedSize(false);
        userAdapter = new UserAdapter(displayStudentList);
        RecyclerView.LayoutManager notificationLayoutManager = new LinearLayoutManager(ViewClassActivity.this);
        notificationsRecycler.setLayoutManager(notificationLayoutManager);
        notificationsRecycler.setAdapter(userAdapter);
        userAdapter.setOnItemClickListener(position -> startActivity(new Intent(ViewClassActivity.this, ViewUserActivity.class)
                .putExtra(StringExtras.PROFILE_USER, (new Gson()).toJson(displayStudentList.get(position)))
                .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser))));
    }
}
