package com.yukicide.theacademiclinkandroid.AppUI.adminUI.subjectsCRUD;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Adapters.GradeListAdapter;
import com.yukicide.theacademiclinkandroid.Repositories.Adapters.SubjectAdapter;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;
import com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking.SubjectModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.ClassModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.StudentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.TeacherModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.UserModel;
import com.yukicide.theacademiclinkandroid.Repositories.UIElements.MyProgressDialog;

import java.util.ArrayList;

public class MandatorySubjectsActivity extends AppCompatActivity {
    CardView cardGrade, cardSubjects;
    ArrayList<Integer> gradeList = new ArrayList<>();
    ArrayList<SubjectModel> subjectList = new ArrayList<>();
    GradeListAdapter collectionAdapter;
    SubjectAdapter subjectAdapter;

    int selectedGrade = -1;
    private boolean changed = false;
    Button save;
    MyProgressDialog progressDialog;

    private Boolean assign = false;
    private UserModel profileUser = null;
    private StudentModel student = null;
    private TeacherModel teacher = null;

    TextView gradeTitle, subjectTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mandatory_subjects);

        assign = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.ASSIGN_SUBJECT), Boolean.class);
        if (assign == null)
            assign = false;

        if (assign) {
            profileUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.PROFILE_USER), UserModel.class);

            if (profileUser.getUserType().equals(UserType.STUDENT))
                student = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.PROFILE_USER), StudentModel.class);

            if  (profileUser.getUserType().equals(UserType.TEACHER))
                teacher = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.PROFILE_USER), TeacherModel.class);
        }

        gradeTitle = findViewById(R.id.gradeTitle);
        subjectTitle = findViewById(R.id.subjectTitle);

        cardGrade = findViewById(R.id.cardGrade);
        cardSubjects = findViewById(R.id.cardSubjects);

        cardSubjects.setVisibility(View.GONE);

        initGradeRecycler();
        initSubjectRecycler();

        if (assign && profileUser.getUserType().equals(UserType.STUDENT)) {
            if (profileUser.getUserType().equals(UserType.STUDENT)) {
                FirebaseFirestore.getInstance()
                        .collection(CollectionName.CLASS)
                        .document(student.getClassId())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot != null) {

                                ClassModel c = documentSnapshot.toObject(ClassModel.class);
                                assert c != null;

                                selectedGrade = c.getGrade();
                                cardGrade.setVisibility(View.GONE);
                                cardSubjects.setVisibility(View.VISIBLE);

                                getSubjects();
                            }
                        })
                        .addOnFailureListener(e -> new AlertDialog.Builder(MandatorySubjectsActivity.this, R.style.CustomDialogTheme)
                                .setIcon(R.drawable.ic_error_outline)
                                .setTitle("Error")
                                .setMessage(e.getMessage())
                                .setPositiveButton("Ok", null)
                                .show());
            }
        } else {
            FirebaseFirestore.getInstance()
                    .collection(CollectionName.CLASS)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots != null) {
                            for (DocumentSnapshot d : queryDocumentSnapshots) {
                                ClassModel c = d.toObject(ClassModel.class);
                                assert c != null;

                                boolean exists = false;
                                for (int a : gradeList) {
                                    if (c.getGrade() == a) {
                                        exists = true;
                                        break;
                                    }
                                }

                                if (!exists) {
                                    gradeList.add(c.getGrade());
                                }
                            }
                            collectionAdapter.notifyDataSetChanged();
                            gradeTitle.setText(R.string.gradeListTitle);
                        }
                    })
                    .addOnFailureListener(e -> new AlertDialog.Builder(MandatorySubjectsActivity.this, R.style.CustomDialogTheme)
                            .setIcon(R.drawable.ic_error_outline)
                            .setTitle("Error")
                            .setMessage(e.getMessage())
                            .setPositiveButton("Ok", null)
                            .show());
        }

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        save = findViewById(R.id.save);
        save.setVisibility(View.GONE);
        save.setOnClickListener(v -> {
            progressDialog = new MyProgressDialog(this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();

            if (assign) {
                if (profileUser.getUserType().equals(UserType.STUDENT)) {
                    FirebaseFirestore.getInstance()
                        .collection(CollectionName.USERS)
                        .document(student.getId())
                        .set(student)
                        .addOnSuccessListener(aVoid -> {
                            changed = false;
                            progressDialog.dismiss();
                            save.setVisibility(View.GONE);
                            new AlertDialog.Builder(MandatorySubjectsActivity.this, R.style.CustomDialogTheme)
                                    .setIcon(R.drawable.ic_success)
                                    .setTitle("Done")
                                    .setMessage("Assigned subjects for " + profileUser.getFirstName() + " " + profileUser.getSurname() + " updated!")
                                    .setPositiveButton("Ok", null)
                                    .show();
                        })
                        .addOnFailureListener(e -> {
                            changed = false;
                            progressDialog.dismiss();
                            save.setVisibility(View.GONE);
                            new AlertDialog.Builder(MandatorySubjectsActivity.this, R.style.CustomDialogTheme)
                                    .setIcon(R.drawable.ic_error_outline)
                                    .setTitle("Error")
                                    .setMessage(e.getMessage())
                                    .setPositiveButton("Ok", null)
                                    .show();
                        });
                } else if (profileUser.getUserType().equals(UserType.TEACHER)) {
                    FirebaseFirestore.getInstance()
                        .collection(CollectionName.USERS)
                        .document(teacher.getId())
                        .set(teacher)
                        .addOnSuccessListener(aVoid -> {
                            changed = false;
                            progressDialog.dismiss();
                            save.setVisibility(View.GONE);
                            new AlertDialog.Builder(MandatorySubjectsActivity.this, R.style.CustomDialogTheme)
                                    .setIcon(R.drawable.ic_success)
                                    .setTitle("Done")
                                    .setMessage("Assigned subjects for " + profileUser.getFirstName() + " " + profileUser.getSurname() + " updated!")
                                    .setPositiveButton("Ok", null)
                                    .show();
                        })
                        .addOnFailureListener(e -> {
                            changed = false;
                            progressDialog.dismiss();
                            save.setVisibility(View.GONE);
                            new AlertDialog.Builder(MandatorySubjectsActivity.this, R.style.CustomDialogTheme)
                                    .setIcon(R.drawable.ic_error_outline)
                                    .setTitle("Error")
                                    .setMessage(e.getMessage())
                                    .setPositiveButton("Ok", null)
                                    .show();
                        });
                }
            } else {
                saveSubjects(0);
            }
        });
    }

    private void saveSubjects(int count) {
        if (count == subjectList.size()) {
            changed = false;
            progressDialog.dismiss();
            save.setVisibility(View.GONE);
            new AlertDialog.Builder(MandatorySubjectsActivity.this, R.style.CustomDialogTheme)
                    .setIcon(R.drawable.ic_success)
                    .setTitle("Done")
                    .setMessage("Mandatory subjects for Grade " + selectedGrade + " saved!")
                    .setPositiveButton("Ok", null)
                    .show();
        } else {
            FirebaseFirestore.getInstance().collection(CollectionName.SUBJECTS)
                    .document(subjectList.get(count).getId())
                    .set(subjectList.get(count))
                    .addOnSuccessListener(aVoid -> saveSubjects(count + 1))
                    .addOnFailureListener(e -> new AlertDialog.Builder(MandatorySubjectsActivity.this, R.style.CustomDialogTheme)
                            .setIcon(R.drawable.ic_error_outline)
                            .setTitle("Error")
                            .setMessage(e.getMessage())
                            .setPositiveButton("Ok", null)
                            .show());
        }
    }

    private void initGradeRecycler() {
        RecyclerView notificationsRecycler = findViewById(R.id.gradeRecycler);
        notificationsRecycler.setHasFixedSize(true);
        collectionAdapter = new GradeListAdapter(gradeList);
        RecyclerView.LayoutManager notificationLayoutManager = new LinearLayoutManager(MandatorySubjectsActivity.this);
        notificationsRecycler.setLayoutManager(notificationLayoutManager);
        notificationsRecycler.setAdapter(collectionAdapter);
        collectionAdapter.setOnItemClickListener(position -> {
            selectedGrade = gradeList.get(position);
            cardGrade.setVisibility(View.GONE);
            cardSubjects.setVisibility(View.VISIBLE);
            getSubjects();
        });
    }

    private void initSubjectRecycler() {
        RecyclerView subjectRecycler = findViewById(R.id.subjectsRecycler);
        subjectRecycler.setHasFixedSize(true);
        subjectAdapter = new SubjectAdapter(subjectList, true,  false);
        RecyclerView.LayoutManager notificationLayoutManager = new LinearLayoutManager(MandatorySubjectsActivity.this);
        subjectRecycler.setLayoutManager(notificationLayoutManager);
        subjectRecycler.setAdapter(subjectAdapter);
        subjectAdapter.setOnItemClickListener(new SubjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (assign) {
                    if (profileUser.getUserType().equals(UserType.STUDENT)) {
                        boolean added = false;
                        int p = -1;
                        for (SubjectModel s : student.getSubjects()) {
                            if (s.getId().equals(subjectList.get(position).getId())) {
                                added = true;
                                p = student.getSubjects().indexOf(s);
                            }
                        }

                        if (added) {
                            student.getSubjects().remove(p);
                        } else {
                            student.getSubjects().add(subjectList.get(position));
                        }
                    } else if (profileUser.getUserType().equals(UserType.TEACHER)) {
                        boolean added = false;
                        int p = -1;
                        for (String s : teacher.getSubjects()) {
                            if (s.equals(subjectList.get(position).getId())) {
                                added = true;
                                p = teacher.getSubjects().indexOf(s);
                            }
                        }

                        if (added) {
                            teacher.getSubjects().remove(p);
                        } else {
                            teacher.getSubjects().add(subjectList.get(position).getId());
                        }
                    }
                }

                changed = true;
                save.setVisibility(View.VISIBLE);
                subjectList.get(position).setMandatory(!subjectList.get(position).isMandatory());
                subjectAdapter.notifyDataSetChanged();
            }

            @Override
            public void onEditClick(int position) {

            }
        });
    }

    private void getSubjects() {
        subjectList.clear();

        FirebaseFirestore.getInstance().collection(CollectionName.SUBJECTS)
                .whereEqualTo("grade", selectedGrade)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        for (DocumentSnapshot d : queryDocumentSnapshots) {
                            SubjectModel subjectModel = d.toObject(SubjectModel.class);
                            assert subjectModel != null;
                            subjectModel.setId(d.getId());

                            subjectList.add(subjectModel);
                        }

                        if (assign) {
                            subjectTitle.setText(String.format("Select subjects to assign to %s %s", profileUser.getFirstName(), profileUser.getSurname()));

                            for (SubjectModel s : subjectList)
                                s.setMandatory(false);

                            if (profileUser.getUserType().equals(UserType.STUDENT)) {
                                for (SubjectModel s : subjectList) {
                                    for (SubjectModel ss : student.getSubjects()) {
                                        if (s.getId().equals(ss.getId())) {
                                            s.setMandatory(true);
                                        }
                                    }
                                }
                            } else if (profileUser.getUserType().equals(UserType.TEACHER)) {
                                for (SubjectModel s : subjectList) {
                                    for (String ts : teacher.getSubjects()) {
                                        if (s.getId().equals(ts)) {
                                            s.setMandatory(true);
                                        }
                                    }
                                }
                            }
                        } else {
                            subjectTitle.setText(R.string.subjectListTitle);
                        }
                        subjectAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> new AlertDialog.Builder(MandatorySubjectsActivity.this, R.style.CustomDialogTheme)
                        .setIcon(R.drawable.ic_error_outline)
                        .setTitle("Error")
                        .setMessage(e.getMessage())
                        .setPositiveButton("Ok", null)
                        .show());
    }

    @Override
    public void onBackPressed() {
        if (changed)
            new AlertDialog.Builder(MandatorySubjectsActivity.this, R.style.CustomDialogTheme)
                    .setIcon(R.drawable.ic_error_outline)
                    .setTitle("Unsaved changes!")
                    .setMessage("Would you like to discard unsaved changes?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        changed = false;
                        save.setVisibility(View.GONE);
                        onBackPressed();
                    })
                    // TODO: 2020/04/20 INCLUDE SAVE OPTION
                    /*.setNeutralButton("Save", (dialog, which) -> {
                        progressDialog = new MyProgressDialog(getBaseContext());
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        saveSubjects(0);
                    })*/
                    .setNegativeButton("No", null)
                    .show();
        else {
            if (assign && profileUser.getUserType().equals(UserType.STUDENT)) {
                super.onBackPressed();
            } else {
                if (cardGrade.getVisibility() == View.GONE) {
                    selectedGrade = -1;
                    cardGrade.setVisibility(View.VISIBLE);
                    cardSubjects.setVisibility(View.GONE);
                } else
                    super.onBackPressed();
            }
        }
    }
}
