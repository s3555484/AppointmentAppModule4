package edu.fscj.cop2805c.calendar;

public sealed interface CalendarReminder permits Appointment{

    //build a reminder in the form of a formatted String
    public String buildReminder();

    //send a reminder using contact's preferred notification method
    public void sendReminder(String reminder);

}
