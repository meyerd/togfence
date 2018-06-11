package de.hosenhasser.togfence.togfence.Toggl;

import java.util.ArrayList;
import java.util.List;

public class TogglTimeEntry {
    public String description;
    public int wid;
    public int pid;
    public int tid;
    public boolean billable;
    public String start;
    public String stop;
    public int duration;
    public String created_with;
    public List<String> tags = new ArrayList<>();
    public boolean duronly;
    public String at;

    @Override
    public String toString() {
        return "TogglTimeEntry\n" +
                "description: " + description + "\n" +
                "wid: " + Integer.toString(wid) + "\n" +
                "pid: " + Integer.toString(pid) + "\n" +
                "tid: " + Integer.toString(tid) + "\n" +
                "billable: " + (billable ? "true" : "false") + "\n" +
                "start: " + start + "\n" +
                "stop: " + stop + "\n" +
                "duration: " + Integer.toString(duration) + "\n" +
                "created_with: " + created_with + "\n" +
                "tags: " + tags.toString() + "\n" +
                "duronly: " + (duronly ? "true" : "false") + "\n" +
                "at: " + at;
     }
}
