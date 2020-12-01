package com.example.retrofit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    public static final String ID_TODO = "idToDo";

    private ListView lvToDos;
    Activity activity;
    FloatingActionButton fabAddToDo;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress = new ProgressDialog(MainActivity.this);
        progress.setCancelable(false);
        activity = this;
        lvToDos = (ListView)findViewById(R.id.lvToDos);
        fabAddToDo = (FloatingActionButton)findViewById(R.id.fabAddToDo);

        fabAddToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AddToDo.class);
                startActivityForResult(intent,0);
            }
        });
        loadToDos();
    }

    private void loadToDos() {
        showProgress(getString(R.string.loadToDos));
        //conectarse
        Retrofit retrofit = API.getRetrofitClient();
        ToDoAPI api = retrofit.create(ToDoAPI.class);
        Call<List<ToDo>> apiCall = api.getAllToDos();

        apiCall.enqueue(new Callback<List<ToDo>>() {
            @Override
            public void onResponse(Call<List<ToDo>> call, final Response<List<ToDo>> response) {
                hideProgress();
                //cargar lista en ListView
                ToDoList lista = new ToDoList(activity, response.body());
                lvToDos.setAdapter(lista);
                lvToDos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getBaseContext(), DetailTodo.class);
                        intent.putExtra(ID_TODO,response.body().get(position).getId());
                        startActivityForResult(intent,0);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<ToDo>> call, Throwable t) {
                hideProgress();
                Toast.makeText(activity, R.string.loadToDosUnsuccessfully, Toast.LENGTH_LONG).show();
                Log.d(getString(R.string.retroError),t.toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadToDos();
    }

    public void hideProgress(){
        if(progress.isShowing()){
            progress.dismiss();
        }
    }

    public void showProgress(String mensaje){
        progress.setMessage(mensaje);
        progress.show();
    }
}