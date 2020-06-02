package com.yukicide.theacademiclinkandroid.AppUI.globalUI.notesCRUD;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;
import com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking.SubjectModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.StudentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.TeacherModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.UserModel;

import io.grpc.ClientStreamTracer;

public class NotesActivity extends AppCompatActivity {
    private SubjectModel subjectModel;
    private UserModel currentUser;
    private TeacherModel teacher;
    private StudentModel student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        subjectModel = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.SUBJECT), SubjectModel.class);

        FloatingActionButton btnAddNotes = findViewById(R.id.btnAddNotes);
        btnAddNotes.setVisibility(View.GONE);

        currentUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), UserModel.class);
        if (currentUser == null)
            finish();
        else if (currentUser.getUserType().equals(UserType.TEACHER)) {
            teacher = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), TeacherModel.class);
            btnAddNotes.setVisibility(View.VISIBLE);
        } else if (currentUser.getUserType().equals(UserType.STUDENT))
            student = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), StudentModel.class);

        TextView txtNotesClass = findViewById(R.id.txtSubjectName);
        if (subjectModel.getClassId() != null)
            txtNotesClass.setText(String.format("%s Grade %d Notes", subjectModel.getName(), subjectModel.getClassId()));
        else
            txtNotesClass.setText(String.format("%s Grade %d Notes", subjectModel.getName(), subjectModel.getGrade()));

        btnAddNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotesActivity.this, AddNotesActivity.class)
                    .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser))
                    .putExtra(StringExtras.SUBJECT, (new Gson()).toJson(subjectModel)));
            }
        });
    }
}
