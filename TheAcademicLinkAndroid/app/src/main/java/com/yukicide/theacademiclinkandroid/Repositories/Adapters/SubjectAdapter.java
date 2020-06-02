package com.yukicide.theacademiclinkandroid.Repositories.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking.SubjectModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.ClassModel;

import java.io.IOException;
import java.util.ArrayList;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.NotificationViewHolder> {
    private ArrayList<SubjectModel> NotificationList;
    private boolean mandatoryList;
    private final boolean isAdmin;
    private SubjectAdapter.OnItemClickListener audioListener;
    private SubjectAdapter.OnItemLongClickListener audioMenuListener;

    public interface OnItemClickListener {
        void onItemClick(int position) throws IOException;
        void onEditClick(int position);
    }

    public void setOnItemClickListener(SubjectAdapter.OnItemClickListener listener) {
        audioListener = listener;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position) throws IOException;
    }

    public void setOnItemLongClickListener(SubjectAdapter.OnItemLongClickListener listener) {
        audioMenuListener = listener;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView subjectTitle;
        CheckBox isMandatory;
        ImageView edit;

        NotificationViewHolder(View itemView, final SubjectAdapter.OnItemClickListener listener, final SubjectAdapter.OnItemLongClickListener menuListener) {
            super(itemView);
            subjectTitle = itemView.findViewById(R.id.txtSubjectTitle);
            isMandatory = itemView.findViewById(R.id.isMandatory);
            isMandatory.setClickable(false);
            edit = itemView.findViewById(R.id.btnEdit);

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

            edit.setOnClickListener(v -> {
                if (listener !=null) {
                    int position = getAdapterPosition();
                    if (position!= RecyclerView.NO_POSITION) {
                        listener.onEditClick(position);
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

    public SubjectAdapter(ArrayList<SubjectModel> exampleList, boolean mandatoryList, boolean isAdmin) {
        NotificationList = exampleList;
        this.mandatoryList = mandatoryList;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public SubjectAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_subject, viewGroup, false);
        return new NotificationViewHolder(v, audioListener, audioMenuListener);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull SubjectAdapter.NotificationViewHolder notificationViewHolder, int i) {
        SubjectModel currentItem = NotificationList.get(i);

        notificationViewHolder.subjectTitle.setText(currentItem.getName());
        if (!mandatoryList) {
            notificationViewHolder.isMandatory.setVisibility(View.GONE);
            notificationViewHolder.edit.setVisibility(View.VISIBLE);
        } else {
            notificationViewHolder.isMandatory.setChecked(currentItem.isMandatory());
            notificationViewHolder.edit.setVisibility(View.GONE);
        }

        if (!isAdmin) {
            notificationViewHolder.isMandatory.setVisibility(View.GONE);
            notificationViewHolder.edit.setVisibility(View.GONE);
        }

        if (currentItem.getClassId() != null) {
            /*notificationViewHolder.subjectTitle.append("\nGetting class details...");
            FirebaseFirestore.getInstance().collection(CollectionName.CLASS)
                    .document(currentItem.getClassId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot != null) {
                            ClassModel c = documentSnapshot.toObject(ClassModel.class);
                            assert c != null;
                            notificationViewHolder.subjectTitle.setText(currentItem.getName() + " - " + c.getName());
                        } else {
                            notificationViewHolder.subjectTitle.setText(currentItem.getName() + "\nUnable to get class details\nGrade " + currentItem.getGrade());
                        }
                    })
                    .addOnFailureListener(e -> notificationViewHolder.subjectTitle.setText(currentItem.getName() + "\nUnable to get class details\nGrade " + currentItem.getGrade()));*/

            notificationViewHolder.subjectTitle.setText(currentItem.getName() + " (" + currentItem.getClassId() + ")");
        } else {
            //notificationViewHolder.subjectTitle.append(" - Grade " + currentItem.getGrade());
        }
    }

    @Override
    public int getItemCount() {
        return NotificationList.size();
    }

}
