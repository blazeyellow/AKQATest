package akqa.pojo.output;

import akqa.pojo.input.Duration;
import akqa.pojo.input.InputRecord;
import akqa.pojo.input.RequestedBy;
import akqa.pojo.validation.OpeningHours;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Booking implements Comparable<Booking>{

    protected static Logger logger = Logger.getLogger(Booking.class);

    private int id;
    private String empId;
    private Date bookedOn;
    private Date date;
    private Date startTime;
    private Date endTime;

    private Date earliestStartTime;
    private Date latestEndTime;

    public Booking(List<InputRecord> inputRecords) {

        for (InputRecord inputRecord : inputRecords) {
            if (inputRecord instanceof RequestedBy) {
                RequestedBy requestedBy = (RequestedBy) inputRecord;
                empId = requestedBy.getEmpId();
                bookedOn = requestedBy.getBookedOn();

            }

            if (inputRecord instanceof Duration) {
                Duration duration = (Duration)inputRecord;
                setDate(duration.getDate());
                setStartTime(duration.getDate(), duration.getTime());
                setEndTime(Integer.parseInt(duration.getDuration()));

            }
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public Date getDate() {
        return date;
    }

    public String getDateFormatted() {
        DateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
        return formatter.format(date);
    }

    public void setDate(String date) {
        DateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
        try {
            this.date = formatter.parse(date);

        } catch (ParseException e) {
            logger.warn("Date is invalid");

        }
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public String getStartTimeFormatted() {
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(startTime);

    }

    public void setStartTime(String date, String time) {
        DateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
        try {
            startTime = formatter.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(startTime);
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.split(":")[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(time.split(":")[1]));
            startTime = cal.getTime();

        } catch (ParseException e) {
            logger.warn("Start time is invalid");

        }
    }

    public String getEndTimeFormatted() {
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(endTime);


    }

    public void setEndTime(Integer duration) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getStartTime()); // TODO assuming startDate valid
        cal.add(Calendar.HOUR_OF_DAY, duration);
        endTime = cal.getTime();

    }

    public void setEarliestAndLatestMeetingTimes(OpeningHours openingHours) {
        Calendar cal = Calendar.getInstance();

        Integer startHours = getHours(openingHours.getStart());
        Integer startMinutes = getMinutes(openingHours.getStart());
        Integer endHours = getHours(openingHours.getEnd());
        Integer endMinutes = getMinutes(openingHours.getEnd());

        cal.setTime(startTime);
        cal.set(Calendar.HOUR_OF_DAY, startHours);
        cal.set(Calendar.MINUTE, startMinutes);
        earliestStartTime = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY, endHours);
        cal.set(Calendar.MINUTE, endMinutes);
        latestEndTime = cal.getTime();

    }

    private Integer getMinutes(String time) {
        return Integer.parseInt(time.substring(2));

    }

    private Integer getHours(String time) {
        return Integer.parseInt(time.substring(0, 2));

    }


    public boolean isBookingWithinHours(OpeningHours openingHours) {
        setEarliestAndLatestMeetingTimes(openingHours);

        if (startTime.before(earliestStartTime) || endTime.after(latestEndTime)) {
            return false;

        }

        return true;

    }

    public Date getBookedOn() {
        return bookedOn;
    }

    public void setBookedOn(Date bookedOn) {
        this.bookedOn = bookedOn;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[empId=").append(empId)
                .append(", ")
                .append("date=").append(getDateFormatted())
                .append(", ")
                .append("startTime=")
                .append(getStartTimeFormatted())
                .append(", ")
                .append(getEndTimeFormatted())
                .append("]");

        return sb.toString();

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(date).
                append(startTime).
                append(endTime).
                toHashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;

        }
        if (o == this) {
            return true;

        }

        if (!(o instanceof Booking)) {
            return false;

        }

        Booking rhs = (Booking) o;
        return new EqualsBuilder().
                append(date, rhs.date).
                append(startTime, rhs.startTime).
                append(endTime, rhs.endTime).
                isEquals();
    }

    @Override
    public int compareTo(Booking booking) {
        if (this.startTime.before(booking.getStartTime())) {
            return -1;

        }

        if (this.startTime.after(booking.getStartTime())) {
            return 1;

        }

        return 0;

    }
}
