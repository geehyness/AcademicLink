package com.yukicide.theacademiclinkandroid.AppUI.globalUI.school_work.assignmentsCRUD;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Notes.AssignmentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking.SubjectModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.UserModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ViewAssignmentsActivity extends AppCompatActivity {

    private SubjectModel subjectModel;
    private UserModel currentUser;
    private AssignmentModel assignment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_assignments);

        subjectModel = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.SUBJECT), SubjectModel.class);
        currentUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), UserModel.class);
        assignment = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.ASSIGNMENTS), AssignmentModel.class);

        initViews();
    }

    private void initViews() {
        TextView txtTitle = findViewById(R.id.txtAssignmentTitle),
                txtDetails = findViewById(R.id.txtAssignmentDetails),
                txtFilename = findViewById(R.id.txtFilename),
                txtDueDate = findViewById(R.id.txtDueDate);

        txtTitle.setText(assignment.getName());
        txtDetails.setText(assignment.getDetails());

        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        txtDueDate.setText(sdf.format(assignment.getDate()));

        txtFilename.setText(assignment.getDocuments().getName());
    }
}
