package com.yukicide.theacademiclinkandroid.AppUI.adminUI.home;

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
import com.google.gson.Gson;
import com.yukicide.theacademiclinkandroid.AppUI.globalUI.user_management.ViewUserActivity;
import com.yukicide.theacademiclinkandroid.AppUI.adminUI.subjectsCRUD.ManageSubjectsActivity;
import com.yukicide.theacademiclinkandroid.AppUI.adminUI.teacherCRUD.ManageTeachersActivity;
import com.yukicide.theacademiclinkandroid.R;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.StringExtras;
import com.yukicide.theacademiclinkandroid.Repositories.Fixed.UserType;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.AdminModel;
import com.yukicide.theacademiclinkandroid.Repositories.Models.Users.UserModel;
import com.yukicide.theacademiclinkandroid.AppUI.adminUI.classCRUD.ManageClassActivity;
import com.yukicide.theacademiclinkandroid.AppUI.globalUI.information.SchoolCalendarFragment;
import com.yukicide.theacademiclinkandroid.AppUI.studentUI.home.fragments.ChatsFragment;
import com.yukicide.theacademiclinkandroid.AppUI.globalUI.user_management.LoginActivity;

public class AdminHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    public UserModel currentUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        currentUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), UserModel.class);
        if (currentUser == null) {
            Toast.makeText(this, "User Information not loaded!\nTry to Login again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
        }

        if (currentUser.getUserType().equals(UserType.ADMIN))
            currentUser = (new Gson()).fromJson(getIntent().getStringExtra(StringExtras.CURRENT_USER), AdminModel.class);
        else {
            Toast.makeText(this, "Error encountered.\nTry deleting your cache in the settings!", Toast.LENGTH_SHORT).show();
            finish();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view_staff);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new AdminDashboardFragment()).commit();
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
            // ADMIN CONTROLS
            case R.id.nav_admin_class:
                startActivity(new Intent(AdminHomeActivity.this, ManageClassActivity.class)
                        .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser)));
                break;
            case R.id.nav_admin_staff:
                startActivity(new Intent(AdminHomeActivity.this, ManageTeachersActivity.class)
                        .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser)));
                break;
            case R.id.nav_admin_subject:
                startActivity(new Intent(AdminHomeActivity.this, ManageSubjectsActivity.class)
                        .putExtra(StringExtras.CURRENT_USER, (new Gson()).toJson(currentUser)));
                break;
            case R.id.nav_admin_info:
                Toast.makeText(this, "Admin Info", Toast.LENGTH_SHORT).show();
                break;

            // FRAGMENT CONTROLS
            case R.id.nav_dashboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AdminDashboardFragment()).commit();
                break;
            /*case R.id.nav_calendar:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SchoolCalendarFragment()).commit();
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
            new AlertDialog.Builder(AdminHomeActivity.this, R.style.CustomDialogTheme)
                    .setIcon(R.drawable.ic_error_outline)
                    .setTitle("Exit")
                    .setMessage("Would you like to close the app?")
                    .setPositiveButton("Yes", (dialog, which) -> finish())
                    .setNegativeButton("No", null)
                    .show();

        }
    }
}