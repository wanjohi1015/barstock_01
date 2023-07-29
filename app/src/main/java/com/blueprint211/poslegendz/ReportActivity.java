package com.blueprint211.poslegendz;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ReportActivity extends AppCompatActivity {
    Calendar calendar;
    Locale id = new Locale("in", "ID");
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", id);
    @ServerTimestamp
    Date date_minimal,date_maximal;
    EditText input_minimal, input_maximal;
    String input1;
    String input2,white,black,whiref,blaref;
    Button btn_minimal,btn_maximal;
    TextView totalsales,itemsale,saleprofit,wkegsaleview;
    ImageView cancel;
    Button report;
    CollectionReference child, bkegs,wkegs;
    List<Integer> sale = new ArrayList<>();
    List<Integer> blaksale = new ArrayList<>();
    List<Integer> whisale = new ArrayList<>();
    List<Integer> totprofit = new ArrayList<>();
    List<Float> nuitems = new ArrayList<>();
    List<reports> repoList= new ArrayList<>();
    int addsales=0,ordersales,profit,addprofit=0,whitesales,blacksales,addblaksale=0,addwhitsales=0,kegsales;
    float quantity,items=0;
    boolean isConnected = false;
    ConnectivityManager connectivityManager;
    android.icu.text.SimpleDateFormat dateFormat = new android.icu.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String time  = dateFormat.format(new Date());


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportview);
        calendar = Calendar.getInstance();
        white = "White keg";
        black = "Black keg";
        cancel = (ImageView) findViewById(R.id.btn_cancel);
        saleprofit = (TextView) findViewById(R.id.profit);
        wkegsaleview = (TextView) findViewById(R.id.wkeg);
        wkegsaleview.setTypeface(null, Typeface.BOLD);
        saleprofit.setTypeface(null, Typeface.BOLD);
        totalsales = (TextView) findViewById(R.id.sales);
        totalsales.setTypeface(null, Typeface.BOLD);
        itemsale = (TextView) findViewById(R.id.items);
        itemsale.setTypeface(null, Typeface.BOLD);
        report = (Button) findViewById(R.id.showreport);
        input_minimal = findViewById(R.id.input_minimal);
        input_maximal = findViewById(R.id.input_maximal);
        btn_minimal = (Button) findViewById(R.id.btn_minimal);
        btn_maximal = (Button) findViewById(R.id.btn_maximal);




        btn_minimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(btn_minimal.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);


                        date_minimal = calendar.getTime();

                        input1 = input_minimal.getText().toString();
                        input2 = input_maximal.getText().toString();
                        report.setEnabled(!input1.isEmpty() || !input2.isEmpty());
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                TimePickerDialog timePickerDialog = new TimePickerDialog(btn_minimal.getContext(),
                        new TimePickerDialog.OnTimeSetListener() {


                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                input_minimal.setText(String.format("%s%s", simpleDateFormat.format(calendar.getTime()), hourOfDay + ":" + minute));
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY) , calendar.get(Calendar.MINUTE),false );
                timePickerDialog.show();
            }
        });
        btn_maximal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(btn_maximal.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);
                        input_maximal.setText(simpleDateFormat.format(calendar.getTime()));
                        date_maximal = calendar.getTime();


                        input1 = input_maximal.getText().toString();
                        input2 = input_minimal.getText().toString();
                        if (input1.isEmpty() && input2.isEmpty()) {
                            report.setEnabled(false);
                        } else {
                            report.setEnabled(true);
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isConnected) {
                    reportdata();
                    whitekegreport();
                }else{
                    startActivity(new Intent(getApplicationContext(), noInternetActivity.class));
                }
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_maximal.setEnabled(true);
                input_minimal.setEnabled(true);
                input_maximal.getText().clear();
                input_minimal.getText().clear();
                recreate();


            }

        });
    }

    private void  whitekegreport() {
        String beer = "Keg Beer";
        wkegs = FirebaseFirestore.getInstance().collection("Stocksold");
        wkegs.orderBy("timestamp").whereEqualTo("define", beer).startAt(date_minimal).endAt(date_maximal).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {


                assert value != null;
                for (DocumentSnapshot lcn : value.getDocuments()) {
                    reports mch = lcn.toObject(reports.class);
                    assert mch != null;
                    whitesales = mch.getSales();
                    whisale.add(whitesales);
                }
                for (int i = 0; i < whisale.size(); i++) {
                    addwhitsales += Integer.parseInt(String.valueOf(whisale.get(i)));
                    wkegsaleview.setText("KSH " + String.valueOf(addwhitsales));
                }
                addwhitsales=0;
                repoList.clear();
                whisale.clear();
            }
        });
    }

    private void  reportdata() {

        child = FirebaseFirestore.getInstance().collection("Stocksold");
        child.orderBy("timestamp").startAt(date_minimal).endAt(date_maximal).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {



                assert value != null;
                for (DocumentSnapshot lcn : value.getDocuments()) {
                    reports mch = lcn.toObject(reports.class);
                    assert mch != null;
                    ordersales = mch.getSales();
                    quantity = mch.getItems();
                    profit = mch.getProfit();
                    totprofit.add(profit);
                    sale.add(ordersales);
                    nuitems.add(quantity);
                    repoList.add(mch);

                }


                for (int i = 0; i < sale.size(); i++) {
                    addsales += Integer.parseInt(String.valueOf(sale.get(i)));

                    totalsales.setText("KSH " + String.valueOf(addsales));

                }
                addsales = 0;
                repoList.clear();
                sale.clear();
                for (int i = 0; i < nuitems.size(); i++) {
                    items += Float.parseFloat(String.valueOf(nuitems.get((int) i)));

                    itemsale.setText( String.valueOf(items));
                }
                items=0;
                nuitems.clear();
                for (int i = 0; i < totprofit.size(); i++) {
                    addprofit += Integer.parseInt(String.valueOf(totprofit.get(i)));

                    saleprofit.setText( String.valueOf(addprofit));
                }
                addprofit=0;
                totprofit.clear();

            }


        });
    }
    private void registerNetworkCallback(){


        try {

            connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(){

                @Override
                public void onAvailable(@NonNull Network network) {
                    isConnected = true;
                }

                @Override
                public void onLost(@NonNull Network network) {
                    isConnected = false;
                }
            });




        }catch (Exception e){

            isConnected = false;

        }

    }
    @Override
    public void onResume() {
        super.onResume();
        addsales = 0;
        items=0;
        addprofit=0;
        registerNetworkCallback();

    }
    @Override
    protected void onPause() {
        super.onPause();
        new ConnectivityManager.NetworkCallback();

    }

}
