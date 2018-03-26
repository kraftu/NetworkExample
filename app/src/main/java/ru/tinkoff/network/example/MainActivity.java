package ru.tinkoff.network.example;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
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

        @SuppressWarnings("ConstantConditions")
        @Override
        public String call() throws Exception {
            //https://httpbin.org/html
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://httpbin.org/html")
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        }
    }
}