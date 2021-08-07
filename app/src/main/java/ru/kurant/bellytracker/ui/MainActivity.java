package ru.kurant.bellytracker.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;

import ru.kurant.bellytracker.R;
import ru.kurant.bellytracker.controller.share.StringPatterns;
import ru.kurant.bellytracker.model.room.SingleDBService;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int EXPORT_CODE = 32;
    private static final int IMPORT_CODE = 64;

    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navController = Navigation.findNavController(this, R.id.fragment_container);
        appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph())
                        .setOpenableLayout(drawerLayout)
                        .build();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        StringPatterns.datePattern = sharedPrefs.getString(getString(R.string.date_pattern), getString(R.string.date_pattern_default));
        StringPatterns.timePattern = sharedPrefs.getString(getString(R.string.time_pattern), getString(R.string.time_pattern_default));

        StringPatterns.alcoholLevelTrigger = 0;
    }

    @Override
    public boolean onSupportNavigateUp() {

        if (navController.getCurrentDestination().getId() == R.id.nav_pref) {
            finish();
            startActivity(getIntent());
            return true;
        } else {
            return NavigationUI.navigateUp(navController, appBarConfiguration)
                    || super.onSupportNavigateUp();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        boolean success = false;

        if (id == R.id.menu_export_db) {
            exportDialog();
            success = true;
        } else if (id == R.id.menu_import_db) {
            Intent importIntent = new Intent(Intent.ACTION_GET_CONTENT);
            importIntent.setType(SingleDBService.DB_FILE_TYPE)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    .putExtra("android.content.extra.SHOW_ADVANCED", true);
            startActivityForResult(importIntent, IMPORT_CODE);
        } else if (id == R.id.menu_statistics) {
            navController.navigate(R.id.statisticsFragment);
        } else if (id == R.id.menu_about) {
            navController.navigate(R.id.aboutDialogFragment);
        } else if (id == R.id.menu_prefs) {
            navController.navigate(R.id.nav_pref);
        }
        drawerLayout.closeDrawers();
        return success;
    }

    private void exportDialog() {
        AlertDialog.Builder exportDialogBuilder = new AlertDialog.Builder(this);
        exportDialogBuilder.setMessage(R.string.export_db_msg).setPositiveButton(R.string.yes_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.setType(SingleDBService.DB_FILE_TYPE);
                intent.putExtra(Intent.EXTRA_TITLE, "BellyTrackerDB.db");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(intent, EXPORT_CODE);
            }
        }).setNegativeButton(R.string.no_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), R.string.export_cancelled_msg, Toast.LENGTH_LONG).show();
            }
        }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
            .setCancelable(true);
        exportDialogBuilder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        Context context = this;
        if (requestCode == EXPORT_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri exportDBuri = data.getData();
                if (SingleDBService.exportDB(context, exportDBuri)) {
                    Toast.makeText(context, "Export DB: success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Export DB: fail", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } else if (requestCode == IMPORT_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri importDBuri = data.getData();
                importAndRefresh(context, importDBuri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void importAndRefresh(Context context, Uri importDBuri) {
        AlertDialog.Builder importAlertDialogBuilder = new AlertDialog.Builder(context)
                .setMessage("You mean to replace your Database with saved file. Do you want to backup existing db?")
                .setPositiveButton(R.string.yes_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exportDialog();
                    }
                })
                .setNegativeButton(R.string.no_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (SingleDBService.importDB(context, importDBuri)) {
                            Toast.makeText(context, "Import DB: success", Toast.LENGTH_SHORT).show();
                            navController.popBackStack();
                            navController.navigate(R.id.drinkListFragment);
                        } else {
                            Toast.makeText(context, "Import DB: fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true);
        importAlertDialogBuilder.show();
    }
}