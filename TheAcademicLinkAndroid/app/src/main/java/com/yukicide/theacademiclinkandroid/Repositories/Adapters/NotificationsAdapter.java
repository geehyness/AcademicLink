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
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.NotificationRank;
import com.yukicide.theacademiclinkandroid.Repositories.Models.NotificationModel;

import java.io.IOException;
import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {
    private ArrayList<NotificationModel> NotificationList;
    private OnItemClickListener audioListener;
    private OnItemLongClickListener audioMenuListener;

    public interface OnItemClickListener {
        void onItemClick(int position) throws IOException;
        void onMoreClick(int position);
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
        TextView notificationTitle;
        TextView notificationDetails;
        ImageView notificationDismiss;

        NotificationViewHolder(View itemView, final OnItemClickListener listener, final OnItemLongClickListener menuListener) {
            super(itemView);
            notificationTitle = itemView.findViewById(R.id.txtNotificationTitle);
            notificationDetails = itemView.findViewById(R.id.txtNotificationDetails);
            notificationDismiss = itemView.findViewById(R.id.btnNotificationDismiss);

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

            notificationDismiss.setOnClickListener(v -> {
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

    public NotificationsAdapter(ArrayList<NotificationModel> exampleList) {
        NotificationList = exampleList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notification, viewGroup, false);
        NotificationViewHolder evh = new NotificationViewHolder(v, audioListener, audioMenuListener);
        return evh;
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder notificationViewHolder, int i) {
        NotificationModel currentItem = NotificationList.get(i);

        notificationViewHolder.notificationTitle.setText("");

        if (currentItem.getRank().equals(NotificationRank.URGENT))
            notificationViewHolder.notificationTitle.setText("(URGENT) ");

        notificationViewHolder.notificationTitle.append(currentItem.getTitle());
        notificationViewHolder.notificationDetails.setText(currentItem.getDetails());
    }

    @Override
    public int getItemCount() {
        return NotificationList.size();
    }

}
