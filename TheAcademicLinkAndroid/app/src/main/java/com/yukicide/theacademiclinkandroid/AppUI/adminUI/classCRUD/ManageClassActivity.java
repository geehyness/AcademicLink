package com.yukicide.theacademiclinkandroid.AppUI.adminUI.classCRUD;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Adapters.GradeAdapter;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking.GradeModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.ClassModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.UserModel;

import java.util.ArrayList;

public class ManageClassActivity extends AppCompatActivity {
SearchView searchView;
    private boolean searching = false;
    private GradeAdapter gradeAdapter;
    UserModel currentUser;

    private ArrayList<GradeModel> gradeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_class);

        currentUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), UserModel.class);

        initUI();
        initGradeRecycler();

        FirebaseFirestore ff = FirebaseFirestore.getInstance();
        ff.collection(CollectionName.CLASS)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<ClassModel> classList = new ArrayList<>();

                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        ClassModel classModel = documentSnapshot.toObject(ClassModel.class);
                        assert classModel != null;
                        classModel.setId(documentSnapshot.getId());
                        classList.add(classModel);
                    }

                    for (ClassModel c : classList) {
                        boolean added = false;
                        for (GradeModel g : gradeList) {
                            if (c.getGrade() == g.getGrade()) {
                                gradeList.get(gradeList.indexOf(g)).getClasses().add(c);
                                added = true;
                            }
                        }

                        if (!added) {
                            ArrayList<ClassModel> temp = new ArrayList<>();
                            temp.add(c);
                            gradeList.add(new GradeModel(c.getGrade(), temp));
                        }
                    }

                    gradeAdapter.notifyDataSetChanged();
                }).addOnFailureListener(e -> new AlertDialog.Builder(ManageClassActivity.this, R.style.CustomDialogTheme)
                        .setIcon(R.drawable.ic_error_outline)
                        .setTitle("Error")
                        .setMessage(e.getMessage())
                        .setPositiveButton("Ok", null)
                        .show());

        ImageView back = findViewById(R.id.btnBack);
        back.setOnClickListener(v -> finish());
    }

    private void initUI() {
        FloatingActionButton manage = findViewById(R.id.btnManage);
        manage.setOnClickListener(this::showMenu);

        searchView = findViewById(R.id.searchView);
        searchView.setVisibility(View.GONE);
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.add_student){
                startActivity(new Intent(ManageClassActivity.this, AddClassActivity.class));
            } else if (item.getItemId() == R.id.search){
                if (!searching)
                    searchView.setVisibility(View.VISIBLE);
                else
                    searchView.setVisibility(View.GONE);
                searching = !searching;
            }

            return false;
        });
        popup.inflate(R.menu.manage_class_menu);
        popup.show();
    }

    private void initGradeRecycler() {
        RecyclerView gradeRecycler = findViewById(R.id.gradeRecycler);
        gradeRecycler.setHasFixedSize(false);
        gradeAdapter = new GradeAdapter(gradeList, ManageClassActivity.this, currentUser);
        RecyclerView.LayoutManager notificationLayoutManager = new LinearLayoutManager(ManageClassActivity.this);
        gradeRecycler.setLayoutManager(notificationLayoutManager);
        gradeRecycler.setAdapter(gradeAdapter);
        gradeAdapter.setOnItemClickListener(position -> {

        });
    }
}
