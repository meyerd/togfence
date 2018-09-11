package de.hosenhasser.togfence.togfence.Toggl;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TogglService {
    @GET("/api/v8/me")
    Call<TogglUser> me();

    @GET("/api/v8/me?with_related_data=true")
    Call<FullTogglUser> fullme();

    @POST("/api/v8/time_entries/start")
    Call<TogglStartTimeEntryResponse> startTimeEntry(@Body TogglStartTimeEntry startTimeEntry);

    @PUT("/api/v8/time_entries/{time_entry_id}/stop")
    Call<TogglStopTimeEntryResponse> stopTimeEntry(@Path("time_entry_id") int time_entry_id);
}