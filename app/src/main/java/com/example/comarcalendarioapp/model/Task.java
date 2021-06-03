package com.example.comarcalendarioapp.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.UUID;

public class Task {
    private String description;
    private String id;
    private LocalDate startDate;
    private int startYear;
    private int startMonth;
    private int startDay;
    private int daysLimit;
    private LocalDate limitDate;
    private String place;
    private ArrayList<LocalDate> calendarList;

    public Task(){}

    public Task(String description, int startYear, int startMonth, int startDay , int daysLimit, String place, ArrayList<LocalDate> calendarList){
        this.description=description.substring(0,1).toUpperCase()+description.substring(1);
        this.startYear=startYear;
        this.startMonth=startMonth;
        this.startDay=startDay;
        this.startDate=LocalDate.of(this.startYear,this.startMonth,this.startDay);
        this.daysLimit=daysLimit;
        this.place=place;
        this.calendarList=calendarList;
        setLimitDate(this.startDate, this.daysLimit, this.place);
        setId();

    }

    public String getDescription() {
        return description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public int getDaysLimit() {
        return daysLimit;
    }

    public LocalDate getLimitDate(){ return limitDate;}

    public String getPlace() { return place;}

    public String getId() { return id;}

    public void setLimitDate(LocalDate startDate, int daysLimit, String place) {
        LocalDate date = startDate;
        int dayCount = 0;
        do {
            Month mes = date.getMonth();
            DayOfWeek diaSemana = date.getDayOfWeek();
            if (mes == Month.AUGUST) {
                date = date.plusDays(1);
            } else {
                if (diaSemana == DayOfWeek.SATURDAY) {
                    date = date.plusDays(1);
                } else {
                    if (diaSemana == DayOfWeek.SUNDAY) {
                        date = date.plusDays(1);
                    } else {
                        if(calendarList.contains(date)) {
                            date = date.plusDays(1);
                        } else {
                            date = date.plusDays(1);
                            dayCount++;
                        }
                    }
                }
            }
        } while (dayCount < daysLimit);
        this.limitDate=date;
    }

    public void setId(){
        String newId= UUID.randomUUID().toString();
        this.id=newId;
    }

}
