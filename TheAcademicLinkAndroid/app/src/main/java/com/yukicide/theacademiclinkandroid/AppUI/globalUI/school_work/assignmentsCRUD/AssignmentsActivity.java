package com.yukicide.theacademiclinkandroid.AppUI.globalUI.school_work.assignmentsCRUD;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Adapters.AssignmentsAdapter;
import com.yukicide.theacademiclinkandroid.Repositories.Adapters.NotesAdapter;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Notes.AssignmentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Notes.NotesModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking.SubjectModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.StudentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.TeacherModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.UserModel;

import java.io.IOException;
import java.util.ArrayList;

public class AssignmentsActivity extends AppCompatActivity {
    private SubjectModel subjectModel;
    private UserModel currentUser;
    private TeacherModel teacher;
    private StudentModel student;
    private ArrayList<AssignmentModel> assignmentsList = new ArrayList<>();
    private AssignmentsAdapter assignmentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments);

        subjectModel = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.SUBJECT), SubjectModel.class);

        FloatingActionButton btnAddAssignment = findViewById(R.id.btnAddAssignment);
        btnAddAssignment.setVisibility(View.GONE);

        currentUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), UserModel.class);
        if (currentUser == null)
            finish();
        else if (currentUser.getUserType().equals(UserType.TEACHER)) {
            teacher = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), TeacherModel.class);
            btnAddAssignment.setVisibility(View.VISIBLE);
        } else if (currentUser.getUserType().equals(UserType.STUDENT))
            student = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), StudentModel.class);

        TextView txtAssignmentsClass = findViewById(R.id.txtSubjectName);
        if (subjectModel.getClassId() != null)
            txtAssignmentsClass.setText(String.format("%s Grade %d Assignments", subjectModel.getName(), subjectModel.getClassId()));
        else
            txtAssignmentsClass.setText(String.format("%s Grade %d Assignments", subjectModel.getName(), subjectModel.getGrade()));

        btnAddAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AssignmentsActivity.this, AddAssignmentsActivity.class)
                        .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser))
                        .putExtra(StringExtras.SUBJECT, (new Gson()).toJson(subjectModel)));
            }
        });

        assignmentsRecyclerInit();

        FirebaseFirestore.getInstance().collection(CollectionName.ASSIGNMENTS)
                .whereEqualTo("subjectId", subjectModel.getId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null){
                        for (DocumentSnapshot d : queryDocumentSnapshots) {
                            AssignmentModel n = d.toObject(AssignmentModel.class);
                            n.setId(d.getId());

                            boolean exists = false;
                            for (AssignmentModel am : assignmentsList) {
                                if (n.equals(am)) {
                                    exists = true;
                                    break;
                                }
                            }

                            if (!exists) {
                                assignmentsList.add(n);
                            }
                        }

                        assignmentsAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new AlertDialog.Builder(AssignmentsActivity.this, R.style.CustomDialogTheme)
                                .setIcon(R.drawable.ic_error_outline)
                                .setTitle("Error")
                                .setMessage("Unable to get assignments!\n\n" + e.getMessage())
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                });
    }

    private void assignmentsRecyclerInit() {
        RecyclerView catRecyclerView = findViewById(R.id.assignmentsRecycler);
        catRecyclerView.setHasFixedSize(false);
        assignmentsAdapter = new AssignmentsAdapter(assignmentsList);
        RecyclerView.LayoutManager catLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        catRecyclerView.setLayoutManager(catLayoutManager);
        catRecyclerView.setAdapter(assignmentsAdapter);
        assignmentsAdapter.setOnItemClickListener(new AssignmentsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) throws IOException {
                startActivity(new Intent(AssignmentsActivity.this, ViewAssignmentsActivity.class)
                        .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser))
                        .putExtra(StringExtras.SUBJECT, (new Gson()).toJson(subjectModel))
                        .putExtra(StringExtras.ASSIGNMENTS, (new Gson()).toJson(assignmentsList.get(position))));
            }
        });
    }
}
