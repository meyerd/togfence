package de.hosenhasser.togfence.togfence.Toggl;

import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import de.hosenhasser.togfence.togfence.R;
import de.hosenhasser.togfence.togfence.TogfenceApplication;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TogglRetrofit {
    public static final String TAG = "TogglRetrofit";
    private static volatile TogglRetrofit sJToggleSingletonSingletonInstance;

    public static TogglRetrofit getInstance() {
        if (sJToggleSingletonSingletonInstance == null) {
            synchronized (TogglRetrofit.class) {
                if (sJToggleSingletonSingletonInstance == null) sJToggleSingletonSingletonInstance = new TogglRetrofit();
            }
        }

        return sJToggleSingletonSingletonInstance;
    }

    protected TogglRetrofit readResolve() {
        return getInstance();
    }

    private Retrofit retrofit;
    private OkHttpClient client;
    private TogglService service;

    private boolean checkApiKey() {
        String toggl_api_key = PreferenceManager.getDefaultSharedPreferences(TogfenceApplication.getAppContext()).getString("toggl_api_key", "INVALID");
        if (toggl_api_key.equals("INVALID")) {
            return false;
        }
        return true;
    }

    private boolean checkValid() {
        if (retrofit != null && service != null && client != null) {
            return true;
        }
        if (!checkApiKey()) {
            Toast.makeText(
                    TogfenceApplication.getAppContext(),
                    TogfenceApplication.getAppContext().getString(R.string.no_valid_toggl_api_key),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        String toggl_api_key = PreferenceManager.getDefaultSharedPreferences(TogfenceApplication.getAppContext()).getString("toggl_api_key", "INVALID");

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(toggl_api_key))
                .addInterceptor(logging)
                .build();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(TogglUser.class, new DataDeserializer<TogglUser>())
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.toggl.com")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        service = retrofit.create(TogglService.class);

//                PreferenceManager.getDefaultSharedPreferences(TogfenceApplication.getAppContext()).getString("toggl_api_key", "INVALID")

        return true;
    }

    TogglRetrofit() {
        checkValid();
    }

    public void dumpMe() {
        if (!checkValid()) {
            return;
        }
        Call<TogglUser> call = service.me();
        call.enqueue(new Callback<TogglUser>() {
            @Override
            public void onResponse(Call<TogglUser> call, Response<TogglUser> response) {
                Log.i(TAG, response.code() + "");
                Log.i(TAG, "raw: " + response.raw().body().toString());
                TogglUser u = response.body();
                Log.i(TAG, "User: " + u.toString());
            }

            @Override
            public void onFailure(Call<TogglUser> call, Throwable t) {
                Log.i(TAG, "toggl user call failed: " + t.getMessage());
                call.cancel();
            }
        });

    }

    public void createNewStartTimeEntry(String name) {
        if (!checkValid()) {
            return;
        }

//        TimeEntry entry = new TimeEntry();
//        DateTime dt = new DateTime();
//        entry.setStart(dt);
//        entry.setDuration(1);
//        entry.setDescription(
//                TogfenceApplication.getAppContext().getString(R.string.toggl_description_start) +
//                        "|" + name
//        );
//        entry.setCreatedWith(TogfenceApplication.getAppContext().getString(R.string.toggl_created_with));
//
//        entry = jToggl.createTimeEntry(entry);
    }

    public void createNewStopTimeEntry(String name) {
        if (!checkValid()) {
            return;
        }

//        TimeEntry entry = new TimeEntry();
//        DateTime dt = new DateTime();
//        entry.setStart(dt);
//        entry.setDuration(1);
//        entry.setDescription(
//                TogfenceApplication.getAppContext().getString(R.string.toggl_description_stop) +
//                        "|" + name
//        );
//        entry.setCreatedWith(TogfenceApplication.getAppContext().getString(R.string.toggl_created_with));
//
//        entry = jToggl.createTimeEntry(entry);
    }

}
