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
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.ClassModel;

import java.io.IOException;
import java.util.ArrayList;

public class ClassAdapter  extends RecyclerView.Adapter<ClassAdapter.NotificationViewHolder> {
    private ArrayList<ClassModel> ClassList;
    private ClassAdapter.OnItemClickListener audioListener;
    private ClassAdapter.OnItemLongClickListener audioMenuListener;

    public interface OnItemClickListener {
        void onItemClick(int position) throws IOException;
    }

    public void setOnItemClickListener(ClassAdapter.OnItemClickListener listener) {
        audioListener = listener;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position) throws IOException;
    }

    public void setOnItemLongClickListener(ClassAdapter.OnItemLongClickListener listener) {
        audioMenuListener = listener;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView txtClassName;

        NotificationViewHolder(View itemView, final ClassAdapter.OnItemClickListener listener, final ClassAdapter.OnItemLongClickListener menuListener) {
            super(itemView);
            txtClassName = itemView.findViewById(R.id.txtName);

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

    public ClassAdapter(ArrayList<ClassModel> exampleList) {
        ClassList = exampleList;
    }

    @NonNull
    @Override
    public ClassAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_class, viewGroup, false);
        ClassAdapter.NotificationViewHolder evh = new ClassAdapter.NotificationViewHolder(v, audioListener, audioMenuListener);
        return evh;
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull ClassAdapter.NotificationViewHolder notificationViewHolder, int i) {
        ClassModel currentItem = ClassList.get(i);

        notificationViewHolder.txtClassName.setText(currentItem.getName());
    }

    @Override
    public int getItemCount() {
        return ClassList.size();
    }

}

