package com.yukicide.theacademiclinkandroid.AppUI.adminUI.subjectsCRUD;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking.SubjectModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.ClassModel;
import com.yukicide.theacademiclinkandroid.Repositories.UIElements.MyProgressDialog;

import java.util.ArrayList;
import java.util.Objects;

public class AddSubjectActivity extends AppCompatActivity {
    Spinner spFrom, spTo;
    TextInputLayout txtName;
    MyProgressDialog progressDialog;
    ArrayList<String> addedSubjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        txtName = findViewById(R.id.txtSubjectName);
        setSpinner();

        Button add = findViewById(R.id.btnAdd);
        add.setOnClickListener(v -> addSubject());

        ImageView back = findViewById(R.id.btnBack);
        back.setOnClickListener(v -> finish());
    }

    private void addSubject() {
        String name = Objects.requireNonNull(txtName.getEditText()).getText().toString();

        if (TextUtils.isEmpty(name)) {
            txtName.getEditText().setError("Subject name cannot be empty!");
            txtName.getEditText().requestFocus();
            return;
        }

        if (spFrom.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Choose a starting grade for this subject!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spTo.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Choose an ending grade for this subject!", Toast.LENGTH_SHORT).show();
            return;
        }

        int start = Integer.valueOf(spFrom.getSelectedItem().toString());
        int end = Integer.valueOf(spTo.getSelectedItem().toString());

        if (start > end) {
            Toast.makeText(this, "End grade cannot be lower than start grade!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        uploadGrade(name, start, end);
    }

    private void uploadGrade(String name, int current, int end) {
        CheckBox cbSingleAdd = findViewById(R.id.cbSingleAdd);
        CheckBox cbMandatory = findViewById(R.id.cbMandatory);
        boolean single = cbSingleAdd.isChecked(),
                mandatory = cbMandatory.isChecked();

        if (current <= end) {
            FirebaseFirestore.getInstance().collection(CollectionName.CLASS)
                    .whereEqualTo("grade", current)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots != null) {
                            ArrayList<ClassModel> gradeList = new ArrayList<>();

                            for (DocumentSnapshot d : queryDocumentSnapshots) {
                                ClassModel classModel = d.toObject(ClassModel.class);
                                assert classModel != null;
                                classModel.setId(d.getId());

                                gradeList.add(classModel);
                            }

                            if (gradeList.size() == 0) {
                                new AlertDialog.Builder(AddSubjectActivity.this, R.style.CustomDialogTheme)
                                        .setIcon(R.drawable.ic_error_outline)
                                        .setCancelable(false)
                                        .setTitle("Error")
                                        .setMessage("No grade " + current + " classes exist in the database!\nAdd the classes in Class Management.")
                                        .setPositiveButton("Ok", (dialog, which) -> uploadGrade(name, current + 1, end))
                                        .show();
                            } else {
                                if (single) {
                                    SubjectModel newSubject = new SubjectModel(current, name + " Grade" + current);
                                    newSubject.setMandatory(mandatory);
                                    FirebaseFirestore.getInstance().collection(CollectionName.SUBJECTS)
                                            .document(name.toLowerCase().replace(" ", "_") + current)
                                            .set(newSubject)
                                            .addOnSuccessListener(aVoid -> {
                                                addedSubjects.add(name + " Grade " + current);
                                                uploadGrade(name, current + 1, end);
                                            })
                                            .addOnFailureListener(e -> new AlertDialog.Builder(AddSubjectActivity.this, R.style.CustomDialogTheme)
                                                    .setIcon(R.drawable.ic_error_outline)
                                                    .setTitle("Error")
                                                    .setMessage(e.getMessage())
                                                    .setPositiveButton("Ok", null)
                                                    .show());
                                } else {
                                    uploadClass(name, current, end, gradeList, 0, mandatory);
                                }
                            }
                        }
                    }).addOnFailureListener(e -> new AlertDialog.Builder(AddSubjectActivity.this, R.style.CustomDialogTheme)
                        .setIcon(R.drawable.ic_error_outline)
                        .setTitle("Error")
                        .setMessage(e.getMessage())
                        .setPositiveButton("Ok", null)
                        .show());
        } else {
            StringBuilder addedList = new StringBuilder();

            for (String s : addedSubjects) {
                addedList.append(s).append("\n");
            }

            if (!addedSubjects.isEmpty()) {
                new AlertDialog.Builder(AddSubjectActivity.this, R.style.CustomDialogTheme)
                        .setIcon(R.drawable.ic_success)
                        .setTitle("Success")
                        .setMessage(addedList.toString() + "added.\n\n" + "Would you like to add another subject?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            spFrom.setSelection(0);
                            spTo.setSelection(0);
                            Objects.requireNonNull(txtName.getEditText()).setText("");
                        })
                        .setNegativeButton("No", (dialog, which) -> finish())
                        .show();
            } else {
                new AlertDialog.Builder(AddSubjectActivity.this, R.style.CustomDialogTheme)
                        .setIcon(R.drawable.ic_error_outline)
                        .setTitle("Error")
                        .setMessage("No Subject was added! Would you like to retry adding?")
                        .setPositiveButton("Yes", null)
                        .setNegativeButton("No", (dialog, which) -> finish())
                        .show();
            }

            addedSubjects.clear();
            progressDialog.dismiss();
        }
    }

    private void uploadClass (String name, int current, int end, ArrayList<ClassModel> classList, int position, boolean mandatory) {
        if (position == classList.size()) {
            uploadGrade(name, current + 1, end);
        } else {
            SubjectModel newSubject = new SubjectModel(current, classList.get(position).getId(), name);
            newSubject.setMandatory(mandatory);
            FirebaseFirestore.getInstance().collection(CollectionName.SUBJECTS)
                    .document(name.toLowerCase().replace(" ", "_") + classList.get(position).getId())
                    .set(newSubject)
                    .addOnSuccessListener(aVoid -> {
                        addedSubjects.add(newSubject.getName());
                        uploadClass(name, current, end, classList, position + 1, mandatory);
                    })
                    .addOnFailureListener(e -> new AlertDialog.Builder(AddSubjectActivity.this, R.style.CustomDialogTheme)
                            .setIcon(R.drawable.ic_error_outline)
                            .setTitle("Error")
                            .setMessage(e.getMessage())
                            .setPositiveButton("Ok", null)
                            .show());
        }
    }

    private void setSpinner() {
        spFrom = findViewById(R.id.spFrom);
        spTo = findViewById(R.id.spTo);

        ArrayList<String> gradeList = new ArrayList<>();

        gradeList.add("Pick a grade");

        for (int a = 1; a <= 12; a++) {
            gradeList.add(String.valueOf(a));
        }

        if (!gradeList.isEmpty()){
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gradeList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spFrom.setAdapter(adapter);
            spTo.setAdapter(adapter);
        }
    }
}
