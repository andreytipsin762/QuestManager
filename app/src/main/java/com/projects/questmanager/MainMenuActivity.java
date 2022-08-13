package com.projects.questmanager;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainMenuActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton menuButton;
    private RecyclerView recyclerView;

    private List<QuestInfo> partyNameList = new ArrayList<>();
    private MyAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
//    private String questLocation;

    //todo:create class
//    String questName, adminName, adminPass, userPass, urlImage, isConfirmedByHQ, questDescription,usersLimit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_menu);
        setTitle("");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> OpenMenu());

        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        db.collection("Quests")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            partyNameList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //  getResult
                                String questName, adminName, adminPass, userPass, urlImage, isConfirmedByHQ, questDescription, usersLimit;

                                Log.d(TAG, document.getId() + " => " + document.getData());
                                questName = document.getData().get("questName").toString();
                                adminName = document.getData().get("adminName").toString();
                                adminPass = document.getData().get("adminPass").toString();
                                userPass = document.getData().get("userPass").toString();
                                usersLimit = document.getData().get("usersLimit").toString();
                                String questID = document.getId().toString();


                                try {
                                    urlImage = document.getData().get("urlImage").toString();


                                } catch (Exception e) {
                                    urlImage = "";

                                }
                                isConfirmedByHQ = document.getData().get("isConfirmedByHQ").toString();
                                questDescription = document.getData().get("questDescription").toString();
                                String questLocation = document.getData().get("questLocation").toString();

                                QuestInfo questInfo = new QuestInfo(questName, adminName, adminPass, userPass, urlImage, isConfirmedByHQ, questDescription, usersLimit, questLocation, questID);

                              MyUtils.updateQuestInfo(questInfo,questID);
                                partyNameList.add(questInfo);

                            }

//                            partyNameList.add("j o p a a a");
//        partyNameList.add("biba");
//        partyNameList.add("Dina");

                            adapter = new MyAdapter(MainMenuActivity.this, partyNameList);

//        Log.println(Log.DEBUG,"mytag",partyNameList.size()+"");

                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
//        adapter = new MyAdapter(MainMenuActivity.this, partyNameList);

//        Log.println(Log.DEBUG,"mytag",partyNameList.size()+"");

        recyclerView.setAdapter(adapter);

//        adapter = new MyAdapter(MainMenuActivity.this, partyNameList);

//        Log.println(Log.DEBUG,"mytag",partyNameList.size()+"");

//        recyclerView.setAdapter(adapter);


    }

    private void OpenMenu() {
        PopupMenu popupMenu = new PopupMenu(MainMenuActivity.this, menuButton);
        popupMenu.getMenuInflater().inflate(R.menu.popupmenu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getTitle().toString()) {
                    case "My quests":
                        Intent intentMyQuests = new Intent(MainMenuActivity.this, MyQuestActivity.class);
                        startActivity(intentMyQuests);
                        break;

                    case "Create quest":
                        Intent intentCreate = new Intent(MainMenuActivity.this, CreateQuestActivity.class);
                        startActivity(intentCreate);
                        break;

                    default:
                        break;
                }
//                if (menuItem.getTitle().equals("Three")) {
//                    Toast.makeText(MainMenuActivity.this, "J O P A", Toast.LENGTH_SHORT).show();
//                }
                return true;
            }
        });

        popupMenu.show();
    }
}