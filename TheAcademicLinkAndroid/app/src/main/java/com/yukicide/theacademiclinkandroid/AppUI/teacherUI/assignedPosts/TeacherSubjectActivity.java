package com.yukicide.theacademiclinkandroid.AppUI.teacherUI.assignedPosts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.AppUI.globalUI.school_work.assignmentsCRUD.AssignmentsActivity;
import com.yukicide.theacademiclinkandroid.AppUI.globalUI.school_work.notesCRUD.NotesActivity;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;
import com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking.SubjectModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.TeacherModel;

public class TeacherSubjectActivity extends AppCompatActivity {
    SubjectModel subjectModel;
    private TeacherModel currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_subject);

        currentUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), TeacherModel.class);
        if (currentUser == null || currentUser.getUserType()!= UserType.TEACHER)
            finish();

        subjectModel = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.SUBJECT), SubjectModel.class);

        TextView txtSubjectName = findViewById(R.id.txtSubjectName);
        if (subjectModel.getClassId() != null)
            txtSubjectName.setText(String.format("%s Grade %d", subjectModel.getName(), subjectModel.getClassId()));
        else
            txtSubjectName.setText(String.format("%s Grade %d", subjectModel.getName(), subjectModel.getGrade()));

        CardView cardNotes = findViewById(R.id.cardNotes),
                cardAssignments = findViewById(R.id.cardAssignments),
                cardQuizz = findViewById(R.id.cardQuiz),
                cardMarks = findViewById(R.id.cardMarks);

        cardNotes.setOnClickListener(v -> startActivity(new Intent(TeacherSubjectActivity.this, NotesActivity.class)
            .putExtra(StringExtras.SUBJECT, (new Gson()).toJson(subjectModel))
            .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser))));

        cardAssignments.setOnClickListener(v -> startActivity(new Intent(TeacherSubjectActivity.this, AssignmentsActivity.class)
            .putExtra(StringExtras.SUBJECT, (new Gson()).toJson(subjectModel))
            .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser))));
    }


}
