package de.hosenhasser.togfence.togfence.Toggl;

public class TogglTask {
    public String name;
    public int pid;
    public int wid;
    public int uid;
    public int estimated_seconds;
    public boolean active;
    public String at;
    public int tracked_seconds;

    @Override
    public String toString() {
        return "TogglTask\n" +
                "name: " + name + "\n" +
                "pid: " + Integer.toString(pid) + "\n" +
                "wid: " + Integer.toString(wid) + "\n" +
                "uid: " + Integer.toString(uid) + "\n" +
                "estimated_seconds: " + Integer.toString(estimated_seconds) + "\n" +
                "active: " + (active ? "true" : "false") + "\n" +
                "at: " + at + "\n" +
                "tracked_seconds: " + Integer.toString(tracked_seconds);
    }
}
