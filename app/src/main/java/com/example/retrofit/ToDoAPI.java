package com.example.retrofit;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;

public interface ToDoAPI {

    @GET("/todos")
    Call<List<ToDo>> getAllToDos();

    @GET("/todos/{id}")
    Call<ToDo> getToDo(@Path("id") Integer id);

    @DELETE("/todos/{id}")
    Call<ToDo> deleteToDo(@Path("id") Integer id);

    @FormUrlEncoded
    @PATCH("todos/{id}")
    Call<ToDo> updateToDo(
            @Field("id") Integer id,
            @Field("userId") Integer userId,
            @Field("title") String title,
            @Field("completed") Boolean completed
    );

    @FormUrlEncoded
    @POST("todos/")
    Call<ToDo> addToDo(
            @Field("userId") Integer userId,
            @Field("title") String title,
            @Field("completed") Boolean completed
    );
}
