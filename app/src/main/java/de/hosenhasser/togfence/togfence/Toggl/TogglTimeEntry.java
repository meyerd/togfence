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
}
