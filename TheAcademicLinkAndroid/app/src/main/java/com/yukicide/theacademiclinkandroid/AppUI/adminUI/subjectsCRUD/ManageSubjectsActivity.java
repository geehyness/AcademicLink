package com.yukicide.theacademiclinkandroid.AppUI.adminUI.subjectsCRUD;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Adapters.SubjectCollectionAdapter;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking.SubjectCollectionModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking.SubjectModel;

import java.util.ArrayList;

public class ManageSubjectsActivity extends AppCompatActivity {
    final ArrayList<SubjectCollectionModel> subjectList = new ArrayList<>();
    ArrayList<SubjectCollectionModel> displaySubjectList = new ArrayList<>();
    private SubjectCollectionAdapter collectionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_subjects);

        initSubjectRecycler();

        ImageView manage = findViewById(R.id.btnManage);
        manage.setOnClickListener(this::showMenu);

        FirebaseFirestore.getInstance().collection(CollectionName.SUBJECTS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<SubjectModel> subjectList = new ArrayList<>();

                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        SubjectModel subjectModel = documentSnapshot.toObject(SubjectModel.class);
                        assert subjectModel != null;
                        subjectModel.setId(documentSnapshot.getId());
                        subjectList.add(subjectModel);
                    }

                    for (SubjectModel c : subjectList) {
                        Boolean added = false;
                        for (SubjectCollectionModel g : this.subjectList) {
                            if (c.getGrade() == g.getCollectionGrade()) {
                                this.subjectList.get(this.subjectList.indexOf(g)).getSubjectList().add(c);
                                added = true;
                            }
                        }

                        if (!added) {
                            ArrayList<SubjectModel> temp = new ArrayList<>();
                            temp.add(c);
                            this.subjectList.add(new SubjectCollectionModel(c.getGrade(), temp));
                        }
                    }

                    displaySubjectList.addAll(this.subjectList);
                    collectionAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> new AlertDialog.Builder(ManageSubjectsActivity.this, R.style.CustomDialogTheme)
                        .setIcon(R.drawable.ic_error_outline)
                        .setTitle("Error")
                        .setMessage(e.getMessage())
                        .setPositiveButton("Ok", null)
                        .show());

        ImageView back = findViewById(R.id.btnBack);
        back.setOnClickListener(v -> finish());
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.add_subject) {
                startActivity(new Intent(ManageSubjectsActivity.this, AddSubjectActivity.class));
            } else if (item.getItemId() == R.id.mandatory) {
                startActivity(new Intent(ManageSubjectsActivity.this, MandatorySubjectsActivity.class));
            }

            return false;
        });
        popup.inflate(R.menu.manage_subjects_menu);
        popup.show();
    }

    private void initSubjectRecycler() {
        RecyclerView notificationsRecycler = findViewById(R.id.subjectCollectionRecycler);
        notificationsRecycler.setHasFixedSize(true);
        collectionAdapter = new SubjectCollectionAdapter(displaySubjectList, getApplicationContext());
        RecyclerView.LayoutManager notificationLayoutManager = new LinearLayoutManager(ManageSubjectsActivity.this);
        notificationsRecycler.setLayoutManager(notificationLayoutManager);
        notificationsRecycler.setAdapter(collectionAdapter);
        collectionAdapter.setOnItemClickListener(position -> {

        });
    }
}