package com.yukicide.theacademiclinkandroid.Repositories.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.AppUI.adminUI.subjectsCRUD.EditSubject;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking.SubjectCollectionModel;

import java.io.IOException;
import java.util.ArrayList;

public class SubjectCollectionAdapter extends RecyclerView.Adapter<SubjectCollectionAdapter.NotificationViewHolder> {
    private RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
    private ArrayList<SubjectCollectionModel> subjectCollectionList;
    private SubjectCollectionAdapter.OnItemClickListener audioListener;
    private SubjectCollectionAdapter.OnItemLongClickListener audioMenuListener;

    Context mContext;

    public interface OnItemClickListener {
        void onItemClick(int position) throws IOException;
    }

    public void setOnItemClickListener(SubjectCollectionAdapter.OnItemClickListener listener) {
        audioListener = listener;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position) throws IOException;
    }

    public void setOnItemLongClickListener(SubjectCollectionAdapter.OnItemLongClickListener listener) {
        audioMenuListener = listener;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView grade;
        RecyclerView gradeRecycler;
        ImageView btnMore;
        boolean isShown = false;

        NotificationViewHolder(View itemView, final SubjectCollectionAdapter.OnItemClickListener listener, final SubjectCollectionAdapter.OnItemLongClickListener menuListener) {
            super(itemView);
            grade = itemView.findViewById(R.id.txtSubjectName);
            gradeRecycler = itemView.findViewById(R.id.subjectsRecycler);
            btnMore = itemView.findViewById(R.id.btnMore);

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
                            Toast.makeText(v.getContext(), "Unable to play message!\nPlease report error in 'Help' menu.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                return false;
            });
        }
    }

    public SubjectCollectionAdapter(ArrayList<SubjectCollectionModel> exampleList, Context mContext) {
        subjectCollectionList = exampleList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public SubjectCollectionAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_subject_collection, viewGroup, false);
        return new SubjectCollectionAdapter.NotificationViewHolder(v, audioListener, audioMenuListener);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull SubjectCollectionAdapter.NotificationViewHolder gradeViewHolder, int i) {
        SubjectCollectionModel currentItem = subjectCollectionList.get(i);

        gradeViewHolder.grade.setText("Grade " + currentItem.getCollectionGrade());

        gradeViewHolder.gradeRecycler.setVisibility(View.GONE);

        SubjectAdapter gradeAdapter;
        //gradeViewHolder.gradeRecycler.setHasFixedSize(true);
        gradeAdapter = new SubjectAdapter(currentItem.getSubjectList(), false, true);
        LinearLayoutManager notificationLayoutManager = new LinearLayoutManager(gradeViewHolder.gradeRecycler.getContext());
        notificationLayoutManager.setInitialPrefetchItemCount(currentItem.getSubjectList().size());

        gradeViewHolder.gradeRecycler.setLayoutManager(notificationLayoutManager);
        gradeViewHolder.gradeRecycler.setAdapter(gradeAdapter);
        gradeViewHolder.gradeRecycler.setRecycledViewPool(recycledViewPool);
        gradeAdapter.setOnItemClickListener(new SubjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) throws IOException {

            }

            @Override
            public void onEditClick(int position) {
                mContext.startActivity(new Intent(mContext, EditSubject.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(StringExtras.SUBJECT, (new Gson()).toJson(currentItem.getSubjectList().get(position))));
            }
        });

        gradeViewHolder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gradeViewHolder.isShown) {
                    gradeViewHolder.gradeRecycler.setVisibility(View.VISIBLE);
                    gradeViewHolder.btnMore.setImageResource(R.drawable.ic_keyboard_arrow_up);
                } else {
                    gradeViewHolder.gradeRecycler.setVisibility(View.GONE);
                    gradeViewHolder.btnMore.setImageResource(R.drawable.ic_keyboard_arrow_down);
                }

                gradeViewHolder.isShown = !(gradeViewHolder.isShown);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectCollectionList.size();
    }

}
