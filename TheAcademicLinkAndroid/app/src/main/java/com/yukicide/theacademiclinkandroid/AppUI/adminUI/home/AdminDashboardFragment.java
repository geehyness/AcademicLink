package com.yukicide.theacademiclinkandroid.AppUI.adminUI.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.AppUI.adminUI.classCRUD.ManageClassActivity;
import com.yukicide.theacademiclinkandroid.AppUI.adminUI.notificationsCRUD.AddNotificationActivity;
import com.yukicide.theacademiclinkandroid.AppUI.adminUI.teacherCRUD.ManageTeachersActivity;
import com.yukicide.theacademiclinkandroid.AppUI.globalUI.user_management.ViewUserActivity;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Adapters.NotificationsAdapter;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Models.NotificationModel;
import com.yukicide.theacademiclinkandroid.AppUI.globalUI.information.NotificationActivity;

import java.util.ArrayList;
import java.util.Objects;

public class AdminDashboardFragment extends Fragment {

    private ArrayList<NotificationModel> notificationList = new ArrayList<>();
    private ArrayList<NotificationModel> displayNotificationList = new ArrayList<>();

    private NotificationsAdapter notificationAdapter;
    private NotificationsAdapter eventsAdapter;

    private RecyclerView eventsRecycler;
    private boolean hwShown = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        TextView uname = v.findViewById(R.id.txtUsername);
        TextView grade = v.findViewById(R.id.txtGrade);

        AdminHomeActivity activity = (AdminHomeActivity) getActivity();
        uname.setText(String.format("%s %s", Objects.requireNonNull(activity).currentUser.getFirstName(), Objects.requireNonNull(activity).currentUser.getSurname()));
        grade.setText(Objects.requireNonNull(activity).currentUser.getUserType().name().toLowerCase());

        initNotificationsRecycler(v);
        initEventRecycler(v);

        getNotifications(v);

        ImageView btnProfile = v.findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ViewUserActivity.class)
                    .putExtra(StringExtras.PROFILE_USER, (new Gson()).toJson(((AdminHomeActivity) Objects.requireNonNull(getActivity())).currentUser))
                    .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(((AdminHomeActivity) Objects.requireNonNull(getActivity())).currentUser)));
            }
        });

        CardView studentsCard = v.findViewById(R.id.cardStudents);
        CardView teachersCard = v.findViewById(R.id.cardTeachers);

        teachersCard.setOnClickListener(v1 -> startActivity(new Intent(getActivity(), ManageTeachersActivity.class)
                .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(((AdminHomeActivity) Objects.requireNonNull(getActivity())).currentUser))));
        studentsCard.setOnClickListener(v12 -> startActivity(new Intent(getActivity(), ManageClassActivity.class)
                    .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(((AdminHomeActivity) Objects.requireNonNull(getActivity())).currentUser))));

        FloatingActionButton notificationAdd = v.findViewById(R.id.notificationAdd);
        notificationAdd.setOnClickListener(v13 -> startActivity(new Intent(getActivity(), AddNotificationActivity.class)
                .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(((AdminHomeActivity) Objects.requireNonNull(getActivity())).currentUser))));

        return v;
    }

    private void getNotifications(View v) {
        FirebaseFirestore.getInstance()
                .collection(CollectionName.NOTIFICATIONS)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (queryDocumentSnapshots != null) {
                        notificationList.clear();
                        displayNotificationList.clear();

                        for (DocumentSnapshot d : queryDocumentSnapshots) {
                            NotificationModel tempNoti = d.toObject(NotificationModel.class);
                            assert tempNoti != null;
                            tempNoti.setId(d.getId());

                            boolean exists = false;
                            for (NotificationModel n : notificationList)
                                if (n.getId().equals(d.getId())) {
                                    exists = true;
                                    break;
                                }

                            if (!exists) {
                                boolean read = false;
                                for (String n : ((AdminHomeActivity) Objects.requireNonNull(getActivity())).currentUser.getNotificationsRead()) {
                                    if (tempNoti.getId().equals(n)) {
                                        read = true;
                                        break;
                                    }
                                }

                                if (!read) {
                                    notificationList.add(tempNoti);
                                }
                            }
                        }

                        displayNotificationList.addAll(notificationList);
                        notificationAdapter.notifyDataSetChanged();
                        checkNotifications(v);
                    }
                });
    }

    private void initNotificationsRecycler(View v) {
        RecyclerView notificationsRecycler = v.findViewById(R.id.notificationRecycler);
        notificationsRecycler.setHasFixedSize(false);
        notificationAdapter = new NotificationsAdapter(displayNotificationList);
        RecyclerView.LayoutManager notificationLayoutManager = new LinearLayoutManager(getContext());
        notificationsRecycler.setLayoutManager(notificationLayoutManager);
        notificationsRecycler.setAdapter(notificationAdapter);
        notificationAdapter.setOnItemClickListener(new NotificationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                startActivity(new Intent(getContext(), NotificationActivity.class)
                        .putExtra(StringExtras.NOTIFICATION, (new Gson()).toJson(displayNotificationList.get(position))));
            }

            @Override
            public void onMoreClick(int position) {
                displayNotificationList.remove(position);
                notificationAdapter.notifyItemRemoved(position);

                checkNotifications(v);
            }
        });
    }

    private void checkNotifications(View view) {
        TextView txtNotifications = view.findViewById(R.id.txtNotifications);
        if (displayNotificationList.isEmpty()) {
            txtNotifications.setText(R.string.no_notifications);
        } else {
            txtNotifications.setText(R.string.notifications);
        }
    }

    private void initEventRecycler(View v) {
        eventsRecycler = v.findViewById(R.id.eventsRecycler);
        eventsRecycler.setVisibility(View.GONE);

        ImageView btnShowEvent = v.findViewById(R.id.btnShowEvent);
        btnShowEvent.setOnClickListener(v1 -> {
            if (!hwShown) {
                btnShowEvent.setImageResource(R.drawable.ic_keyboard_arrow_up);
                eventsRecycler.setVisibility(View.VISIBLE);
                hwShown = true;
            } else {
                btnShowEvent.setImageResource(R.drawable.ic_keyboard_arrow_down);
                eventsRecycler.setVisibility(View.GONE);
                hwShown = false;
            }
        });

        eventsRecycler.setHasFixedSize(true);
        eventsAdapter = new NotificationsAdapter(displayNotificationList);
        RecyclerView.LayoutManager eventsLayoutManager = new LinearLayoutManager(getContext());
        eventsRecycler.setLayoutManager(eventsLayoutManager);
        eventsRecycler.setAdapter(notificationAdapter);
        notificationAdapter.setOnItemClickListener(new NotificationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                startActivity(new Intent(getContext(), NotificationActivity.class)
                        .putExtra(StringExtras.NOTIFICATION, (new Gson()).toJson(displayNotificationList.get(position))));
            }

            @Override
            public void onMoreClick(int position) {
                ((AdminHomeActivity) Objects.requireNonNull(getActivity())).currentUser.getNotificationsRead().add(displayNotificationList.get(position).getId());
                displayNotificationList.remove(position);
                notificationAdapter.notifyItemRemoved(position);

                FirebaseFirestore.getInstance()
                        .collection(CollectionName.USERS)
                        .document(((AdminHomeActivity) Objects.requireNonNull(getActivity())).currentUser.getId())
                        .set(((AdminHomeActivity) Objects.requireNonNull(getActivity())).currentUser)
                        .addOnFailureListener(e -> Log.e("bg_process", Objects.requireNonNull(e.getLocalizedMessage())));

                checkNotifications(v);
            }
        });
    }
}
