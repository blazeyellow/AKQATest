package akqa.pojo.validation;


import akqa.exceptions.BookingListException;

public class OpeningHours {

    private String start;
    private String end;

    public OpeningHours(String start, String end) throws BookingListException {

        // TODO validate start and end times (valid 24hr times, start.value < end.value)

        this.start = checkStart(start);
        this.end = checkEnd(end);

    }

    private String checkStart(String start) throws BookingListException {
        Integer hours;
        Integer minutes;
        try {
            hours = Integer.parseInt(start.substring(0, 2));
            minutes = Integer.parseInt(start.substring(2,4));

        } catch (NumberFormatException e) {
            throw new BookingListException("Business start hours are not valid");

        }

        if (hours<0 || hours>23) {
            throw new BookingListException("Hours of business (start) hours are out of range");


        }

        if (minutes<0 || minutes>59) {
            throw new BookingListException("Hours of business (start) minutes are out of range");

        }

        return start; // all good

    }

    private String checkEnd(String end) throws BookingListException {
        Integer hours;
        Integer minutes;
        try {
            hours = Integer.parseInt(end.substring(0, 2));
            minutes = Integer.parseInt(end.substring(2, 4));

        } catch (NumberFormatException e) {
            throw new BookingListException("Business end hours are not valid");

        }

        if (hours<0 || hours>23) {
            throw new BookingListException("Hours of business (end) hours are out of range");


        }

        if (minutes<0 || minutes>59) {
            throw new BookingListException("Hours of business (end) minutes are out of range");

        }

        if (Integer.parseInt(start) >= Integer.parseInt(end)) {
            throw new BookingListException("Hours of business are invalid");


        }

        return end; // all good

    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }
}
