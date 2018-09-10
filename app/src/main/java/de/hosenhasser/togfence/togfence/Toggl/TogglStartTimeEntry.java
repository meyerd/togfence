package de.hosenhasser.togfence.togfence.Toggl;

import java.util.ArrayList;
import java.util.List;

class TogglInnerStartTimeEntry {
    String description;
    public List<String> tags = new ArrayList<>();
    public int pid;
    public String created_with;
}

public class TogglStartTimeEntry {
    public TogglInnerStartTimeEntry time_entry = new TogglInnerStartTimeEntry();
}
