package com.yukicide.theacademiclinkandroid.AppUI.studentUI.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.AppUI.globalUI.user_management.ViewUserActivity;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.CollectionName;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.ParentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.StudentModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.UserModel;
import com.yukicide.theacademiclinkandroid.AppUI.globalUI.information.SchoolCalendarFragment;
import com.yukicide.theacademiclinkandroid.AppUI.studentUI.home.fragments.AttendanceFragment;
import com.yukicide.theacademiclinkandroid.AppUI.studentUI.home.fragments.ChatsFragment;
import com.yukicide.theacademiclinkandroid.AppUI.studentUI.home.fragments.DiscussionsFragment;
import com.yukicide.theacademiclinkandroid.AppUI.studentUI.home.fragments.HomeworkFragment;
import com.yukicide.theacademiclinkandroid.AppUI.studentUI.home.fragments.DashboardFragment;
import com.yukicide.theacademiclinkandroid.AppUI.studentUI.home.fragments.TimetableFragment;
import com.yukicide.theacademiclinkandroid.AppUI.globalUI.user_management.LoginActivity;

import static com.yukicide.theacademiclinkandroid.R.layout.activity_student_home;

public class StudentHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    public UserModel currentUser = null;
    public StudentModel currentStudent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_student_home);

        currentUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), UserModel.class);
        if (currentUser == null) {
            Toast.makeText(this, "User Information not loaded!\nTry to Login again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
        }

        if (currentUser.getUserType().equals(UserType.STUDENT)) {
            currentStudent = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), StudentModel.class);
        } else {
            currentUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), ParentModel.class);

            FirebaseFirestore.getInstance().collection(CollectionName.USERS)
                    .document(((ParentModel) currentUser).getChildId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> currentStudent = documentSnapshot.toObject(StudentModel.class))
                    .addOnFailureListener(e -> new AlertDialog.Builder(StudentHomeActivity.this, R.style.CustomDialogTheme)
                            .setIcon(R.drawable.ic_error_outline)
                            .setTitle("Error")
                            .setMessage(e.getMessage() + "\n\nStudent information may be unavailable until you restart the app!")
                            .setPositiveButton("Ok", null)
                            .show());
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view_student);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new DashboardFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }

        View headerView = navigationView.getHeaderView(0);
        TextView uname = headerView.findViewById(R.id.nav_uname);
        TextView grade = headerView.findViewById(R.id.nav_grade);

        uname.setText(String.format("%s %s", currentUser.getFirstName(), currentUser.getSurname()));
        grade.setText(currentUser.getUserType().name().toLowerCase());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_dashboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new DashboardFragment()).commit();
                break;
            /*case R.id.nav_homework:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeworkFragment()).commit();
                break;*/
            case R.id.nav_timetable:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new TimetableFragment()).commit();
                break;
            /*case R.id.nav_calendar:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SchoolCalendarFragment()).commit();
                break;*/
            /*case R.id.nav_discussions:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new DiscussionsFragment()).commit();
                break;*/
            /*case R.id.nav_attendance:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AttendanceFragment()).commit();
                break;*/
            /*case R.id.nav_chats:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ChatsFragment()).commit();
                break;*/

            case R.id.nav_profile:
                startActivity(new Intent(this, ViewUserActivity.class)
                        .putExtra(StringExtras.PROFILE_USER, (new Gson()).toJson(currentUser))
                        .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser)));
                break;
            /*case R.id.nav_gallery:
                Toast.makeText(this, "Gallery", Toast.LENGTH_SHORT).show();
                break;*/
            case R.id.nav_info:
                Toast.makeText(this, "Info", Toast.LENGTH_SHORT).show();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(StudentHomeActivity.this, R.style.CustomDialogTheme)
                    .setIcon(R.drawable.ic_error_outline)
                    .setTitle("Exit")
                    .setMessage("Would you like to close the app?")
                    .setPositiveButton("Yes", (dialog, which) -> finish())
                    .setNegativeButton("No", null)
                    .show();

        }
    }
}