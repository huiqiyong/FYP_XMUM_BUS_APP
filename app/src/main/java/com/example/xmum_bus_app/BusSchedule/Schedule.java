package com.example.xmum_bus_app.BusSchedule;

public class Schedule {

    private String ImageUrl, Session, SessionId, Route, DepartTime1, AvailableSeat, Stop1;

    public Schedule(String imageUrl, String session, String sessionId, String route, String departTime1, String availableSeat, String stop1) {
        ImageUrl = imageUrl;
        Session = session;
        SessionId = sessionId;
        Route = route;
        DepartTime1 = departTime1;
        AvailableSeat = availableSeat;
        Stop1 = stop1;
    }

    public Schedule() {
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getSession() {
        return Session;
    }

    public void setSession(String session) {
        Session = session;
    }

    public String getSessionId() {
        return SessionId;
    }

    public void setSessionId(String sessionId) {
        SessionId = sessionId;
    }

    public String getRoute() {
        return Route;
    }

    public void setRoute(String route) {
        Route = route;
    }

    public String getDepartTime1() {
        return DepartTime1;
    }

    public void setDepartTime1(String departTime1) {
        DepartTime1 = departTime1;
    }

    public String getAvailableSeat() {
        return AvailableSeat;
    }

    public void setAvailableSeat(String availableSeat) {
        AvailableSeat = availableSeat;
    }

    public String getStop1() {
        return Stop1;
    }

    public void setStop1(String stop1) {
        Stop1 = stop1;
    }

}
