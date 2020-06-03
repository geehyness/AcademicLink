package com.yukicide.theacademiclinkandroid.AppUI.globalUI.notesCRUD;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Adapters.DocumentAdapter;
import com.yukicide.theacademiclinkandroid.Repositories.Adapters.NotesAdapter;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Notes.NotesModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking.SubjectModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.StudentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.TeacherModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.UserModel;

import java.io.IOException;
import java.util.ArrayList;

public class NotesActivity extends AppCompatActivity {
    private SubjectModel subjectModel;
    private UserModel currentUser;
    private TeacherModel teacher;
    private StudentModel student;
    private ArrayList<NotesModel> notesList = new ArrayList<>();
    private NotesAdapter notesAdapter;

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

        notesRecyclerInit();

        FirebaseFirestore.getInstance().collection(CollectionName.NOTES)
                .whereEqualTo("subjectId", subjectModel.getId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null){
                        for (DocumentSnapshot d : queryDocumentSnapshots) {
                            NotesModel n = d.toObject(NotesModel.class);
                            n.setId(d.getId());

                            boolean exists = false;
                            for (NotesModel nm : notesList) {
                                if (n.equals(nm)) {
                                    exists = true;
                                    break;
                                }
                            }

                            if (!exists) {
                                notesList.add(n);
                            }
                        }

                        notesAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new AlertDialog.Builder(NotesActivity.this, R.style.CustomDialogTheme)
                                .setIcon(R.drawable.ic_error_outline)
                                .setTitle("Error")
                                .setMessage("Unable to get notes!\n\n" + e.getMessage())
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                });
    }

    private void notesRecyclerInit() {
        RecyclerView catRecyclerView = findViewById(R.id.notesRecycler);
        catRecyclerView.setHasFixedSize(false);
        notesAdapter = new NotesAdapter(notesList);
        RecyclerView.LayoutManager catLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        catRecyclerView.setLayoutManager(catLayoutManager);
        catRecyclerView.setAdapter(notesAdapter);
        notesAdapter.setOnItemClickListener(new NotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) throws IOException {
                startActivity(new Intent(NotesActivity.this, ViewNotesActivity.class)
                    .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser))
                    .putExtra(StringExtras.NOTES, (new Gson()).toJson(notesList.get(position))));
            }
        });
    }
}
