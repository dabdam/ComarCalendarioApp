package com.example.comarcalendarioapp.gui.activity;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.comarcalendarioapp.R;
import com.example.comarcalendarioapp.gui.dialog.DatePickerFragment;
import com.example.comarcalendarioapp.model.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText etTaskDescription;
    private EditText etStartDate;
    private EditText etDaysLimit;
    private Spinner placeSetter;
    private Button btnSave;
    private CheckBox chkCalendar;
    private Task newTask;
    private String description;
    private int startYear;
    private int startMonth;
    private int startDay;
    private String place;
    private int daysLimit;
    private ArrayList<LocalDate> calendarList;
    private final String DESCRIPTION_KEY ="description";
    private final String LIMITDATE_KEY ="date";
    private final String REF_KEY="author";
    private final String PLACE_KEY="place";
    private FirebaseFirestore bd = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        etTaskDescription=(EditText) findViewById(R.id.etTaskDescription);
        etStartDate=(EditText) findViewById(R.id.etStartDate);
        etDaysLimit=(EditText) findViewById(R.id.etDaysLimit);
        placeSetter=(Spinner) findViewById(R.id.placeSetter);
        btnSave=(Button) findViewById(R.id.btnSave);
        chkCalendar=(CheckBox) findViewById(R.id.chkCalendar);


        etStartDate.setOnClickListener(v -> {
            showDatePickerDialog();
        });

        placeSetter.setOnItemSelectedListener(this);

        btnSave.setOnClickListener(v -> {
            clickSave();
        });
    }

    //Método para guardar el elemento en la bbdd, primero se filtra que esté toda la información
    private void clickSave() {
        if(filterData()){
            saveData(newTask);
            if(chkCalendar.isChecked()){
                clickCalendar();
            }
            clearFields();
            Toast.makeText(this, "Tarea "+newTask.getDescription()+" guardada", Toast.LENGTH_LONG).show();
        }
    }

    //Método que muestra un Dialog con calendario para elegir la fecha de inicio
    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                startYear=year;
                startMonth=month+1;
                startDay=day;
                String selectedDate = day + " / " + (month+1) + " / " + year;
                etStartDate.setText(selectedDate);
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    //Métodos necesarios para el spinner en el que se elige el partido judicial
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        place=item;

        calendarList=new ArrayList<LocalDate>();
        readData(new FirestoreCallBack() {
            @Override
            public void onCallBack(ArrayList<LocalDate> dateList) {

            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        place="";
    }

    //Método para limpiar los campos después de introducirlos correctamente
    public void clearFields(){
        etTaskDescription.setText("");
        etDaysLimit.setText("");
        etStartDate.setText("");
        chkCalendar.setChecked(false);
    }

    //Método que comprueba que todos los campos estén completos y genera un objeto del tipo Task
    public boolean filterData(){
        if(TextUtils.isEmpty(etTaskDescription.getText().toString())) {
            Toast.makeText(this, "Introduce una descripción", Toast.LENGTH_LONG).show();
            return false;
        } else {
            if(place.isEmpty()) {
                Toast.makeText(this, "Introduce el partido judicial", Toast.LENGTH_LONG).show();
                return false;
            } else {
                if(TextUtils.isEmpty(etDaysLimit.getText())) {
                    Toast.makeText(this, "Introduce el plazo establecido", Toast.LENGTH_LONG).show();
                    return false;
                } else {
                    if(TextUtils.isEmpty(etStartDate.getText().toString())) {
                        Toast.makeText(this, "Introduce la fecha inicial", Toast.LENGTH_LONG).show();
                        return false;
                    } else {
                        description = String.valueOf(etTaskDescription.getText());
                        try {
                            daysLimit = Integer.parseInt(etDaysLimit.getText().toString());
                        } catch (NumberFormatException e) {
                            daysLimit = 0;
                        }
                        newTask = new Task(description, startYear, startMonth, startDay, daysLimit, place, calendarList);
                        return true;
                    }
                }
            }
        }
    }

    //método para guardar en firestore en forma de documento el objeto de tipo Task que recibe
    public void saveData(Task taskToSave){
        Bundle bundle = this.getIntent().getExtras();

        Map<String, Object> datoGuardar = new HashMap<String,Object>();

        String taskId= taskToSave.getId();
        String nombre= taskToSave.getDescription();
        String fechaLimite= taskToSave.getLimitDate().toString();
        String lugar= taskToSave.getPlace();
        String referencia= bundle.getString("user");

        datoGuardar.put(REF_KEY, referencia);
        datoGuardar.put(DESCRIPTION_KEY,nombre);
        datoGuardar.put(LIMITDATE_KEY,fechaLimite);
        datoGuardar.put(PLACE_KEY, lugar);

        bd.collection("elementos").document(taskId).set(datoGuardar).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("OK", "El documento ha sido guardado");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("OK","El documento no ha sido guardado");
            }
        });
    }

    //método para almacenar un evento en el calendario
    public void clickCalendar(){
        if(filterData()) {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            long date=0;
            try {
                Date taskDate = f.parse(newTask.getLimitDate().toString());
                date = taskDate.getTime();
            } catch (ParseException pe){
                pe.printStackTrace();
            }

            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setData(CalendarContract.Events.CONTENT_URI);
            intent.putExtra(CalendarContract.Events.TITLE, newTask.getDescription());
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, date);
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, date);
            intent.putExtra(CalendarContract.Events.ALL_DAY, false);

            if(intent.resolveActivity(getPackageManager())!=null){
                startActivity(intent);
            } else {
                Log.w("OK","Problemas en la inserción en el Calendario");
            }
        }
    }

    //Se ejecuta en el listener del "comboBox" y recupera en la bbdd los datos asociados al lugar seleccionado
    private void readData(FirestoreCallBack myCallBack){
        bd.collection("lugares").document(place).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document =task.getResult();
                    ArrayList<String> arrList = new ArrayList<>();
                    arrList = (ArrayList) document.get("fechas");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    for(String string: arrList){
                        calendarList.add(LocalDate.parse(string, formatter));
                    }
                    myCallBack.onCallBack(calendarList);
                    System.out.println(arrList.toString());
                }
            }
        });
    }

    //Interfaz necesaria para el método anterior, que ejecuta un callback
    private interface FirestoreCallBack{
        void onCallBack(ArrayList<LocalDate> dateList);
    }
}
