package com.yukicide.theacademiclinkandroid.AppUI.teacherUI.assignedPosts;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.AppUI.adminUI.studentCRUD.ManageStudentsActivity;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Adapters.SubjectAdapter;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;
import com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking.SubjectModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.ClassModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.TeacherModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.UserModel;

import java.util.ArrayList;

public class TeacherClassManagementActivity extends AppCompatActivity {
    UserModel currentUser;
    ArrayList<SubjectModel> subjectList = new ArrayList<>();

    SubjectAdapter subjectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_class_management);

        CardView assignedClass = findViewById(R.id.assignedClass);
        TextView txtClass = findViewById(R.id.txtClass);
        ConstraintLayout classC = findViewById(R.id.classC);

        assignedClass.setVisibility(View.GONE);
        txtClass.setVisibility(View.GONE);
        classC.setVisibility(View.GONE);

        currentUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), TeacherModel.class);
        if (currentUser == null || currentUser.getUserType()!= UserType.TEACHER)
            finish();

        if (((TeacherModel) currentUser).getClassId() != null)
            if (!((TeacherModel) currentUser).getClassId().equals(""))
                FirebaseFirestore.getInstance().collection(CollectionName.CLASS)
                        .document(((TeacherModel) currentUser).getClassId())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot != null) {
                                ClassModel classModel = documentSnapshot.toObject(ClassModel.class);

                                assert classModel != null;
                                classModel.setId(documentSnapshot.getId());
                                txtClass.setText(classModel.getName());

                                assignedClass.setVisibility(View.VISIBLE);
                                txtClass.setVisibility(View.VISIBLE);
                                classC.setVisibility(View.VISIBLE);

                                classC.setOnClickListener(v -> startActivity(new Intent(TeacherClassManagementActivity.this, ManageStudentsActivity.class)
                                        .putExtra(StringExtras.CLASS, (new Gson()).toJson(classModel))));
                            }
                        })
                        .addOnFailureListener(e -> new AlertDialog.Builder(TeacherClassManagementActivity.this, R.style.CustomDialogTheme)
                                .setIcon(R.drawable.ic_error_outline)
                                .setTitle("Error")
                                .setMessage(e.getMessage())
                                .setPositiveButton("Ok", null)
                                .show());

        ImageView back = findViewById(R.id.btnBack);
        back.setOnClickListener(v -> finish());

        initSubjectRecycler();
        getSubjects();
    }

    private void initSubjectRecycler() {
        RecyclerView subjectRecycler = findViewById(R.id.subjectsRecycler);
        subjectRecycler.setHasFixedSize(true);
        subjectAdapter = new SubjectAdapter(subjectList, false, false);
        RecyclerView.LayoutManager notificationLayoutManager = new LinearLayoutManager(TeacherClassManagementActivity.this);
        subjectRecycler.setLayoutManager(notificationLayoutManager);
        subjectRecycler.setAdapter(subjectAdapter);
        subjectAdapter.setOnItemClickListener(new SubjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                startActivity(new Intent(TeacherClassManagementActivity.this, TeacherSubjectActivity.class)
                    .putExtra(StringExtras.SUBJECT, (new Gson()).toJson(subjectList.get(position)))
                    .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser)));
            }

            @Override
            public void onEditClick(int position) {

            }
        });
    }

    private void getSubjects() {
        FirebaseFirestore.getInstance().collection(CollectionName.SUBJECTS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots!= null) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            SubjectModel subjectModel = documentSnapshot.toObject(SubjectModel.class);
                            assert subjectModel != null;
                            subjectModel.setId(documentSnapshot.getId());
                            for (String id : ((TeacherModel) currentUser).getSubjects()) {
                                if (id.equals(subjectModel.getId())) {
                                    boolean exists = false;
                                    for (SubjectModel s : subjectList) {
                                        if (subjectModel.getId().equals(s.getId())) {
                                            exists = true;
                                            break;
                                        }
                                    }
                                    if (!exists)
                                        subjectList.add(subjectModel);

                                    break;
                                }
                            }
                        }

                        subjectAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> new AlertDialog.Builder(TeacherClassManagementActivity.this, R.style.CustomDialogTheme)
                        .setIcon(R.drawable.ic_error_outline)
                        .setTitle("Error")
                        .setMessage("Unable to get class info\n" + e.getMessage())
                        .setPositiveButton("Ok", null)
                        .show());
    }

}
