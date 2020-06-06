package com.yukicide.theacademiclinkandroid.AppUI.teacherUI.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.AppUI.adminUI.studentCRUD.ManageStudentsActivity;
import com.yukicide.theacademiclinkandroid.AppUI.teacherUI.assignedPosts.TeacherSubjectActivity;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Adapters.SubjectAdapter;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;
import com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking.SubjectModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.ClassModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.TeacherModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.UserModel;

import java.util.ArrayList;

public class TeacherClassManagementFragment extends Fragment {

    private UserModel currentUser;
    private SubjectAdapter subjectAdapter;
    ArrayList<SubjectModel> subjectList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_teacher_class_management, container, false);

        CardView assignedClass = v.findViewById(R.id.assignedClass);
        TextView txtClass = v.findViewById(R.id.txtClass);
        ConstraintLayout classC = v.findViewById(R.id.classC);

        assignedClass.setVisibility(View.GONE);
        txtClass.setVisibility(View.GONE);
        classC.setVisibility(View.GONE);

        currentUser = (new Gson()).fromJson(getActivity().getIntent().getStringExtra(StringExtras.CURRENT_USER), TeacherModel.class);
        if (currentUser == null || currentUser.getUserType()!= UserType.TEACHER)
            getActivity().finish();

        if (((TeacherModel) currentUser).getClassId() != null)
            if (!((TeacherModel) currentUser).getClassId().equals(""))
                FirebaseFirestore.getInstance().collection(CollectionName.CLASS)
                        .document(((TeacherModel) currentUser).getClassId())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot != null) {
                                ClassModel classModel = documentSnapshot.toObject(ClassModel.class);

                                assert classModel != null;
                                classModel.setId(documentSnapshot.getId());
                                txtClass.setText(classModel.getName());

                                assignedClass.setVisibility(View.VISIBLE);
                                txtClass.setVisibility(View.VISIBLE);
                                classC.setVisibility(View.VISIBLE);

                                classC.setOnClickListener(v1 -> startActivity(new Intent(getContext(), ManageStudentsActivity.class)
                                        .putExtra(StringExtras.CLASS, (new Gson()).toJson(classModel))));
                            }
                        })
                        .addOnFailureListener(e -> new AlertDialog.Builder(getContext(), R.style.CustomDialogTheme)
                                .setIcon(R.drawable.ic_error_outline)
                                .setTitle("Error")
                                .setMessage(e.getMessage())
                                .setPositiveButton("Ok", null)
                                .show());

        initSubjectRecycler(v);
        getSubjects();

        return v;
    }

    private void initSubjectRecycler(View view) {
        RecyclerView subjectRecycler = view.findViewById(R.id.subjectsRecycler);
        subjectRecycler.setHasFixedSize(true);
        subjectAdapter = new SubjectAdapter(subjectList, false, false);
        RecyclerView.LayoutManager notificationLayoutManager = new LinearLayoutManager(getContext());
        subjectRecycler.setLayoutManager(notificationLayoutManager);
        subjectRecycler.setAdapter(subjectAdapter);
        subjectAdapter.setOnItemClickListener(new SubjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                startActivity(new Intent(getContext(), TeacherSubjectActivity.class)
                        .putExtra(StringExtras.SUBJECT, (new Gson()).toJson(subjectList.get(position)))
                        .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser)));
            }

            @Override
            public void onEditClick(int position) {

            }
        });
    }

    private void getSubjects() {
        FirebaseFirestore.getInstance().collection(CollectionName.SUBJECTS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots!= null) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            SubjectModel subjectModel = documentSnapshot.toObject(SubjectModel.class);
                            assert subjectModel != null;
                            subjectModel.setId(documentSnapshot.getId());
                            for (String id : ((TeacherModel) currentUser).getSubjects()) {
                                if (id.equals(subjectModel.getId())) {
                                    boolean exists = false;
                                    for (SubjectModel s : subjectList) {
                                        if (subjectModel.getId().equals(s.getId())) {
                                            exists = true;
                                            break;
                                        }
                                    }
                                    if (!exists)
                                        subjectList.add(subjectModel);

                                    break;
                                }
                            }
                        }

                        subjectAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> new AlertDialog.Builder(getContext(), R.style.CustomDialogTheme)
                        .setIcon(R.drawable.ic_error_outline)
                        .setTitle("Error")
                        .setMessage("Unable to get class info\n" + e.getMessage())
                        .setPositiveButton("Ok", null)
                        .show());
    }
}