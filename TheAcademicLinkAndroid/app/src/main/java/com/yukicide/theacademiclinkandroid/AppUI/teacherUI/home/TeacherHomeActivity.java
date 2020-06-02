package com.yukicide.theacademiclinkandroid.AppUI.teacherUI.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.AppUI.teacherUI.assignedPosts.TeacherClassManagementActivity;
import com.yukicide.theacademiclinkandroid.AppUI.globalUI.user_management.LoginActivity;
import com.yukicide.theacademiclinkandroid.AppUI.globalUI.user_management.ViewUserActivity;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.TeacherModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.UserModel;

public class TeacherHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    public UserModel currentUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        currentUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), UserModel.class);
        if (currentUser == null) {
            Toast.makeText(this, "User Information not loaded!\nTry to Login again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
        }

        if (currentUser.getUserType().equals(UserType.TEACHER))
            currentUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), TeacherModel.class);
        else
            finish();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view_teacher);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new TeacherDashboardFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_teachers_dashboard);
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
                        new TeacherDashboardFragment()).commit();
                break;

            case R.id.nav_teachers_class:
                startActivity(new Intent(TeacherHomeActivity.this, TeacherClassManagementActivity.class)
                    .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser)));
                break;

            case R.id.nav_profile:
                startActivity(new Intent(this, ViewUserActivity.class)
                        .putExtra(StringExtras.PROFILE_USER, (new Gson()).toJson(currentUser))
                        .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser)));
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
            new AlertDialog.Builder(TeacherHomeActivity.this, R.style.CustomDialogTheme)
                    .setIcon(R.drawable.ic_error_outline)
                    .setTitle("Exit")
                    .setMessage("Would you like to close the app?")
                    .setPositiveButton("Yes", (dialog, which) -> finish())
                    .setNegativeButton("No", null)
                    .show();

        }
    }
}