package com.example.xmum_bus_app.BusSchedule;

public class Routes {

    private String RouteName, StartTime, Driver, BusNo, BusStatus, BusLatitude, BusLongitude, EndLatitude, EndLongitude;

    public Routes(String routeName, String startTime, String driver, String busNo, String busStatus, String busLatitude, String busLongitude, String endLatitude, String endLongitude) {
        RouteName = routeName;
        StartTime = startTime;
        Driver = driver;

        BusNo = busNo;
        BusStatus = busStatus;
        BusLatitude = busLatitude;
        BusLongitude = busLongitude;
        EndLatitude = endLatitude;
        EndLongitude = endLongitude;
    }

    public Routes(){
    }

    public String getRouteName() {
        return RouteName;
    }

    public void setRouteName(String routeName) {
        RouteName = routeName;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getDriver() {
        return Driver;
    }

    public void setDriver(String driver) {
        Driver = driver;
    }

    public String getBusNo() {
        return BusNo;
    }

    public void setBusNo(String busNo) {
        BusNo = busNo;
    }

    public String getBusStatus() {
        return BusStatus;
    }

    public void setBusStatus(String busStatus) {
        BusStatus = busStatus;
    }

    public String getBusLatitude() {
        return BusLatitude;
    }

    public void setBusLatitude(String busLatitude) {
        BusLatitude = busLatitude;
    }

    public String getBusLongitude() {
        return BusLongitude;
    }

    public void setBusLongitude(String busLongitude) {
        BusLongitude = busLongitude;
    }

    public String getEndLatitude() {
        return EndLatitude;
    }

    public void setEndLatitude(String endLatitude) {
        EndLatitude = endLatitude;
    }

    public String getEndLongitude() {
        return EndLongitude;
    }

    public void setEndLongitude(String endLongitude) {
        EndLongitude = endLongitude;
    }
}
