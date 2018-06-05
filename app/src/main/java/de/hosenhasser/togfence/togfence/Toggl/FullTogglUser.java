package de.hosenhasser.togfence.togfence.Toggl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FullTogglUser {
    public int id;
    public String api_token;
    public int default_wid;
    public String email;
//    @SerializedName("fullname")
    public String fullname;
    public String jquery_timeofday_format;
    public String jquery_data_format;
    public String timeofday_format;
    public String date_format;
    public boolean store_start_and_stop_time;
    public int beginning_of_week;
    public String language;
    public String image_url;
    public boolean sidebar_piechart;
    public String at;
    public Map<String, String> new_blog_post = new HashMap<>();
    public boolean send_product_emails;
    public boolean send_weekly_report;
    public boolean send_timer_notifications;
    public boolean openid_enabled;
    public String timezone;
    public List<TogglClient> clients = new ArrayList<>();
    public List<TogglProject> projects = new ArrayList<>();
    public List<TogglTag> tags = new ArrayList<>();
    public List<TogglTask> tasks = new ArrayList<>();
    public List<TogglWorkspace> workspaces = new ArrayList<>();
    public List<TogglTimeEntry> time_entries = new ArrayList<>();

    @Override
    public String toString() {
        return "FullTogglUser: \n" +
                "id: " + Integer.toString(id) +
                "api_token: " + api_token + fullname + ", " + email;
    }
}
