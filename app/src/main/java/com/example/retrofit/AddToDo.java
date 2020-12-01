package com.example.retrofit;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddToDo extends AppCompatActivity {

    final String ALERT_STATE = "state_of_Alert";
    final String MESSAGE_ALERT = "message_alert";
    final String TITLE_ALERT = "title_alert";
    final String FINISH = "finish";
    private boolean isAlertDisplayed = false;
    private boolean isFinish = false;
    private String messageAlert = "";
    private String titleAlert = "";
    Button btnAddToDo;
    TextInputEditText tietUserIdAdd;
    TextInputEditText tietTitleToDoAdd;
    CheckBox chkCompleteToDoAdd;
    ProgressDialog progress;
    AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_todo);

        if (savedInstanceState != null) {
            isAlertDisplayed = savedInstanceState.getBoolean(ALERT_STATE);
            isFinish = savedInstanceState.getBoolean(FINISH);
            messageAlert = savedInstanceState.getString(MESSAGE_ALERT);
            titleAlert = savedInstanceState.getString(TITLE_ALERT);
            if (isAlertDisplayed){
                showAlert(messageAlert,titleAlert);
            }
        }

        btnAddToDo = (Button)findViewById(R.id.btnAddToDo);
        tietUserIdAdd = (TextInputEditText)findViewById(R.id.tietUserIdAdd);
        tietTitleToDoAdd = (TextInputEditText)findViewById(R.id.tietTitleToDoAdd);
        chkCompleteToDoAdd = (CheckBox) findViewById(R.id.chkCompleteToDoAdd);

        progress = new ProgressDialog(com.example.retrofit.AddToDo.this);
        progress.setCancelable(false);

        awesomeValidation = new AwesomeValidation(ValidationStyle.COLORATION);
        awesomeValidation.addValidation(this,R.id.tietUserIdAdd, RegexTemplate.NOT_EMPTY,R.string.errorUserIdEmpty);
        awesomeValidation.addValidation(this,R.id.tietTitleToDoAdd, RegexTemplate.NOT_EMPTY,R.string.errorTitleEmpty);

        btnAddToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()){
                    addToDo();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(ALERT_STATE,isAlertDisplayed);
        savedInstanceState.putBoolean(FINISH,isFinish);
        savedInstanceState.putString(MESSAGE_ALERT,messageAlert);
        savedInstanceState.putString(TITLE_ALERT,titleAlert);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void addToDo() {
        mostrarProgress(getString(R.string.addToDo));

        Retrofit retrofit = API.getRetrofitClient();
        ToDoAPI api = retrofit.create(ToDoAPI.class);
        Call<ToDo> apiCall = api.addToDo(Integer.parseInt(tietUserIdAdd.getText().toString()), tietTitleToDoAdd.getText().toString(), chkCompleteToDoAdd.isChecked());

        apiCall.enqueue(new Callback<ToDo>() {
            @Override
            public void onResponse(Call<ToDo> call, Response<ToDo> response) {
                ocultarProgress();
                messageAlert = getResources().getString(R.string.addToDoSuccessfully);
                titleAlert = getResources().getString(R.string.result);
                isFinish = true;
                showAlert(messageAlert, titleAlert);
                isAlertDisplayed = true;
            }

            @Override
            public void onFailure(Call<ToDo> call, Throwable t) {
                ocultarProgress();
                messageAlert = getResources().getString(R.string.addToDoUnsuccessfully);
                titleAlert = getResources().getString(R.string.result);
                isFinish = true;
                showAlert(messageAlert, titleAlert);
                isAlertDisplayed = true;
                Log.d(getString(R.string.retroError),t.toString());
            }
        });
    }

    private void showAlert(String message, String title) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setCancelable(false);

        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isAlertDisplayed=false;
                if (isFinish){
                    finish();
                }
            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void ocultarProgress(){
        if(progress.isShowing()){
            progress.dismiss();
        }
    }

    public void mostrarProgress(String mensaje){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        progress.setMessage(mensaje);
        progress.show();
    }
}