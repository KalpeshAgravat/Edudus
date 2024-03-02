package com.alex.edudus.ui.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.afpensdk.pen.DPenCtrl;
import com.afpensdk.pen.penmsg.IAFPenOfflineDataListener;
import com.afpensdk.pen.penmsg.PenGripStyle;
import com.afpensdk.structure.AFDot;
import com.alex.edudus.BaseActivity;

import com.alex.edudus.R;
import com.alex.edudus.utility.Const;
import com.alex.edudus.utility.LogUtils;
import com.alex.edudus.utility.PenClientCtrl;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.alex.edudus.databinding.ActivityMainBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MainActivity extends BaseActivity implements MenuItem.OnMenuItemClickListener {

    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_board, R.id.nav_paper,R.id.nav_about)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        //TODO getMenuInflater().inflate(R.menu.topmenu, menu);
        getMenuInflater().inflate(R.menu.topmenu_basic, menu);
       /* if(mNormalToolbar.getMenu().findItem(R.id.action_item2)!=null) {
            mNormalToolbar.getMenu().findItem(R.id.action_item2).setEnabled(false);
        }*/

        return super.onCreateOptionsMenu(menu);
    }

   public void openDeviceAct(MenuItem item){
        Intent intent = new Intent(MainActivity.this, DeviceListActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onMenuItemClick(@NonNull MenuItem menuItem) {

       /* if (menuItem.getItemId() == R.id.action_device){
            Intent intent = new Intent(MainActivity.this, DeviceListActivity.class);
            startActivity(intent);
        }*/
        return super.onOptionsItemSelected(menuItem);
    }
}