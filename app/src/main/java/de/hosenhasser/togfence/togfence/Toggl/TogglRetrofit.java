package de.hosenhasser.togfence.togfence.Toggl;

import android.content.ContentResolver;
import android.content.Context;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import de.hosenhasser.togfence.togfence.GeofenceElement;
import de.hosenhasser.togfence.togfence.GeofencesContentProvider;
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
        Gson gsonTogglUser = new GsonBuilder()
                .registerTypeAdapter(TogglUser.class, new DataDeserializer<TogglUser>())
                .create();
        Gson gsonFullTogglUser = new GsonBuilder()
                .registerTypeAdapter(FullTogglUser.class, new DataDeserializer<FullTogglUser>())
                .create();
        Gson gsonTogglStartTimeEntryResponse = new GsonBuilder()
                .registerTypeAdapter(TogglStartTimeEntryResponse.class, new DataDeserializer<TogglStartTimeEntryResponse>())
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.toggl.com")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gsonFullTogglUser))
                .addConverterFactory(GsonConverterFactory.create(gsonTogglStartTimeEntryResponse))
                .addConverterFactory(GsonConverterFactory.create(gsonTogglUser))
                .build();
        service = retrofit.create(TogglService.class);

//                PreferenceManager.getDefaultSharedPreferences(TogfenceApplication.getAppContext()).getString("toggl_api_key", "INVALID")

        return true;
    }

    TogglRetrofit() {
        checkValid();
    }

    public void updateProjectsAndTags(final Context context) {
        if(!checkValid()) {
            return;
        }
        Call<FullTogglUser> call = service.fullme();
        call.enqueue(new Callback<FullTogglUser>() {
            @Override
            public void onResponse(Call<FullTogglUser> call, Response<FullTogglUser> response) {
                Log.i(TAG, response.code() + "");
                Log.i(TAG, "raw: " + response.raw().body().toString());
                FullTogglUser u = response.body();
                Log.i(TAG, "User: " + u.toString());
                int nProjects = 0;
                for(TogglProject p : u.projects) {
                    TogglContentProvider.updateOrInsertProjectElement(context.getContentResolver(), p);
                    nProjects += 1;
                }
                int nTags = 0;
                for(TogglTag t : u.tags) {
                    TogglContentProvider.updateOrInsertTagElement(context.getContentResolver(), t);
                    nTags += 1;
                }
                Log.i(TAG, "Updated Toggl data: " + Integer.toString(nProjects) + " Projects, " +
                    Integer.toString(nTags) + " Tags.");
                Toast.makeText(context, "Updated Toggl data: " + Integer.toString(nProjects) + " Projects, " +
                        Integer.toString(nTags) + " Tags.", Toast.LENGTH_LONG);
            }

            @Override
            public void onFailure(Call<FullTogglUser> call, Throwable t) {
                Log.i(TAG, "toggl user call failed: " + t.getMessage());
                call.cancel();
            }
        });
    }

    public void dumpMe() {
        if (!checkValid()) {
            return;
        }
        Call<FullTogglUser> call = service.fullme();
        call.enqueue(new Callback<FullTogglUser>() {
            @Override
            public void onResponse(Call<FullTogglUser> call, Response<FullTogglUser> response) {
                Log.i(TAG, response.code() + "");
                Log.i(TAG, "raw: " + response.raw().body().toString());
                FullTogglUser u = response.body();
                Log.i(TAG, "User: " + u.toString());
            }

            @Override
            public void onFailure(Call<FullTogglUser> call, Throwable t) {
                Log.i(TAG, "toggl user call failed: " + t.getMessage());
                call.cancel();
            }
        });

//        Call<TogglUser> call = service.me();
//        call.enqueue(new Callback<TogglUser>() {
//            @Override
//            public void onResponse(Call<TogglUser> call, Response<TogglUser> response) {
//                Log.i(TAG, response.code() + "");
//                Log.i(TAG, "raw: " + response.raw().body().toString());
//                TogglUser u = response.body();
//                Log.i(TAG, "User: " + u.toString());
//            }
//
//            @Override
//            public void onFailure(Call<TogglUser> call, Throwable t) {
//                Log.i(TAG, "toggl user call failed: " + t.getMessage());
//                call.cancel();
//            }
//        });

    }

    public void createNewStartTimeEntry(ContentResolver contentResolver, GeofenceElement ge) {
        if (!checkValid()) {
            return;
        }

        TogglStartTimeEntry te = new TogglStartTimeEntry();
        te.time_entry.created_with = "Togfence";
        te.time_entry.tags.add(ge.toggl_tag_text);
        te.time_entry.description = ge.name;

        final GeofenceElement thisge = ge;
        final ContentResolver thiscontentResolver = contentResolver;

        Call<TogglStartTimeEntryResponse> call = service.startTimeEntry(te);
        call.enqueue(new Callback<TogglStartTimeEntryResponse>() {
            @Override
            public void onResponse(Call<TogglStartTimeEntryResponse> call, Response<TogglStartTimeEntryResponse> response) {
                Log.i(TAG, response.code() + "");
                Log.i(TAG, "raw: " + response.raw().body().toString());
                TogglStartTimeEntryResponse u = response.body();
                Log.i(TAG, "timeentry respone: " + u.toString());
                thisge.running_entry_id = u.id;
                GeofencesContentProvider.updateGeofenceElement(thiscontentResolver, thisge);
            }

            @Override
            public void onFailure(Call<TogglStartTimeEntryResponse> call, Throwable t) {
                Log.i(TAG, "toggl start time entry call failed: " + t.getMessage());
                call.cancel();
            }
        });

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

    public void createNewStopTimeEntry(ContentResolver contentResolver, GeofenceElement ge) {
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
