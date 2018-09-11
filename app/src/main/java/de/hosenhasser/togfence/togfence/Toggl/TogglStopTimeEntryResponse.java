package de.hosenhasser.togfence.togfence.Toggl;

import java.util.ArrayList;
import java.util.List;

public class TogglStopTimeEntryResponse {
    public String description;
    public int id;
    public int uid;
    public int wid;
    public String guid;
    public boolean billable;
    public String start;
    public String stop;
    public int duration;
    public List<String> tags = new ArrayList<>();
    public boolean duronly;
    public String at;

    @Override
    public String toString() {
        return "TogglStopTimeEntryResponse\n" +
                "description: " + description + "\n" +
                "wid: " + Integer.toString(wid) + "\n" +
                "uid: " + Integer.toString(uid) + "\n" +
                "id: " + Integer.toString(id) + "\n" +
                "billable: " + (billable ? "true" : "false") + "\n" +
                "start: " + start + "\n" +
                "duration: " + Integer.toString(duration) + "\n" +
                "tags: " + tags.toString() + "\n" +
                "duronly: " + (duronly ? "true" : "false") + "\n" +
                "at: " + at;
     }
}
