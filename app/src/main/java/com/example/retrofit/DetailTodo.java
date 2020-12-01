package com.example.retrofit;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.Nullable;
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

public class DetailTodo extends AppCompatActivity {

    final String ALERT_STATE = "state_of_Alert";
    final String MESSAGE_ALERT = "message_alert";
    final String TITLE_ALERT = "title_alert";
    final String FINISH = "finish";
    private int idToDo;
    private boolean isAlertDisplayed = false;
    private boolean isFinish = false;
    private String messageAlert = "";
    private String titleAlert = "";
    ProgressDialog progress;
    TextInputEditText tietUserIdDetail;
    TextInputEditText tietTitleToDoDetail;
    CheckBox chkCompleteToDoDetail;
    AwesomeValidation awesomeValidation;
    Button btnDeleteToDo;
    Button btnUpdateToDo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_todo);

        if (savedInstanceState != null) {
            isAlertDisplayed = savedInstanceState.getBoolean(ALERT_STATE);
            isFinish = savedInstanceState.getBoolean(FINISH);
            messageAlert = savedInstanceState.getString(MESSAGE_ALERT);
            titleAlert = savedInstanceState.getString(TITLE_ALERT);
            if (isAlertDisplayed){
                showAlert(messageAlert,titleAlert);
            }
        }

        tietUserIdDetail = (TextInputEditText)findViewById(R.id.tietUserIdDetail);
        tietTitleToDoDetail = (TextInputEditText)findViewById(R.id.tietTitleToDoDetail);
        chkCompleteToDoDetail = (CheckBox)findViewById(R.id.chkCompleteToDoDetail);
        btnDeleteToDo = (Button)findViewById(R.id.btnDeleteToDo);
        btnUpdateToDo = (Button)findViewById(R.id.btnUpdateToDo);
        idToDo = getIntent().getIntExtra(MainActivity.ID_TODO,0);

        progress = new ProgressDialog(DetailTodo.this);
        progress.setCancelable(false);

        awesomeValidation = new AwesomeValidation(ValidationStyle.COLORATION);
        awesomeValidation.addValidation(this,R.id.tietUserIdDetail, RegexTemplate.NOT_EMPTY,R.string.errorUserIdEmpty);
        awesomeValidation.addValidation(this,R.id.tietTitleToDoDetail, RegexTemplate.NOT_EMPTY,R.string.errorTitleEmpty);

        btnUpdateToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()){
                    updateToDo();
                }
            }
        });

        btnDeleteToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()){
                    deleteToDo();
                }
            }
        });

        cargarDetallePersona();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(ALERT_STATE,isAlertDisplayed);
        savedInstanceState.putBoolean(FINISH,isFinish);
        savedInstanceState.putString(MESSAGE_ALERT,messageAlert);
        savedInstanceState.putString(TITLE_ALERT,titleAlert);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void deleteToDo() {
        mostrarProgress(getString(R.string.deletingToDo));

        Retrofit retrofit = API.getRetrofitClient();
        ToDoAPI api = retrofit.create(ToDoAPI.class);
        Call<ToDo> apiCall = api.deleteToDo(idToDo);

        apiCall.enqueue(new Callback<ToDo>() {
            @Override
            public void onResponse(Call<ToDo> call, Response<ToDo> response) {
                ocultarProgress();
                messageAlert = getResources().getString(R.string.deleteToDoSuccessfully);
                titleAlert = getResources().getString(R.string.result);
                isFinish = true;
                showAlert(messageAlert, titleAlert);
                isAlertDisplayed = true;
            }

            @Override
            public void onFailure(Call<ToDo> call, Throwable t) {
                ocultarProgress();
                messageAlert = getResources().getString(R.string.deleteToDoUnsuccessfully);
                titleAlert = getResources().getString(R.string.result);
                isFinish = false;
                showAlert(messageAlert, titleAlert);
                isAlertDisplayed = true;
                Log.d(getString(R.string.retroError),t.toString());
            }
        });
    }

    private void updateToDo() {

        mostrarProgress(getString(R.string.updatingToDo));

        Retrofit retrofit = API.getRetrofitClient();
        ToDoAPI api = retrofit.create(ToDoAPI.class);
        Call<ToDo> apiCall = api.updateToDo(idToDo,Integer.parseInt(tietUserIdDetail.getText().toString()), tietTitleToDoDetail.getText().toString(), chkCompleteToDoDetail.isChecked());

        apiCall.enqueue(new Callback<ToDo>() {
            @Override
            public void onResponse(Call<ToDo> call, Response<ToDo> response) {
                ocultarProgress();
                cargarDetallePersona();
                messageAlert = getResources().getString(R.string.updateToDoSuccessfully);
                titleAlert = getResources().getString(R.string.result);
                isFinish = false;
                showAlert(messageAlert, titleAlert);
                isAlertDisplayed = true;
            }

            @Override
            public void onFailure(Call<ToDo> call, Throwable t) {
                ocultarProgress();
                messageAlert = getResources().getString(R.string.updateToDoUnsuccessfully);
                titleAlert = getResources().getString(R.string.result);
                isFinish = false;
                showAlert(messageAlert, titleAlert);
                isAlertDisplayed = true;
                Log.d(getString(R.string.retroError),t.toString());
            }
        });
    }

    private void cargarDetallePersona() {
        mostrarProgress(getString(R.string.loadTodo));
        Retrofit retrofit = API.getRetrofitClient();
        ToDoAPI api = retrofit.create(ToDoAPI.class);
        Call<ToDo> apiCall = api.getToDo(idToDo);

        apiCall.enqueue(new Callback<ToDo>() {
            @Override
            public void onResponse(Call<ToDo> call, Response<ToDo> response) {
                ocultarProgress();
                tietUserIdDetail.setText(String.valueOf(response.body().getUserId()));
                tietTitleToDoDetail.setText(response.body().getTitle());
                chkCompleteToDoDetail.setChecked(response.body().getCompleted());
            }

            @Override
            public void onFailure(Call<ToDo> call, Throwable t) {
                ocultarProgress();
                messageAlert = getResources().getString(R.string.loadToDoUnsuccessfully);
                titleAlert = getResources().getString(R.string.result);
                isFinish = false;
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
        progress.setMessage(mensaje);
        progress.show();
    }
}