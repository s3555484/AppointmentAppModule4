// AppointmentApp.java
// T. Olugbemi
// 02/13/23
// creates an appointment for a contact

package edu.fscj.cop2805c.calendar;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

enum REMINDER_PREFERENCE { NONE, EMAIL, PHONE }

// main application class
public final class AppointmentApp{

    private ArrayList<Appointment> appointments = new ArrayList<>();
    private Random rand = new Random();
    private int numAppointments = 0;

    private Appointment createRandomAppointment(Contact c) {
        ZonedDateTime apptTime, reminder;
        int plusVal = rand.nextInt() % 12 + 1;
        // create a future appointment using random month value
        apptTime = ZonedDateTime.now().plusMonths(plusVal);

        // create the appt reminder for the appointment time minus random (<24) hours
        // use absolute value in case random is negative to prevent reminders > appt
        int minusVal = Math.abs(rand.nextInt()) % 24 + 1;
        reminder = apptTime.minusHours(minusVal);
        // create an appointment using the contact and appt time
        int apptNum = appointments.size() + 1;
        Appointment appt = new Appointment("Test Appointment " + ++numAppointments,
                "This is test appointment " + numAppointments,
                c, apptTime);
        appt.setReminder(reminder);
        return appt;
    }

    private void addAppointments(Appointment... appts) {
        for (Appointment a : appts)
            appointments.add(a);
    }
    // unit test
    public static void main(String[] args) {
        ZonedDateTime apptTime, reminder;

        AppointmentApp apptApp = new AppointmentApp();

        // start with a contact
        Contact c = new Contact("Smith", "John", "JohnSmith@email.com",
                "904-555-1212", REMINDER_PREFERENCE.EMAIL,
                ZoneId.of("America/New_York"));

        Appointment a1 = apptApp.createRandomAppointment(c);
        Appointment a2 = apptApp.createRandomAppointment(c);
        Appointment a3 = apptApp.createRandomAppointment(c);

        apptApp.addAppointments(a1, a2, a3);

        // print the information
        for (Appointment a : apptApp.appointments) {
            System.out.println(a);
            a.sendReminder(a.buildReminder());
        }
    }
}

// class which represents appointments
final class Appointment implements CalendarReminder {

    private String title;
    private String description;
    private Contact contact;
    private ZonedDateTime apptTime;
    private ZonedDateTime reminder;

    public Appointment(String title, String description, Contact contact,
                       ZonedDateTime apptTime) {
        this.title = title;
        this.description = description;
        this.contact = contact;
        this.apptTime = apptTime;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Contact getContact() {
        return contact;
    }

    public ZonedDateTime getApptTime() {
        return apptTime;
    }

    public ZonedDateTime getReminder() {
        return reminder;
    }

    public void setReminder(ZonedDateTime reminder) {
        this.reminder = reminder;
    }

    @Override
    public String toString() {
        String s = "Appt:\n" +
                "\tTitle: " + this.getTitle() + "\n" +
                "\tDesc: " + this.getDescription() + "\n" +
                "\tContact: " + this.getContact() + "\n" +
                "\tAppt Date/Time: " + this.getApptTime() + "\n" +
                "\tReminder: " + this.getReminder() + "\n";
        return s;
    }

    /**
     * @return
     */
    @Override
    public String buildReminder() {
        int longestLength = 0;
        String name = this.getContact().getName().toString();
        REMINDER_PREFERENCE preference = this.getContact().getRemindPref();
        String remindPref = switch (preference){
            case PHONE -> "SMS";
            case EMAIL-> "e-mail";
            default -> null;
        };
        String phoneOrEmail = switch (preference){
            case PHONE -> this.getContact().getPhone().toString();
            case EMAIL -> this.getContact().getEmail().toString();
            default -> null;
        };
        String heading = "Sending the following "+ remindPref + " message to " +
                          name.substring(name.indexOf(' ') + 1, name.length()) + " " +
                          name.substring(0, name.indexOf(',')) + " at " + phoneOrEmail;
        String purpose = "This is a reminder that you have an upcoming appointment.";
        String title = "Title: " + this.getTitle();
        String description = "Desc: " + this.getDescription();
        String date = this.apptTime.getDayOfMonth() + " " + this.apptTime.getMonth().toString() + ", " + this.apptTime.getYear();
        String time = this.apptTime.getHour() + ":" + this.apptTime.getMinute() + " " + this.apptTime.getZone();
        StringBuilder headerLine = new StringBuilder();
        StringBuilder footerLine = new StringBuilder();
        String message = "";

        //get longest line length
        if (name.length() + "Hello, ".length() > longestLength){
            longestLength = name.length();
        }
        if (purpose.length() > longestLength){
            longestLength = purpose.length();
        }
        if (title.length() > longestLength){
            longestLength = title.length();
        }
        if (description.length() > longestLength){
            longestLength = description.length();
        }
        if (date.length() > longestLength){
            longestLength = date.length();
        }
        if (time.length() > longestLength){
            longestLength = time.length();
        }
        // build the header and footer lines
        for (int i = 0; i < longestLength + 4; i++){
            headerLine.append("+");
            footerLine.append("+");
        }

        name = "Hello, " + name.substring(name.indexOf(' ') + 1, name.length()) + " " + name.substring(0, name.indexOf(',')) + "!";

        message =   heading + "\n\n" +
                    headerLine + "\n" +
                    "+ " + rightPadding(name, longestLength) + " +\n" +
                    "+ " + rightPadding(purpose, longestLength) + " +\n" +
                    "+ " + rightPadding(" ", longestLength) + " +\n" +
                    "+ " + rightPadding(title, longestLength) + " +\n" +
                    "+ " + rightPadding(description, longestLength) + " +\n" +
                    "+ " + rightPadding(date, longestLength) + " +\n" +
                    "+ " + rightPadding(time, longestLength) + " +\n" +
                    footerLine + "\n";


        return message;
    }

    private StringBuilder rightPadding(String S, int finaLength) {

        StringBuilder padded = new StringBuilder(S);
        if (S.length() < finaLength){
            for (int x = 0; x < finaLength - S.length(); x++){
                padded.append(" ");
            }
        }
        return padded;
    }


    /**
     * @param reminder
     */
    @Override
    public void sendReminder(String reminder) {
        System.out.println(reminder);
    }
}


// class which represents the appointment contact
class Contact {
    private StringBuilder name; // last, first
    private String email;
    private String phone;
    private REMINDER_PREFERENCE remindPref;
    private ZoneId zoneId;

    public Contact(String lName, String fName,
                   String email, String phone,
                   REMINDER_PREFERENCE remindPref,
                   ZoneId zoneId) {
        name = new StringBuilder();
        name.append(lName).append(", ").append(fName);
        this.email = email;
        this.phone = phone;
        this.remindPref = remindPref;
        this.zoneId = zoneId;
    }

    public StringBuilder getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public REMINDER_PREFERENCE getRemindPref() {
        return remindPref;
    }

    public void setRemindPref(REMINDER_PREFERENCE remindPref) {
        this.remindPref = remindPref;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setTimeZoneOffset(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public String toString() {
        String s = this.getEmail() + "," +
                   "(" + this.getName()  + ")" +
                   this.getPhone() + "," +
                   this.getRemindPref() +"," +
                   this.getZoneId();
        return s;
    }
}
