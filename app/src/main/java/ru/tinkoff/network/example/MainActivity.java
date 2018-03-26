package ru.tinkoff.network.example;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultTextView = findViewById(R.id.resultContent);
        findViewById(R.id.performRequest).setOnClickListener(v -> performRequest());
    }

    private void printResult(@Nullable String resultString) {
        resultTextView.setText(resultString);
    }

    private void performRequest() {
        Single.fromCallable(new BackgroundTask())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::printResult,
                        throwable -> printResult(
                                "Exception:\n" + throwable.toString()
                        )
                );
    }

    static class BackgroundTask implements Callable<String> {

        @SuppressLint("DefaultLocale")
        @SuppressWarnings("ConstantConditions")
        @Override
        public String call() throws Exception {
            //https://docs.postman-echo.com
            //https://github.com/square/okhttp/wiki/Recipes
            OkHttpClient client = new OkHttpClient.Builder()
                    .addNetworkInterceptor(new StethoInterceptor())
                    .build();

            Request request = new Request.Builder()
                    .url("https://postman-echo.com/basic-auth")
                    .header("Authorization", Credentials.basic("postman", "password"))
                    .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                return String.format(
                        "Not successful request:\n%d %s",
                        response.code(),
                        response.message()
                );
            }

            return response.body().string();
        }
    }
}