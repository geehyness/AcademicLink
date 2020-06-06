package com.yukicide.theacademiclinkandroid.Repositories.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Notes.AssignmentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Notes.NotesModel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AssignmentsAdapter extends RecyclerView.Adapter<AssignmentsAdapter.NotificationViewHolder> {
    private ArrayList<AssignmentModel> NotificationList;
    private AssignmentsAdapter.OnItemClickListener audioListener;
    private AssignmentsAdapter.OnItemLongClickListener audioMenuListener;

    public interface OnItemClickListener {
        void onItemClick(int position) throws IOException;
    }

    public void setOnItemClickListener(AssignmentsAdapter.OnItemClickListener listener) {
        audioListener = listener;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position) throws IOException;
    }

    public void setOnItemLongClickListener(AssignmentsAdapter.OnItemLongClickListener listener) {
        audioMenuListener = listener;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView assignmentsTitle;
        TextView assignmentsDetails;
        TextView assignmentsUDate;

        NotificationViewHolder(View itemView, final AssignmentsAdapter.OnItemClickListener listener, final AssignmentsAdapter.OnItemLongClickListener menuListener) {
            super(itemView);
            assignmentsTitle = itemView.findViewById(R.id.txtNotesTitle);
            assignmentsDetails = itemView.findViewById(R.id.txtNotesDetails);
            assignmentsUDate = itemView.findViewById(R.id.txtNotesUploadDate);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        try {
                            listener.onItemClick(position);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(v.getContext(), "Unable to show menu!\nPlease report error in 'Help' menu.", Toast.LENGTH_SHORT).show();
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

    public AssignmentsAdapter(ArrayList<AssignmentModel> exampleList) {
        NotificationList = exampleList;
    }

    @NonNull
    @Override
    public AssignmentsAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notes, viewGroup, false);
        AssignmentsAdapter.NotificationViewHolder evh = new AssignmentsAdapter.NotificationViewHolder(v, audioListener, audioMenuListener);
        return evh;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull AssignmentsAdapter.NotificationViewHolder notificationViewHolder, int i) {
        AssignmentModel currentItem = NotificationList.get(i);

        notificationViewHolder.assignmentsTitle.setText(currentItem.getName());

        notificationViewHolder.assignmentsDetails.setText(currentItem.getDocuments().getName());

        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        notificationViewHolder.assignmentsUDate.setText("Due by: " + sdf.format(currentItem.getDate().getTime()));
    }

    @Override
    public int getItemCount() {
        return NotificationList.size();
    }

}