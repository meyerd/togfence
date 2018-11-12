package de.hosenhasser.togfence.togfence.Toggl;

import java.util.ArrayList;
import java.util.List;

public class TogglCurrentTimeEntryResponse {
    public String description;
    public int id;
    public String guid;
    public int uid;
    public int wid;
    public int pid;
    public boolean billable;
    public String start;
    public int duration;
    public List<String> tags = new ArrayList<>();
    public boolean duronly;
    public String at;

    @Override
    public String toString() {
        return "TogglCurrentTimeEntryResponse\n" +
                "description: " + description + "\n" +
                "guid: " + guid + "\n" +
                "wid: " + Integer.toString(wid) + "\n" +
                "uid: " + Integer.toString(uid) + "\n" +
                "id: " + Integer.toString(id) + "\n" +
                "pid: " + Integer.toString(pid) + "\n" +
                "billable: " + (billable ? "true" : "false") + "\n" +
                "start: " + start + "\n" +
                "duration: " + Integer.toString(duration) + "\n" +
                "tags: " + tags.toString() + "\n" +
                "duronly: " + (duronly ? "true" : "false") + "\n" +
                "at: " + at;
     }
}
