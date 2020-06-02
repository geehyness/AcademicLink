package com.yukicide.theacademiclinkandroid.Repositories.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.AppUI.adminUI.classCRUD.ViewClassActivity;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking.GradeModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.ClassModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.UserModel;

import java.io.IOException;
import java.util.ArrayList;

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.NotificationViewHolder> {
    private RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
    private ArrayList<GradeModel> GradeList;
    private GradeAdapter.OnItemClickListener audioListener;
    private GradeAdapter.OnItemLongClickListener audioMenuListener;

    Context mContext;
    UserModel currentUser;

    public interface OnItemClickListener {
        void onItemClick(int position) throws IOException;
    }

    public void setOnItemClickListener(GradeAdapter.OnItemClickListener listener) {
        audioListener = listener;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position) throws IOException;
    }

    public void setOnItemLongClickListener(GradeAdapter.OnItemLongClickListener listener) {
        audioMenuListener = listener;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView grade;
        RecyclerView gradeRecycler;

        NotificationViewHolder(View itemView, final GradeAdapter.OnItemClickListener listener, final GradeAdapter.OnItemLongClickListener menuListener) {
            super(itemView);
            grade = itemView.findViewById(R.id.txtGrade);
            gradeRecycler = itemView.findViewById(R.id.gradeRecycler);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        try {
                            listener.onItemClick(position);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (menuListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        try {
                            menuListener.onItemLongClick(position);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return false;
            });
        }
    }

    public GradeAdapter(ArrayList<GradeModel> exampleList, Context mContext, UserModel currentUser) {
        GradeList = exampleList;
        this.mContext = mContext;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public GradeAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_grade, viewGroup, false);
        return new NotificationViewHolder(v, audioListener, audioMenuListener);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull GradeAdapter.NotificationViewHolder gradeViewHolder, int i) {
        GradeModel currentItem = GradeList.get(i);

        gradeViewHolder.grade.setText("Grade " + currentItem.getGrade());

        ClassAdapter gradeAdapter;
        //gradeViewHolder.gradeRecycler.setHasFixedSize(true);
        gradeAdapter = new ClassAdapter(currentItem.getClasses());
        GridLayoutManager notificationLayoutManager = new GridLayoutManager(gradeViewHolder.gradeRecycler.getContext(), 2);
        notificationLayoutManager.setInitialPrefetchItemCount(currentItem.getClasses().size());

        gradeViewHolder.gradeRecycler.setLayoutManager(notificationLayoutManager);
        gradeViewHolder.gradeRecycler.setAdapter(gradeAdapter);
        gradeViewHolder.gradeRecycler.setRecycledViewPool(recycledViewPool);
        gradeAdapter.setOnItemClickListener(position -> {
            ClassModel selectedClass = currentItem.getClasses().get(position);
            mContext.startActivity(new Intent(mContext, ViewClassActivity.class)
                .putExtra(StringExtras.CLASS, (new Gson()).toJson(selectedClass))
                .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser)));
        });
    }

    @Override
    public int getItemCount() {
        return GradeList.size();
    }

}
