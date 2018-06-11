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
    public String jquery_date_format;
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
                "id: " + Integer.toString(id) + "\n" +
                "api_token: " + api_token + "\n" +
                "default_wid: " + Integer.toString(default_wid) + "\n" +
                "email: " + email + "\n" +
                "fullname: " + fullname + "\n" +
                "jquery_timeofday_format: " + jquery_timeofday_format + "\n" +
                "jquery_date_format: " + jquery_date_format + "\n" +
                "timeofday_format: " + timeofday_format + "\n" +
                "date_format: " + date_format + "\n" +
                "store_start_and_stop_time: " + (store_start_and_stop_time ? "true" : "false") + "\n" +
                "beginning_of_week" + Integer.toString(beginning_of_week) + "\n" +
                "language: " + language + "\n" +
                "image_url: " + image_url + "\n" +
                "sidebar_piechart: " + (sidebar_piechart ? "true" : "false") + "\n" +
                "at: " + at + "\n" +
                "new_bloc_post: " + new_blog_post.toString() + "\n" +
                "send_product_emails: " + (send_product_emails ? "true" : "false") + "\n" +
                "send_weekly_report: " + (send_weekly_report ? "true" : "false") + "\n" +
                "send_timer_notifications: " + (send_timer_notifications ? "true" : "false") + "\n" +
                "openid_enabled: " + (openid_enabled ? "true" : "false") + "\n" +
                "timezone: " + timezone + "\n" +
                "clients: " + clients.toString() + "\n" +
                "projects: " + projects.toString() + "\n" +
                "tags: " + tags.toString() + "\n" +
                "tasks: " + tasks.toString() + "\n" +
                "workspaces: " + workspaces.toString() + "\n" +
                "time_entries: " + time_entries.toString() + "\n";
    }
}
