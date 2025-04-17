package com.satnamsinghmaggo.nitnemgurbani;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class MainActivity extends BaseActivity {
    ImageView imageview;
    ImageView imageview1;
    ImageButton themeToggleBtn;


    @SuppressLint("ScheduleExactAlarm")
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageview = findViewById(R.id.HukamNamaIMG);
        imageview1 = findViewById(R.id.PaathIMG);
      //  GlobalLoader.preloadAnimation(this, "Animation1.json");
        themeToggleBtn = findViewById(R.id.themeToggleBtn);

        SharedPreferences prefs = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        int themeMode = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(themeMode);

        themeToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTheme(); // same method you're already using
                updateThemeIcon();
            }
        });

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            themeToggleBtn.setImageResource(R.drawable.light_mode); // icon for light mode
        } else {
            themeToggleBtn.setImageResource(R.drawable.dark_mode); // icon for dark mode
        }

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Acitvity_HukamNama.class);
                startActivity(intent);
            }
        });

        imageview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Paath_acitvity.class);
                startActivity(intent);
            }
        });
        requestNotificationPermission();
        createNotificationChannel();
       // scheduleDailyNotification(this,11,18,103,"test","test");
        scheduleDailyNotification(this, 7, 0, 100, "Nitnem Sahib", "Start your day with Nitnem 🙏🏻");
        scheduleDailyNotification(this, 17, 0, 101, "Rehras Sahib", "Its Rehras Sahib Time 🙏🏻");
        scheduleDailyNotification(this, 21, 30, 102, "Kirtan Sohila", "Night prayer reminder 🙏🏻");


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "myChannelId", "Daily Reminder", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    @SuppressLint("ScheduleExactAlarm")
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    private void scheduleDailyNotification(Context context, int hour, int minute, int requestCode, String title, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("daily_channel", "Daily Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
    private void toggleTheme() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        SharedPreferences prefs = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor.putInt("theme_mode", AppCompatDelegate.MODE_NIGHT_NO);
            Toast.makeText(this, "Light Mode On ☀", Toast.LENGTH_SHORT).show();
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            editor.putInt("theme_mode", AppCompatDelegate.MODE_NIGHT_YES);
            Toast.makeText(this, "Dark Mode On 🌙", Toast.LENGTH_SHORT).show();
        }

        editor.apply();
    }


    private void updateThemeIcon() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            themeToggleBtn.setImageResource(R.drawable.ic_menu_day);
        } else {
            themeToggleBtn.setImageResource(R.drawable.ic_menu_night);
        }

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(androidx.appcompat.R.attr.colorControlNormal, typedValue, true);

        int color;
        if (typedValue.resourceId != 0) {
            color = ContextCompat.getColor(this, typedValue.resourceId);
        } else {
            color = typedValue.data;
        }

        themeToggleBtn.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }
}
