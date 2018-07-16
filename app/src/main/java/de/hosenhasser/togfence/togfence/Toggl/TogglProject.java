package de.hosenhasser.togfence.togfence.Toggl;

public class TogglProject {
    public int id;
    public int wid;
    public int cid;
    public String name;
    public boolean billable;
    public boolean is_private;
    public boolean active;
    public boolean template;
    public String at;
    public String created_at;
    public String color;
    public int template_id;
    public boolean auto_estimates;
    public int estimated_hours;
    public String hex_color;
    public float rate;

    @Override
    public String toString() {
        return "TogglProject\n" +
                "name: " + name + "\n" +
                "wid: " + Integer.toString(wid) + "\n" +
                "cid: " + Integer.toString(cid) + "\n" +
                "active: " + (active ? "true" : "false") + "\n" +
                "is_private: " + (active ? "true" : "false") + "\n" +
                "template: " + (template ? "true" : "false") + "\n" +
                "template_id: " + Integer.toString(template_id) + "\n" +
                "billable: " + (billable ? "true" : "false") + "\n" +
                "auto_estimates: " + (auto_estimates ? "true" : "false") + "\n" +
                "estimated_hours: " + Integer.toString(estimated_hours) + "\n" +
                "at: " + at + "\n" +
                "color: " + color + "\n" +
                "rate: " + Float.toString(rate) + "\n";
    }
}
