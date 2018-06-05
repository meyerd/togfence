package de.hosenhasser.togfence.togfence.Toggl;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TogglService {
    @GET("/api/v8/me")
    Call<TogglUser> me();
}