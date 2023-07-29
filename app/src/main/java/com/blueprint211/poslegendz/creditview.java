package com.blueprint211.poslegendz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class creditview extends AppCompatActivity {
    RecyclerView recview;
    ArrayList<Titlemodel> titleArrayList = new ArrayList<>();
    ArrayList<Credit> creditList = new ArrayList<>();
    FirebaseFirestore db;
    TitleAdapter adapter;
    CollectionReference child;
    TextView emptylist;
    ProgressBar spinner;
    EditText searchBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditview);

        recview = findViewById(R.id.precview);
        emptylist = findViewById(R.id.empty);
        recview.setNestedScrollingEnabled(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new TitleAdapter( this, titleArrayList);
        recview.setLayoutManager(layoutManager);
        recview.setAdapter(adapter);
        spinner=(ProgressBar)findViewById(R.id.progressBar);

        spinner.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                spinner.setVisibility(View.INVISIBLE);

            }
        }, 2000);

        this.searchBox =(EditText) findViewById(R.id.searchName);
        this. searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                creditview.this.filterQuery(s.toString());
            }
        });

        db = FirebaseFirestore.getInstance();
        child = db.collection("Credits");
        child.orderBy("timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                QuerySnapshot tasks = task.getResult();
                emptylist.setVisibility(View.INVISIBLE);
                for (DocumentSnapshot dsp : tasks.getDocuments()) {

                    Titlemodel mch = dsp.toObject(Titlemodel.class);
                    titleArrayList.add(mch);
                    adapter.notifyDataSetChanged();
                }
            }else{
                emptylist.setVisibility(View.VISIBLE);
            }


        });
        child.get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                QuerySnapshot tasks = task.getResult();


                for (DocumentSnapshot ssp : tasks.getDocuments()) {
                    List<Credit> creditList = (List<Credit>) ssp.get("titleitems");


                    assert creditList != null;
                    creditList.add(new Credit());

                }
                adapter.notifyDataSetChanged();
            }
        });





    }

    private void filterQuery(String text) {
        ArrayList<Titlemodel> filterdNames = new ArrayList<>();
        for (Titlemodel s : this.titleArrayList){
            if (s.getName().toUpperCase().toLowerCase().contains(text) ) {
                filterdNames.add(s);
            }
        }
        this.adapter.setFilter(filterdNames);
    }

    public void onBackPressed() {

        finish();
        Intent intent = new Intent(creditview.this, dashboardActivity.class);
        startActivity(intent);
    }
    public void onLoginClick (View view){
        startActivity(new Intent(this, dashboardActivity.class));


    }
}
