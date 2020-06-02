package com.yukicide.theacademiclinkandroid.AppUI.adminUI.teacherCRUD;

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
import com.yukicide.theacademiclinkandroid.AppUI.adminUI.studentCRUD.AddBatchStudentsActivity;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Adapters.UserAdapter;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.ClassModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.TeacherModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.UserModel;

import java.util.ArrayList;

public class ManageTeachersActivity extends AppCompatActivity {
    private ClassModel selectedClass = null;
    private ArrayList<TeacherModel> staffList = new ArrayList<>();
    private ArrayList<UserModel> displayStaffList = new ArrayList<>();

    private UserAdapter userAdapter;
    private UserModel currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_teachers);

        currentUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), UserModel.class);

        initTeachersRecycler();

        FloatingActionButton manage = findViewById(R.id.btnManage);
        manage.setOnClickListener(this::showMenu);

        FirebaseFirestore.getInstance().collection(CollectionName.USERS)
                .whereEqualTo("userType", UserType.TEACHER)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot d : queryDocumentSnapshots) {
                        TeacherModel teacherModel = d.toObject(TeacherModel.class);
                        assert teacherModel != null;
                        teacherModel.setId(d.getId());
                        if (teacherModel.getUserType().equals(UserType.TEACHER))
                            staffList.add(teacherModel);
                    }
                    // TODO: 2020/02/22 SORT BY NAME
                    displayStaffList.addAll(staffList);
                    userAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> new AlertDialog.Builder(ManageTeachersActivity.this, R.style.CustomDialogTheme)
                        .setIcon(R.drawable.ic_error_outline)
                        .setTitle("Error")
                        .setMessage("22 "+e.getMessage())
                        .setPositiveButton("Ok", null)
                        .show());

        ImageView back = findViewById(R.id.btnBack);
        back.setOnClickListener(v -> finish());
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.add_teacher){
                startActivity(new Intent(ManageTeachersActivity.this, AddTeacherActivity.class));
            } else if (item.getItemId() == R.id.add_batch){
                startActivity(new Intent(ManageTeachersActivity.this, AddBatchStudentsActivity.class));
            }

            return false;
        });
        popup.inflate(R.menu.manage_teachers_menu);
        popup.show();
    }

    private void initTeachersRecycler() {
        RecyclerView notificationsRecycler = findViewById(R.id.teachersRecycler);
        notificationsRecycler.setHasFixedSize(false);
        userAdapter = new UserAdapter(displayStaffList);
        RecyclerView.LayoutManager notificationLayoutManager = new LinearLayoutManager(ManageTeachersActivity.this);
        notificationsRecycler.setLayoutManager(notificationLayoutManager);
        notificationsRecycler.setAdapter(userAdapter);
        userAdapter.setOnItemClickListener(position -> startActivity(new Intent(ManageTeachersActivity.this, ViewUserActivity.class)
                .putExtra(StringExtras.PROFILE_USER, (new Gson()).toJson(displayStaffList.get(position)))
                .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser))));
    }
}