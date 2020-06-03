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
import com.yukicide.theacademiclinkandroid.Repositories.Models.Notes.AttachmentModel;

import java.io.IOException;
import java.util.ArrayList;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.NotificationViewHolder> {
    private ArrayList<AttachmentModel> DocumentList;
    private DocumentAdapter.OnItemClickListener audioListener;
    private DocumentAdapter.OnItemLongClickListener audioMenuListener;

    public interface OnItemClickListener {
        void onItemClick(int position) throws IOException;
        void onMoreClick(int position);
    }

    public void setOnItemClickListener(DocumentAdapter.OnItemClickListener listener) {
        audioListener = listener;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position) throws IOException;
    }

    public void setOnItemLongClickListener(DocumentAdapter.OnItemLongClickListener listener) {
        audioMenuListener = listener;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView docTitle;
        ImageView docDismiss;

        NotificationViewHolder(View itemView, final DocumentAdapter.OnItemClickListener listener, final DocumentAdapter.OnItemLongClickListener menuListener) {
            super(itemView);
            docTitle = itemView.findViewById(R.id.txtDocTitle);
            docDismiss = itemView.findViewById(R.id.btnDocDismiss);

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

            docDismiss.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onMoreClick(position);
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

    public DocumentAdapter(ArrayList<AttachmentModel> exampleList) {
        DocumentList = exampleList;
    }

    @NonNull
    @Override
    public DocumentAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notes_doc, viewGroup, false);
        DocumentAdapter.NotificationViewHolder evh = new DocumentAdapter.NotificationViewHolder(v, audioListener, audioMenuListener);
        return evh;
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull DocumentAdapter.NotificationViewHolder notificationViewHolder, int i) {
        String currentItem = DocumentList.get(i).getUrl();

        notificationViewHolder.docTitle.setText(currentItem);
    }

    @Override
    public int getItemCount() {
        return DocumentList.size();
    }

}
