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
import com.yukicide.theacademiclinkandroid.Repositories.Models.Notes.NotesModel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotificationViewHolder> {
    private ArrayList<NotesModel> NotificationList;
    private NotesAdapter.OnItemClickListener audioListener;
    private NotesAdapter.OnItemLongClickListener audioMenuListener;

    public interface OnItemClickListener {
        void onItemClick(int position) throws IOException;
    }

    public void setOnItemClickListener(NotesAdapter.OnItemClickListener listener) {
        audioListener = listener;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position) throws IOException;
    }

    public void setOnItemLongClickListener(NotesAdapter.OnItemLongClickListener listener) {
        audioMenuListener = listener;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView notesTitle;
        TextView notesDetails;
        TextView notesUDate;

        NotificationViewHolder(View itemView, final NotesAdapter.OnItemClickListener listener, final NotesAdapter.OnItemLongClickListener menuListener) {
            super(itemView);
            notesTitle = itemView.findViewById(R.id.txtNotesTitle);
            notesDetails = itemView.findViewById(R.id.txtNotesDetails);
            notesUDate = itemView.findViewById(R.id.txtNotesUploadDate);

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

    public NotesAdapter(ArrayList<NotesModel> exampleList) {
        NotificationList = exampleList;
    }

    @NonNull
    @Override
    public NotesAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notes, viewGroup, false);
        NotesAdapter.NotificationViewHolder evh = new NotesAdapter.NotificationViewHolder(v, audioListener, audioMenuListener);
        return evh;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.NotificationViewHolder notificationViewHolder, int i) {
        NotesModel currentItem = NotificationList.get(i);

        notificationViewHolder.notesTitle.setText(currentItem.getName());

        if (currentItem.getDocuments().size() == 1)
            notificationViewHolder.notesDetails.setText(String.format("%d Resource", currentItem.getDocuments().size()));
        else
            notificationViewHolder.notesDetails.setText(String.format("%d Resources", currentItem.getDocuments().size()));

        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        notificationViewHolder.notesUDate.setText(sdf.format(currentItem.getUploaded().getTime()));
    }

    @Override
    public int getItemCount() {
        return NotificationList.size();
    }

}
