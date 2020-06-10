package com.yukicide.theacademiclinkandroid.AppUI.globalUI.school_work.assignmentsCRUD;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.AppUI.globalUI.school_work.notesCRUD.ViewNotesActivity;
import com.yukicide.theacademiclinkandroid.MainActivity;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Downloading.DownloadTask;
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

        ImageView btnDownload = findViewById(R.id.btnDownload);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new DownloadTask(ViewAssignmentsActivity.this, assignment.getDocuments().getUrl());

                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(assignment.getDocuments().getUrl()));
                    startActivity(browserIntent);
                } catch (ActivityNotFoundException e) {
                    new AlertDialog.Builder(ViewAssignmentsActivity.this, R.style.CustomDialogTheme)
                            .setIcon(R.drawable.ic_error_outline)
                            .setTitle("Error")
                            .setMessage("Link to resource may be invalid!\nWould you like to report this error?")
                            .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO: 2020/06/07 REPORT LINK

                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                    e.printStackTrace();
                }
            }
        });
    }
}
