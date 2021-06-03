package com.example.comarcalendarioapp.gui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comarcalendarioapp.R;
import com.example.comarcalendarioapp.gui.adapter.TaskAdapter;
import com.example.comarcalendarioapp.model.TaskFirestore;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private FirebaseFirestore bd;
    private FloatingActionButton fabInsert;
    private FirebaseAuth mAuth;
    private Query query;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        setContentView(R.layout.activity_list);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        searchView= findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchText) {
                taskAdapter.stopListening();
                if (!TextUtils.isEmpty(searchText)) {
                    String capitalizedSearchText = capitalizeText(searchText);
                    query = bd.collection("elementos")
                            .whereGreaterThanOrEqualTo("description", capitalizedSearchText)
                            .whereLessThanOrEqualTo("description", capitalizedSearchText+"\uF7FF");
                    FirestoreRecyclerOptions<TaskFirestore> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<TaskFirestore>().setQuery(query, TaskFirestore.class).build();
                    taskAdapter= new TaskAdapter(firestoreRecyclerOptions);
                    recyclerView.setAdapter(taskAdapter);
                    taskAdapter.startListening();
                } else {
                    query = bd.collection("elementos")
                            .orderBy("date", Query.Direction.ASCENDING);
                    FirestoreRecyclerOptions<TaskFirestore> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<TaskFirestore>().setQuery(query, TaskFirestore.class).build();
                    taskAdapter= new TaskAdapter(firestoreRecyclerOptions);
                    recyclerView.setAdapter(taskAdapter);
                    taskAdapter.startListening();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchText) {
                taskAdapter.stopListening();
                if (!TextUtils.isEmpty(searchText)) {
                    String capitalizedSearchText = capitalizeText(searchText);
                    query = bd.collection("elementos")
                            .whereGreaterThanOrEqualTo("description", capitalizedSearchText)
                            .whereLessThanOrEqualTo("description", capitalizedSearchText+"\uF7FF");
                    FirestoreRecyclerOptions<TaskFirestore> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<TaskFirestore>().setQuery(query, TaskFirestore.class).build();
                    taskAdapter= new TaskAdapter(firestoreRecyclerOptions);
                    recyclerView.setAdapter(taskAdapter);
                    taskAdapter.startListening();
                } else {
                    query = bd.collection("elementos")
                            .orderBy("date", Query.Direction.ASCENDING);
                    FirestoreRecyclerOptions<TaskFirestore> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<TaskFirestore>().setQuery(query, TaskFirestore.class).build();
                    taskAdapter= new TaskAdapter(firestoreRecyclerOptions);
                    recyclerView.setAdapter(taskAdapter);
                    taskAdapter.startListening();
                }
                return false;
            }
        });

        bd = FirebaseFirestore.getInstance();

        query = bd.collection("elementos").orderBy("date", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<TaskFirestore> firestoreRecyclerOptions
                = new FirestoreRecyclerOptions.Builder<TaskFirestore>().setQuery(query, TaskFirestore.class).build();
        taskAdapter= new TaskAdapter(firestoreRecyclerOptions);
        recyclerView.setAdapter(taskAdapter);

        fabInsert= findViewById(R.id.fabInsert);
        fabInsert.setOnClickListener(v -> {
            Bundle bundle = this.getIntent().getExtras();
            String user = bundle.getString("user");
            Intent intento = new Intent(ListActivity.this, TaskActivity.class);

            Bundle bU = new Bundle();
            bU.putString("user", user);

            intento.putExtras(bU);
            startActivity(intento);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        taskAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        taskAdapter.stopListening();
    }

    //método para convertir en mayúscula la primera palabra introducida
    private String capitalizeText(String searchText){
        String capitalizedText;
        return capitalizedText = searchText.substring(0,1).toUpperCase() + searchText.substring(1);
    }
}

