package com.yukicide.theacademiclinkandroid.AppUI.globalUI.school_work.notesCRUD;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Adapters.DocumentAdapter;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Notes.AssignmentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Notes.AttachmentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Notes.NotesModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking.SubjectModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.UserModel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ViewNotesActivity extends AppCompatActivity {
    private SubjectModel subjectModel;
    private UserModel currentUser;
    private NotesModel notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notes);

        subjectModel = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.SUBJECT), SubjectModel.class);
        currentUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), UserModel.class);
        notes = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.NOTES), NotesModel.class);

        initViews();
    }

    private void initViews() {
        TextView txtTitle = findViewById(R.id.txtNotesTitle),
                txtDetails = findViewById(R.id.txtNotesDetails);

        txtTitle.setText(notes.getName());
        txtDetails.setText(notes.getDetails());

        initDocRecycler();
    }

    private void initDocRecycler() {
        RecyclerView catRecyclerView = findViewById(R.id.docsRecycler);
        catRecyclerView.setHasFixedSize(false);
        DocumentAdapter docAdapter = new DocumentAdapter(notes.getDocuments(), true);
        RecyclerView.LayoutManager catLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        catRecyclerView.setLayoutManager(catLayoutManager);
        catRecyclerView.setAdapter(docAdapter);
        docAdapter.setOnItemClickListener(new DocumentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) throws IOException {
                try {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(notes.getDocuments().get(position).getUrl()));
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    new AlertDialog.Builder(ViewNotesActivity.this, R.style.CustomDialogTheme)
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

            @Override
            public void onMoreClick(int position) {}
        });
    }
}