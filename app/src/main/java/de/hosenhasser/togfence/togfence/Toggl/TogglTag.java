package de.hosenhasser.togfence.togfence.Toggl;

public class TogglTag {
    public String name;
    public int wid;

    @Override
    public String toString() {
        return "TogglTag\n" +
                "name: " + name + "\n" +
                "wid: " + Integer.toString(wid);
    }
}
