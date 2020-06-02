package com.yukicide.theacademiclinkandroid.AppUI.adminUI.notificationsCRUD;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.NotificationRank;
import com.yukicide.theacademiclinkandroid.Repositories.Models.NotificationModel;
import com.yukicide.theacademiclinkandroid.Repositories.UIElements.MyProgressDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddNotificationActivity extends AppCompatActivity {
    TextInputLayout title, details;

    ImageView btnDate;
    TextView txtDate;
    TextView txtEventDate;

    Switch event, urgent;

    Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notification);

        btnDate = findViewById(R.id.btnDate);
        txtDate = findViewById(R.id.txtDate);
        txtEventDate = findViewById(R.id.txtEventDate);
        btnDate.setVisibility(View.GONE);
        txtDate.setVisibility(View.GONE);
        txtEventDate.setVisibility(View.GONE);

        title = findViewById(R.id.txtNotificationTitle);
        details = findViewById(R.id.txtNotificationDetails);

        event = findViewById(R.id.isEvent);
        urgent = findViewById(R.id.isUrgent);

        event.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (event.isChecked()) {
                btnDate.setVisibility(View.VISIBLE);
                txtDate.setVisibility(View.VISIBLE);
                txtEventDate.setVisibility(View.VISIBLE);
            } else {
                btnDate.setVisibility(View.GONE);
                txtDate.setVisibility(View.GONE);
                txtEventDate.setVisibility(View.GONE);
            }
        });

        Button save = findViewById(R.id.btnSave);
        save.setOnClickListener(v -> {
            if (title.getEditText().getText().toString().isEmpty()) {
                title.getEditText().setError("Notification Title cannot be empty!");
                title.getEditText().requestFocus();
                return;
            }

            if (details.getEditText().getText().toString().isEmpty()) {
                details.getEditText().setError("Notification Title cannot be empty!");
                details.getEditText().requestFocus();
                return;
            }

            if (event.isChecked())
                if (date == null) {
                    new AlertDialog.Builder(AddNotificationActivity.this, R.style.CustomDialogTheme)
                            .setIcon(R.drawable.ic_error_outline)
                            .setTitle("Event date missing!")
                            .setMessage("Please select a date on which the event will take place!")
                            .setPositiveButton("Ok", null)
                            .show();
                    return;
                }

            NotificationModel tempNot = new NotificationModel();

            tempNot.setTitle(title.getEditText().getText().toString());
            tempNot.setDetails(details.getEditText().getText().toString());
            if (event.isChecked()) {
                tempNot.setEvent(true);
                tempNot.setDate(date);
            }
            if (urgent.isChecked()) {
                tempNot.setRank(NotificationRank.URGENT);
            } else {
                tempNot.setRank(NotificationRank.GENERAL);
            }

            MyProgressDialog progressDialog = new MyProgressDialog(AddNotificationActivity.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();

            FirebaseFirestore.getInstance()
                    .collection(CollectionName.NOTIFICATIONS)
                    .add(tempNot)
                    .addOnSuccessListener(documentReference -> {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(AddNotificationActivity.this, R.style.CustomDialogTheme)
                                .setIcon(R.drawable.ic_success)
                                .setTitle("Success")
                                .setMessage("Notification added.\n\n" + "Would you like to add another notification?")
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    Objects.requireNonNull(title.getEditText()).setText("");
                                    Objects.requireNonNull(details.getEditText()).setText("");
                                    urgent.setChecked(false);
                                    event.setChecked(false);
                                })
                                .setNegativeButton("No", (dialog, which) -> finish())
                                .show();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(AddNotificationActivity.this, R.style.CustomDialogTheme)
                                .setIcon(R.drawable.ic_error_outline)
                                .setTitle("Error")
                                .setMessage(e.getMessage())
                                .setPositiveButton("Yes", null)
                                .setNegativeButton("No", (dialog, which) -> finish())
                                .show();
                    });
        });

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCal.set(Calendar.YEAR, year);
                myCal.set(Calendar.YEAR, year);
                myCal.set(Calendar.YEAR, year);

                upDateLabel(myCal.getTime());
            }
        };

        View.OnClickListener onClickListener = v -> new DatePickerDialog(AddNotificationActivity.this, dateSetListener, myCal.get(Calendar.YEAR), myCal.get(Calendar.MONTH), myCal.get(Calendar.DAY_OF_MONTH)).show();
        btnDate.setOnClickListener(onClickListener);
    }

    final Calendar myCal = Calendar.getInstance();
    private void upDateLabel(Date tempDate) {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        date = tempDate;

        txtEventDate.setText(sdf.format(myCal.getTime()));
    }
}
