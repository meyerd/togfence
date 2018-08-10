package de.hosenhasser.togfence.togfence.Toggl;

public class TogglTag {
    public int id;
    public String name;
    public int wid;
    public String at;

//    @Override
//    public String toString() {
//        return "TogglTag\n" +
//                "name: " + name + "\n" +
//                "wid: " + Integer.toString(wid);
//    }
    @Override
    public String toString() {
        return name;
    }
}
