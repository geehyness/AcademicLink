package com.yukicide.theacademiclinkandroid.Repositories.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.StudentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.UserModel;

import java.io.IOException;
import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.NotificationViewHolder> {
    private ArrayList<UserModel> NotificationList;
    private UserAdapter.OnItemClickListener audioListener;
    private UserAdapter.OnItemLongClickListener audioMenuListener;

    public interface OnItemClickListener {
        void onItemClick(int position) throws IOException;
    }

    public void setOnItemClickListener(UserAdapter.OnItemClickListener listener) {
        audioListener = listener;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position) throws IOException;
    }

    public void setOnItemLongClickListener(UserAdapter.OnItemLongClickListener listener) {
        audioMenuListener = listener;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView studentPic;
        TextView studentName;
        TextView studentExtra;

        NotificationViewHolder(View itemView, final UserAdapter.OnItemClickListener listener, final UserAdapter.OnItemLongClickListener menuListener) {
            super(itemView);
            studentPic = itemView.findViewById(R.id.studentPic);
            studentName = itemView.findViewById(R.id.txtStudentName);
            studentExtra = itemView.findViewById(R.id.txtStudentExtra);

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

    public UserAdapter(ArrayList<UserModel> exampleList) {
        NotificationList = exampleList;
    }

    @NonNull
    @Override
    public UserAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user, viewGroup, false);
        UserAdapter.NotificationViewHolder evh = new UserAdapter.NotificationViewHolder(v, audioListener, audioMenuListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.NotificationViewHolder notificationViewHolder, int i) {
        UserModel currentItem = NotificationList.get(i);

        // TODO: 2020/02/22 DISPLAY PIC


        notificationViewHolder.studentName.setText(String.format("%s %s", currentItem.getFirstName(), currentItem.getSurname()));
        notificationViewHolder.studentExtra.setText(currentItem.getEmail());
    }

    @Override
    public int getItemCount() {
        return NotificationList.size();
    }

}
