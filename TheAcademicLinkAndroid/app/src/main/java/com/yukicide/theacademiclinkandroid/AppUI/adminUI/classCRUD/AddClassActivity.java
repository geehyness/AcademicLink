package com.yukicide.theacademiclinkandroid.AppUI.adminUI.classCRUD;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.ClassModel;
import com.yukicide.theacademiclinkandroid.Repositories.UIElements.MyProgressDialog;

import java.util.ArrayList;
import java.util.Objects;

public class AddClassActivity extends AppCompatActivity {
Spinner gradeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        gradeSpinner = findViewById(R.id.gradeSpinner);
        setSpinner();

        Button btnAddClass = findViewById(R.id.btnAddClass);
        btnAddClass.setOnClickListener(v -> addClass());

        ImageView back = findViewById(R.id.btnBack);
        back.setOnClickListener(v -> finish());
    }

    private void addClass() {
        TextInputLayout txtClassName = findViewById(R.id.txtClassName);
        String letter = Objects.requireNonNull(txtClassName.getEditText()).getText().toString().toUpperCase();

        if (TextUtils.isEmpty(letter)) {
            txtClassName.getEditText().setError("Insert Class Letter");
            return;
        }

        if (gradeSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Select A Grade!", Toast.LENGTH_SHORT).show();
            return;
        }

        int grade = Integer.parseInt(gradeSpinner.getSelectedItem().toString());
        String id = grade + letter;
        String name = "Grade " + grade + letter;

        ClassModel tempClass = new ClassModel(grade, name);

        final MyProgressDialog progressDialog = new MyProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        FirebaseFirestore ff = FirebaseFirestore.getInstance();
        ff.collection(CollectionName.CLASS).document(id).set(tempClass)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(AddClassActivity.this, R.style.CustomDialogTheme)
                            .setIcon(R.drawable.ic_success)
                            .setTitle("Success")
                            .setMessage(name + " added.\n" + "Would you like to add another class?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                gradeSpinner.setSelection(0);
                                txtClassName.getEditText().setText("");
                            })
                            .setNegativeButton("No", (dialog, which) -> finish())
                            .show();
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(AddClassActivity.this, R.style.CustomDialogTheme)
                            .setIcon(R.drawable.ic_error_outline)
                            .setTitle("Error")
                            .setMessage(e.getMessage())
                            .setPositiveButton("Ok", null)
                            .show();
                });
    }

    private void setSpinner() {
        ArrayList<String> gradeList = new ArrayList<>();

        gradeList.add("Pick a grade");

        for (int a = 1; a <= 12; a++) {
            gradeList.add(String.valueOf(a));
        }

        if (!gradeList.isEmpty()){
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gradeList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            gradeSpinner.setAdapter(adapter);
        }
    }
}
