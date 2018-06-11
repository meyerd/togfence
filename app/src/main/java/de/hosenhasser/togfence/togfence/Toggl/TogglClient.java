package de.hosenhasser.togfence.togfence.Toggl;

public class TogglClient {
    public String name;
    public int wid;
    public String notes;
    public String at;

    @Override
    public String toString() {
        return "TogglClient:\n" +
                "wid: " + Integer.toString(wid) + "\n" +
                "notes: " + notes + "\n" +
                "at: " + at + "\n";
    }
}
