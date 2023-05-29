package com.example.firebaseemailaccount;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.CookieManager;

import okhttp3.CookieJar;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofit_client {
    // private static final String BASE_URL = "";
    // login activity에서 사용하기 위해 public으로 수정
    public static final String BASE_URL = "https://e226-121-131-237-41.ngrok-free.app/";

    private static Gson gson = new GsonBuilder().setLenient().create();

    public static OkHttpClient client = new OkHttpClient.Builder()
            .cookieJar(new Cookie_jar())
            .build();
    private static Retrofit network_client = new Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();


    public static CommunityInterface getApiService(){return network_client.create(CommunityInterface.class);}

    public static CommentInterface getCommentApiService(){return network_client.create(CommentInterface.class);}

    public static UserInterface getUserApiService(){return network_client.create(UserInterface.class);}
}