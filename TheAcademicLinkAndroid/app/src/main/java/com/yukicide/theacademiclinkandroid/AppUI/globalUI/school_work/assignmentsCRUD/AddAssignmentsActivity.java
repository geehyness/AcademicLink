package com.yukicide.theacademiclinkandroid.AppUI.globalUI.school_work.assignmentsCRUD;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.AppUI.globalUI.school_work.notesCRUD.AddNotesActivity;
import com.yukicide.theacademiclinkandroid.AppUI.globalUI.user_management.EditProfileActivity;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Notes.AssignmentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Notes.AttachmentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking.SubjectModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.TeacherModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.UserModel;

import org.checkerframework.checker.units.qual.A;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddAssignmentsActivity extends AppCompatActivity {
    private UserModel currentUser;
    private SubjectModel subjectModel;
    private TeacherModel teacher;
    private ProgressBar progressBarUpload;
    private boolean selectingFile;
    private static final int REQ_CODE_PICK_DOC_FILE = 2;
    private Uri doc = null;
    private StorageReference storageReference;
    private StorageTask uploadTask;
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assignments);

        date = new Date();
        DatePicker datePicker = findViewById(R.id.datePicker1);

        subjectModel = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.SUBJECT), SubjectModel.class);
        currentUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), UserModel.class);

        if (currentUser == null || !currentUser.getUserType().equals(UserType.TEACHER))
            finish();
        else if (currentUser.getUserType().equals(UserType.TEACHER))
            teacher = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), TeacherModel.class);

        storageReference = FirebaseStorage.getInstance().getReference("assignments/" + subjectModel.getId());

        ImageView addDoc = findViewById(R.id.btnAddDoc);
        addDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectingFile) {
                    selectingFile = true;
                    Intent intent;
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    startActivityForResult(Intent.createChooser(intent, "Pick display logo"), REQ_CODE_PICK_DOC_FILE);
                }
            }
        });

        Button save = findViewById(R.id.btnSave);
        save.setOnClickListener(v -> {
            /*DatePicker datePicker = findViewById(R.id.datePicker1);*/
            // TODO: 2020/06/06 GET SELECTED YEAR
            date = new Date((new Date()).getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

            TextInputLayout txtTitle = findViewById(R.id.txtNotesTitle),
                    txtDetails = findViewById(R.id.txtNotesDetails);

            if (txtTitle.getEditText().getText().toString().isEmpty()) {
                txtTitle.getEditText().requestFocus();
                txtTitle.getEditText().setError("Title cannot be empty.");
                return;
            } else if (txtDetails.getEditText().getText().toString().isEmpty()) {
                txtDetails.getEditText().requestFocus();
                txtDetails.getEditText().setError("Details cannot be empty.");
                return;
            }

            if (date == null) {
                new AlertDialog.Builder(AddAssignmentsActivity.this, R.style.CustomDialogTheme)
                        .setIcon(R.drawable.ic_error_outline)
                        .setTitle("Error")
                        .setMessage("Please pick a due date!")
                        .setPositiveButton("Ok", null)
                        .show();
                return;
            } else if (date.getTime() < (new Date()).getTime()) {
                new AlertDialog.Builder(AddAssignmentsActivity.this, R.style.CustomDialogTheme)
                        .setIcon(R.drawable.ic_error_outline)
                        .setTitle("Error")
                        .setMessage("Selected date is today or has passed!")
                        .setPositiveButton("Ok", null)
                        .show();
                return;
            }

            if (doc == null) {
                new AlertDialog.Builder(AddAssignmentsActivity.this, R.style.CustomDialogTheme)
                        .setIcon(R.drawable.ic_error_outline)
                        .setTitle("Error")
                        .setMessage("Please select a PDF file containing the assignment details!")
                        .setPositiveButton("Ok", null)
                        .show();
                return;
            }

            progressBarUpload.setVisibility(View.VISIBLE);

            String myFormat = "dd-MM-yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

            final StorageReference fileRef = storageReference.child(sdf.format(new Date()) + "/" + getFileName(doc));
            uploadTask = fileRef.putFile(doc)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        AttachmentModel document = new AttachmentModel(getFileName(doc), uri.toString(), true);
                        AssignmentModel newA = new AssignmentModel(txtTitle.getEditText().getText().toString(),
                                txtDetails.getEditText().getText().toString(), subjectModel.getId(), date, document, currentUser.getId());

                        FirebaseFirestore ff = FirebaseFirestore.getInstance();
                        ff.collection(CollectionName.ASSIGNMENTS).add(newA)
                                .addOnSuccessListener(documentReference -> finish())
                                .addOnFailureListener(e -> new AlertDialog.Builder(AddAssignmentsActivity.this, R.style.CustomDialogTheme)
                                        .setIcon(R.drawable.ic_error_outline)
                                        .setTitle("Error")
                                        .setMessage(e.getMessage())
                                        .setPositiveButton("Ok", null)
                                        .show());
                    }))
                    .addOnFailureListener(e -> {
                        new AlertDialog.Builder(AddAssignmentsActivity.this, R.style.CustomDialogTheme)
                                .setIcon(R.drawable.ic_error_outline)
                                .setTitle("Error")
                                .setMessage(e.getMessage())
                                .setPositiveButton("Ok", null)
                                .show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            progressBarUpload.setProgress((int) progress, true);
                        } else {
                            progressBarUpload.setProgress((int) progress);
                        }
                    });
        });

        progressBarUpload = findViewById(R.id.progressBarUpload);
        progressBarUpload.setVisibility(View.GONE);

        storageReference = FirebaseStorage.getInstance().getReference("items/" + subjectModel.getId());

        ImageView back = findViewById(R.id.btnBack);
        back.setOnClickListener(v -> finish());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_PICK_DOC_FILE && resultCode == RESULT_OK) {
            if ((data != null) && (data.getData() != null)) {
                doc= data.getData();
                TextView txtDoc = findViewById(R.id.txtDocName);
                txtDoc.setText(getFileName(doc));
            }
        }

        selectingFile = false;
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }return result;
    }

    private String fileExtension(Uri uri){
        if (uri != null){
            ContentResolver cr = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(cr.getType(uri));
        } else {
            return null;
        }
    }
}
