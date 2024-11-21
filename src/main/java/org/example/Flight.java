package org.example;

import java.util.Date;

public class Flight {

    private String companhia;
    private Date departure;
    private Date arrival;
    private boolean isDirect;
    private double price;

    public Flight(String companhia, Date departure, Date arrival, boolean isDirect, double price) {
        this.companhia = companhia;
        this.departure = departure;
        this.arrival = arrival;
        this.isDirect = isDirect;
        this.price = price;
    }

    public Flight(){

    }

    public String getCompanhia() {
        return companhia;
    }

    public void setCompanhia(String companhia) {
        this.companhia = companhia;
    }

    public Date getDeparture() {
        return departure;
    }

    public void setDeparture(Date departure) {
        this.departure = departure;
    }

    public Date getArrival() {
        return arrival;
    }

    public void setArrival(Date arrival) {
        this.arrival = arrival;
    }

    public boolean isDirect() {
        return isDirect;
    }

    public void setDirect(boolean direct) {
        isDirect = direct;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    @Override
    public String toString() {
        return "Flight{" +
                "companhia='" + companhia + '\'' +
                ", departure=" + departure +
                ", arrival=" + arrival +
                ", isDirect=" + isDirect +
                ", price=" + price +
                '}';
    }
}
