package com.yukicide.theacademiclinkandroid.AppUI.adminUI.subjectsCRUD;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking.SubjectModel;

import java.util.Objects;

public class EditSubject extends AppCompatActivity {
    SubjectModel currentSubject;

    TextInputLayout name, subjectGrade, subjectClass;
    Button delete, save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_subject);

        currentSubject = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.SUBJECT), SubjectModel.class);

        if (currentSubject == null) {
            new AlertDialog.Builder(EditSubject.this, R.style.CustomDialogTheme)
                    .setIcon(R.drawable.ic_error_outline)
                    .setTitle("Error")
                    .setMessage("Unable to retrieve subject details!")
                    .setPositiveButton("Ok", (dialog, which) -> finish())
                    .show();
        } else {
            // TODO: 2020/04/30 USE SPINNERS FOR SUBJECT GRADE AND CLASS

            name = findViewById(R.id.txtName);
            subjectGrade = findViewById(R.id.txtGrade);
            subjectClass = findViewById(R.id.txtClass);

            Objects.requireNonNull(name.getEditText()).setText(currentSubject.getName());
            Objects.requireNonNull(subjectGrade.getEditText()).setText(String.valueOf(currentSubject.getGrade()));

            if (currentSubject.getClassId() != null)
                Objects.requireNonNull(subjectClass.getEditText()).setText(currentSubject.getClassId());
        }

        save = findViewById(R.id.btnSave);
        save.setOnClickListener(v -> {
            // TODO: 2020/05/01 SAVE CHANGES TO SUBJECT
        });

        delete = findViewById(R.id.btnDelete);
        delete.setOnClickListener(v -> new AlertDialog.Builder(EditSubject.this, R.style.CustomDialogTheme)
                .setIcon(R.drawable.ic_error_outline)
                .setTitle("Are you sure you want to delete subject?")
                .setMessage("Please note that deleting this subject may have unforeseen ramifications!")
                .setPositiveButton("Delete", (dialog, which) -> FirebaseFirestore.getInstance().collection(CollectionName.SUBJECTS)
                        .document(currentSubject.getId())
                        .delete())
                .setNegativeButton("Cancel", null)
                .show());

        ImageView back = findViewById(R.id.btnBack);
        back.setOnClickListener(v -> finish());
    }
}
