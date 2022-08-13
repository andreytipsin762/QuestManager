package com.projects.questmanager;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TaskManagementActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextView questName;
    private RecyclerView recyclerView;
    private FrameLayout frameLayoutManagement;
    private EditText taskName, taskDesc, taskCorrectAnswer, taskLoc, questID;
    private Button confirmAdding, cancelAdding, newTask;

    private TaskManagementAdaptor adaptor;

    private List<TaskInfo> taskList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_management);

        questName = findViewById(R.id.questName);
        recyclerView = findViewById(R.id.recyclerView);
        frameLayoutManagement = findViewById(R.id.frameLayoutManagement);
        taskName = findViewById(R.id.taskName);
        taskDesc = findViewById(R.id.taskDesc);
        taskCorrectAnswer = findViewById(R.id.taskCorrAnswer);
        confirmAdding = findViewById(R.id.confirmAdding);
        cancelAdding = findViewById(R.id.cancelAdding);
        newTask = findViewById(R.id.newTaskButton);
        taskLoc = findViewById(R.id.taskLoc);

        //todo: change edittext to textview
        questID = findViewById(R.id.questID);

        String taskName, tascLoc, taskDescription, correctAnswer;

        Toast.makeText(this, "l:" + taskList.size(), Toast.LENGTH_SHORT).show();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adaptor = new TaskManagementAdaptor(TaskManagementActivity.this, taskList);
        recyclerView.setAdapter(adaptor);

        newTask.setOnClickListener(v -> addNewTask());
        confirmAdding.setOnClickListener(v -> confirmNewTask());
        cancelAdding.setOnClickListener(v -> cancelNewTask());

    }

    private void cancelNewTask() {
        frameLayoutManagement.setVisibility(View.INVISIBLE);
    }

    private void confirmNewTask() {
        String s1 = taskName.getText().toString();
        String s3 = taskDesc.getText().toString();
        String s4 = taskCorrectAnswer.getText().toString();
        String s2 = taskLoc.getText().toString();
        String s5 = questID.getText().toString();
        TaskInfo task = new TaskInfo(s1, s2, s3, s4, s5);

        db.collection("Tasks")
                .add(task)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        frameLayoutManagement.setVisibility(View.INVISIBLE);

                        updateList();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });


    }

    private void updateList() {
        db.collection("Tasks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            taskList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //  getResult
                                String s1 =  document.getData().get("taskName").toString();
                                String s3 = document.getData().get("taskDescription").toString();
                                String s4 = document.getData().get("correctAnswer").toString();
                                String s2 = document.getData().get("taskLoc").toString();
                                String s5 = document.getData().get("questID").toString();
                                TaskInfo task1 = new TaskInfo(s1, s2, s3, s4, s5);

                                Log.d(TAG, document.getId() + " => " + document.getData());

                                taskList.add(task1);

                            }

                            adaptor = new TaskManagementAdaptor(TaskManagementActivity.this, taskList);

//        Log.println(Log.DEBUG,"mytag",partyNameList.size()+"");

                            recyclerView.setAdapter(adaptor);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    private void addNewTask() {
        frameLayoutManagement.setVisibility(View.VISIBLE);
    }
}
