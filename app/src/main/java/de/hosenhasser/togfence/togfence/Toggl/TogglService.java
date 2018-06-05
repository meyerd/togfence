package de.hosenhasser.togfence.togfence.Toggl;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TogglService {
    @GET("/api/v8/me")
    Call<TogglUser> me();

    @GET("/api/v8/me?with_related_data=true")
    Call<FullTogglUser> fullme();
}