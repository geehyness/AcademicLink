package com.yukicide.theacademiclinkandroid.AppUI.globalUI.school_work.notesCRUD;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Adapters.DocumentAdapter;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Notes.AttachmentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Notes.NotesModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.ProgressTracking.SubjectModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.TeacherModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.UserModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AddNotesActivity extends AppCompatActivity {
    private UserModel currentUser;
    private SubjectModel subjectModel;
    private TeacherModel teacher;
    private boolean selectingFile;
    private static final int REQ_CODE_PICK_DOC_FILE = 2;

    ArrayList<Uri> docArray = new ArrayList<>();
    ArrayList<AttachmentModel> docStringArray = new ArrayList<>();
    ArrayList<AttachmentModel> onlineDocArray = new ArrayList<>();
    private ProgressBar progressBarUpload;

    private StorageReference storageReference;
    private StorageTask uploadTask;
    private DocumentAdapter docAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);

        subjectModel = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.SUBJECT), SubjectModel.class);
        currentUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), UserModel.class);

        if (currentUser == null || !currentUser.getUserType().equals(UserType.TEACHER))
            finish();
        else if (currentUser.getUserType().equals(UserType.TEACHER))
            teacher = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), TeacherModel.class);

        FloatingActionButton addDoc = findViewById(R.id.btnAddDoc);
        addDoc.setOnClickListener(this::showMenu);

        Button save = findViewById(R.id.btnSave);
        save.setOnClickListener(v -> {
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

            progressBarUpload.setVisibility(View.VISIBLE);
            uploadFile(0, txtTitle.getEditText().getText().toString(), txtDetails.getEditText().getText().toString());
        });

        progressBarUpload = findViewById(R.id.progressBarUpload);
        progressBarUpload.setVisibility(View.GONE);

        storageReference = FirebaseStorage.getInstance().getReference("notes/" + subjectModel.getId());

        docRecyclerInit();

        ImageView back = findViewById(R.id.btnBack);
        back.setOnClickListener(v -> finish());
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.addDoc){
                if (!selectingFile) {
                    selectingFile = true;
                    Intent intent;
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    startActivityForResult(Intent.createChooser(intent, "Pick display logo"), REQ_CODE_PICK_DOC_FILE);
                }
            } else if (item.getItemId() == R.id.addLink){
                    final EditText txtLink = new EditText(this);
                    txtLink.setHint("URL");

                    new AlertDialog.Builder(this)
                            .setTitle("Add Link")
                            .setMessage("Add url link to notes or learning material:")
                            .setView(txtLink)
                            .setCancelable(false)
                            .setPositiveButton("Add", (dialog, whichButton) -> {
                                if (!txtLink.getText().toString().isEmpty()) {
                                    AttachmentModel attachment = new AttachmentModel("", txtLink.getText().toString(), false);
                                    onlineDocArray.add(attachment);
                                    docStringArray.add(attachment);
                                } else {
                                    txtLink.requestFocus();
                                    txtLink.setError("Link cannot be empty!");
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }

            return false;
        });
        popup.inflate(R.menu.add_notes_menu);
        popup.show();
    }

    private void docRecyclerInit() {
        RecyclerView catRecyclerView = findViewById(R.id.docsRecycler);
        catRecyclerView.setHasFixedSize(false);
        docAdapter = new DocumentAdapter(docStringArray);
        RecyclerView.LayoutManager catLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        catRecyclerView.setLayoutManager(catLayoutManager);
        catRecyclerView.setAdapter(docAdapter);
        docAdapter.setOnItemClickListener(new DocumentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) throws IOException {
                if (onlineDocArray.contains(docStringArray.get(position))) {
                    // TODO: 2020/05/30 OPEN LINK

                } else {
                    Uri uri = docArray.get(position);
                    Intent intent;

                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(uri);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);

                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    } else {
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "application/pdf");
                        intent = Intent.createChooser(intent, "Open File");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }*/
                }
            }

            @Override
            public void onMoreClick(int position) {
                if (onlineDocArray.contains(docStringArray.get(position))) {
                    onlineDocArray.remove(onlineDocArray.indexOf(docStringArray.get(position)));
                    docStringArray.remove(position);
                    docAdapter.notifyItemRemoved(position);
                } else {
                    docStringArray.remove(position);
                    docArray.remove(position);
                    docAdapter.notifyItemRemoved(position);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_PICK_DOC_FILE && resultCode == RESULT_OK) {
            if ((data != null) && (data.getData() != null)) {
                docArray.add(0, data.getData());
                //docStringArray.add(data.getDataString());
                docStringArray.add(0, new AttachmentModel(getFileName(data.getData()), getFileName(data.getData()), true));
                docAdapter.notifyDataSetChanged();
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

    private void uploadFile(int position, String name, String details) {
        Log.d("doc", position + "/" + docArray.size());
        if (position == docArray.size()) {
            progressBarUpload.setVisibility(View.GONE);
            //txtProgress.setVisibility(View.GONE);

            if (onlineDocArray.isEmpty()){
                new AlertDialog.Builder(AddNotesActivity.this, R.style.CustomDialogTheme)
                        .setIcon(R.drawable.ic_error_outline)
                        .setTitle("Error")
                        .setMessage("Add atleast 1 document or link to learning resource!")
                        .setPositiveButton("Ok", null)
                        .show();
                return;
            }

            NotesModel currItem = new NotesModel(name, details, subjectModel.getId(), new Date(),onlineDocArray, currentUser.getId());

            FirebaseFirestore ff = FirebaseFirestore.getInstance();
            ff.collection(CollectionName.NOTES).add(currItem)
                .addOnSuccessListener(documentReference -> finish())
                .addOnFailureListener(e -> new AlertDialog.Builder(AddNotesActivity.this, R.style.CustomDialogTheme)
                    .setIcon(R.drawable.ic_error_outline)
                    .setTitle("Error")
                    .setMessage(e.getMessage())
                    .setPositiveButton("Ok", null)
                    .show());
        } else {
            final StorageReference fileRef = storageReference.child(docStringArray.get(position).getUrl() + new Date() + "." + fileExtension(docArray.get(position)));
            uploadTask = fileRef.putFile(docArray.get(position))
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        onlineDocArray.add(new AttachmentModel(getFileName(docArray.get(position)), uri.toString(), true));
                        uploadFile(position + 1, name, details);
                    }))
                    .addOnFailureListener(e -> {
                        new AlertDialog.Builder(AddNotesActivity.this, R.style.CustomDialogTheme)
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
        }
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
