package com.monstertechno.timely;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class TaskActivity extends AppCompatActivity {

    private EditText taskEditText;
    private Button addButton;
    private LinearLayout tasksContainer;
    private AlarmManager alarmManager;
    private NotificationManager notificationManager;

    private static final String CHANNEL_ID = "TASK_REMINDER_CHANNEL";
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_activity);

        taskEditText = findViewById(R.id.taskEditText);
        addButton = findViewById(R.id.addButton);
        tasksContainer = findViewById(R.id.tasksContainer);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = taskEditText.getText().toString();
                if (!task.isEmpty()) {
                    addTask(task);
                    taskEditText.setText("");
                }
            }
        });
    }

    private void addTask(final String task) {
        final View taskView = getLayoutInflater().inflate(R.layout.task_item, null);
        final TextView taskTextView = taskView.findViewById(R.id.taskTextView);
        final CheckBox taskCheckBox = taskView.findViewById(R.id.taskCheckBox);
        final Button deleteButton = taskView.findViewById(R.id.deleteButton);
        final Button setTimeButton = taskView.findViewById(R.id.setTimeButton);

        taskTextView.setText(task);

        taskCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    taskTextView.setPaintFlags(taskTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    taskView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    taskCheckBox.setEnabled(false);
                    deleteButton.setVisibility(View.GONE);
                    setTimeButton.setVisibility(View.GONE);
                    displayToast("Task completed successfully.");

                    // Remove task after 3 seconds
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (taskCheckBox.isChecked()) {
                                tasksContainer.removeView(taskView);
                            }
                        }
                    }, 3000);
                } else {
                    deleteButton.setVisibility(View.VISIBLE);
                    setTimeButton.setVisibility(View.VISIBLE);
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tasksContainer.removeView(taskView);
            }
        });

        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(task);
            }
        });

        tasksContainer.addView(taskView);
    }



    private void showTimePickerDialog(final String task) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(TaskActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        scheduleReminder(task, hourOfDay, minute);
                    }
                }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    private void scheduleReminder(String task, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(TaskActivity.this, ReminderBroadcastReceiver.class);
        intent.putExtra("task", task);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(TaskActivity.this, 0, intent, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        displayToast("Reminder set for " + hourOfDay + ":" + String.format("%02d", minute));
    }

    private void cancelReminder(String task) {
        Intent intent = new Intent(TaskActivity.this, ReminderBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(TaskActivity.this, 0, intent, 0);
        alarmManager.cancel(pendingIntent);
        displayToast("Reminder canceled for task: " + task);
    }

    private void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
