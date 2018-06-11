package de.hosenhasser.togfence.togfence.Toggl;

public class TogglWorkspace {
    public String name;
    public boolean premium;
    public boolean admin;
    public float default_hourly_rate;
    public String default_currency;
    public boolean only_admins_may_create_projects;
    public boolean only_admins_see_billable_rates;
    public int rounding;
    public int rounding_minutes;
    public String at;
    public String logo_url;

    @Override
    public String toString() {
        return "TogglWorkspace\n" +
                "name: " + name + "\n" +
                "premium: " + (premium ? "true" : "false") + "\n" +
                "admin: " + (admin ? "true" : "false") + "\n" +
                "default_hourly_rate: " + Float.toString(default_hourly_rate) + "\n" +
                "default_currency: " + default_currency + "\n" +
                "only_admins_may_create_projects: " + (only_admins_may_create_projects ? "true" : "false") + "\n" +
                "only_admins_see_billable_rates: " + (only_admins_see_billable_rates ? "true" : "false") + "\n" +
                "rounding: " + Integer.toString(rounding) + "\n" +
                "rounding_minutes: " + Integer.toString(rounding_minutes) + "\n" +
                "at: " + at + "\n" +
                "logo_url: " + logo_url;
    }
}
