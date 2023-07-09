package com.monstertechno.timely;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity {

    private List<Task> taskList;
    private ArrayAdapter<Task> taskAdapter;
    private ListView listViewTasks;
    private EditText editTextTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskList = new ArrayList<>();
        taskAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, taskList);

        listViewTasks = findViewById(R.id.listViewTasks);
        listViewTasks.setAdapter(taskAdapter);
        listViewTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = taskList.get(position);
                task.setCompleted(!task.isCompleted());
                taskAdapter.notifyDataSetChanged();
            }
        });

        editTextTask = findViewById(R.id.editTextTask);
    }

    public void addTask(View view) {
        String taskDescription = editTextTask.getText().toString().trim();
        if (!TextUtils.isEmpty(taskDescription)) {
            Task task = new Task(taskDescription);
            taskList.add(task);
            taskAdapter.notifyDataSetChanged();
            editTextTask.getText().clear();
        }
    }

    public void editTask(View view) {
        int position = listViewTasks.getCheckedItemPosition();
        if (position != ListView.INVALID_POSITION) {
            String newDescription = editTextTask.getText().toString().trim();
            if (!TextUtils.isEmpty(newDescription)) {
                Task task = taskList.get(position);
                task.setDescription(newDescription);
                taskAdapter.notifyDataSetChanged();
                listViewTasks.clearChoices();
                editTextTask.getText().clear();
            }
        }
    }

    public void deleteTask(View view) {
        int position = listViewTasks.getCheckedItemPosition();
        if (position != ListView.INVALID_POSITION) {
            taskList.remove(position);
            taskAdapter.notifyDataSetChanged();
            listViewTasks.clearChoices();
        }
    }
}
