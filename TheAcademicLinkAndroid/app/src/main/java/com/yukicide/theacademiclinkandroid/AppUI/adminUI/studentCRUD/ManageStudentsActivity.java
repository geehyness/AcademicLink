package com.yukicide.theacademiclinkandroid.AppUI.adminUI.studentCRUD;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.AppUI.globalUI.user_management.ViewUserActivity;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Adapters.UserAdapter;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.ClassModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.StudentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.UserModel;

import java.util.ArrayList;

public class ManageStudentsActivity extends AppCompatActivity {
    private ClassModel selectedClass = null;
    private ArrayList<StudentModel> studentList = new ArrayList<>();
    private ArrayList<UserModel> displayStudentList = new ArrayList<>();

    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_students);

        selectedClass = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CLASS), ClassModel.class);
        if (selectedClass == null) {
            new AlertDialog.Builder(ManageStudentsActivity.this, R.style.CustomDialogTheme)
                    .setIcon(R.drawable.ic_error_outline)
                    .setTitle("Error")
                    .setMessage("Unable to get Class Information!")
                    .setPositiveButton("Ok", null)
                    .show();
            finish();
        }

        initStudentRecycler();

        FloatingActionButton manage = findViewById(R.id.btnManage);
        manage.setOnClickListener(this::showMenu);

        FirebaseFirestore.getInstance().collection(CollectionName.USERS)
                .whereEqualTo("userType", UserType.STUDENT)
                .whereEqualTo("classId", selectedClass.getId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot d : queryDocumentSnapshots) {
                        StudentModel studentModel = d.toObject(StudentModel.class);
                        assert studentModel != null;
                        if (studentModel.getUserType().equals(UserType.STUDENT))
                            studentList.add(studentModel);
                    }
                    // TODO: 2020/02/22 SORT BY NAME
                    displayStudentList.addAll(studentList);
                    userAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> new AlertDialog.Builder(ManageStudentsActivity.this, R.style.CustomDialogTheme)
                        .setIcon(R.drawable.ic_error_outline)
                        .setTitle("Error")
                        .setMessage(e.getMessage())
                        .setPositiveButton("Ok", null)
                        .show());

        ImageView back = findViewById(R.id.btnBack);
        back.setOnClickListener(v -> finish());
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.add_student){
                startActivity(new Intent(this, AddStudentActivity.class)
                    .putExtra(StringExtras.CLASS, (new Gson()).toJson(selectedClass)));
            } else if (item.getItemId() == R.id.add_batch){
                startActivity(new Intent(ManageStudentsActivity.this, AddBatchStudentsActivity.class));
            }

            return false;
        });
        popup.inflate(R.menu.manage_student_menu);
        popup.show();
    }

    private void initStudentRecycler() {
        RecyclerView notificationsRecycler = findViewById(R.id.studentsRecycler);
        notificationsRecycler.setHasFixedSize(false);
        userAdapter = new UserAdapter(displayStudentList);
        RecyclerView.LayoutManager notificationLayoutManager = new LinearLayoutManager(ManageStudentsActivity.this);
        notificationsRecycler.setLayoutManager(notificationLayoutManager);
        notificationsRecycler.setAdapter(userAdapter);
        userAdapter.setOnItemClickListener(position -> startActivity(new Intent(ManageStudentsActivity.this, ViewUserActivity.class)
                .putExtra(StringExtras.PROFILE_USER, (new Gson()).toJson(displayStudentList.get(position)))));
    }
}
