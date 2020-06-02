package com.yukicide.theacademiclinkandroid.Repositories.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yukicide.theacademiclinkandroid.R;

import java.io.IOException;
import java.util.ArrayList;

public class GradeListAdapter extends RecyclerView.Adapter<GradeListAdapter.NotificationViewHolder> {
    private ArrayList<Integer> NotificationList;
    private OnItemClickListener audioListener;
    private OnItemLongClickListener audioMenuListener;

    public interface OnItemClickListener {
        void onItemClick(int position) throws IOException;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        audioListener = listener;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position) throws IOException;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        audioMenuListener = listener;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView grade;

        NotificationViewHolder(View itemView, final OnItemClickListener listener, final OnItemLongClickListener menuListener) {
            super(itemView);
            grade = itemView.findViewById(R.id.txtGrade);

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

    public GradeListAdapter(ArrayList<Integer> exampleList) {
        NotificationList = exampleList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_grade_subjects, viewGroup, false);
        NotificationViewHolder evh = new NotificationViewHolder(v, audioListener, audioMenuListener);
        return evh;
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder notificationViewHolder, int i) {
        int currentItem = NotificationList.get(i);

        notificationViewHolder.grade.setText("Grade " + currentItem);
    }

    @Override
    public int getItemCount() {
        return NotificationList.size();
    }

}
