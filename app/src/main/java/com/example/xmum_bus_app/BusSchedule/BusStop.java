package com.example.xmum_bus_app.BusSchedule;

public class BusStop {

    private String Sequence, Title;

    public BusStop(String sequence, String title) {
        Sequence = sequence;
        Title = title;
    }

    public BusStop() {
    }

    public String getSequence() {
        return Sequence;
    }

    public String getTitle() {
        return Title;
    }

}
