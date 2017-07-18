package akqa.pojo.output;

import akqa.exceptions.BookingListException;
import akqa.pojo.input.InputRecord;
import akqa.pojo.validation.OpeningHours;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BookingList {

    protected static Logger logger = Logger.getLogger(BookingList.class);

    private List<Booking> bookingList = new ArrayList<Booking>();

    private BookingList(BufferedReader br) throws BookingListException {
        bookingList = readBookingFile(br);

    }

    public static BookingList process(byte[] data) throws BookingListException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)));
        BookingList bookingList = null;
        bookingList = new BookingList(br);

        return bookingList;

    }

    private List<Booking> readBookingFile(BufferedReader br) throws BookingListException {
        try {
            String bookingLine;

            OpeningHours openingHours = null;
            boolean headerRead = false;

            List<InputRecord> inputRecords = new ArrayList<InputRecord>();
            while ((bookingLine = br.readLine()) != null) {
                if (!headerRead) {
                    // we have the header with office hours
                    openingHours = createOpeningHours(bookingLine);
                    headerRead = true;
                    continue;

                }

                // TODO would normally validate to ensure consecutive occurrences (zero or many) of 'employee entry' then 'start/duration'
                // in input data. we'll assume lines are in this order for this proof of concept

                // backup two records.
                if (inputRecords.size()<2) {
                    inputRecords.add(InputRecord.createInputRecord(bookingLine));

                    if (inputRecords.size()==2) {
                        Booking booking = new Booking(inputRecords);

                        if (booking.isBookingWithinHours(openingHours) && addBooking(booking)) {
                            bookingList.add(new Booking(inputRecords));

                        } else {
                            logger.warn("Booking "+booking.toString()+" clashes with another booking or is out of hours and therefore cannot be added");

                        }

                        inputRecords.clear();
                    }
                }
            }

        } catch (IOException e) {
            throw new BookingListException("An IO error occurred reading input data");

        } catch (BookingListException e) {
            throw e;

        } finally {
            try {
                if (br != null) {
                    br.close();

                }

            } catch (IOException ex) {
                throw new BookingListException("An IO error occurred closing input data");

            }
        }

        return bookingList;

    }

    public byte [] generate() {

        StringBuffer sb = new StringBuffer();

        Collections.sort(bookingList);

        String currentdateFormat="";
        for (Booking booking : bookingList) {

            if (!currentdateFormat.equals(booking.getDateFormatted())) {
                sb.append(booking.getDateFormatted())
                  .append('\n');
                currentdateFormat = booking.getDateFormatted();

            }

            sb.append(booking.getStartTimeFormatted())
                    .append(" ")
                    .append(booking.getEndTimeFormatted())
                    .append(" ")
                    .append(booking.getEmpId())
                    .append('\n');

        }

        return sb.toString().getBytes();

    }

    /**
     *
     * we need to identify clashes and if our booking at hand does clash we need to determine which booking was made
     * first. we may have an instance where the toBeBooked clashes with the tail end of one booking and the head of
     * another. we can only book toBeBooked if and only if toBeBooked was booked BEFORE the other bookings already made
     *
     * @param toBeBooked
     * @return
     */
    private boolean addBooking(Booking toBeBooked) {

        Set<Booking> toBeDeleted = new HashSet<Booking>();

        Iterator it = bookingList.iterator();
        while(it.hasNext()) {
            Booking booking = (Booking)it.next();
            if ((toBeBooked.getStartTime().equals(booking.getStartTime())
                    || toBeBooked.getStartTime().after(booking.getStartTime()))
                    && toBeBooked.getStartTime().before(booking.getEndTime())) {

                if (toBeBooked.getBookedOn().before(booking.getBookedOn())) {
                    toBeDeleted.add(booking);

                } else {
                    return false;

                }
            }

            if (toBeBooked.getEndTime().after(booking.getStartTime())
                    && (toBeBooked.getEndTime().before(booking.getEndTime())
                    || toBeBooked.getEndTime().equals(booking.getEndTime()))) {

                if (toBeBooked.getBookedOn().before(booking.getBookedOn())) {
                    toBeDeleted.add(booking);

                } else {
                    return false;

                }
            }

            if (toBeBooked.getStartTime().before(booking.getStartTime())
                    && toBeBooked.getEndTime().after(booking.getStartTime())) {

                if (toBeBooked.getBookedOn().before(booking.getBookedOn())) {
                    toBeDeleted.add(booking);

                } else {
                    return false;

                }
            }

            if (toBeBooked.getEndTime().after(booking.getEndTime()) && toBeBooked.getStartTime().before(booking.getEndTime())) {
                if (toBeBooked.getBookedOn().before(booking.getBookedOn())) {
                    toBeDeleted.add(booking);

                } else {
                    return false;

                }
            }
        }

        // our booking was booked before ALL clashes and therefore rules them all. go add it! hurrah!

        removeNewerClashes(toBeDeleted);

        return true;

    }

    private void removeNewerClashes(Set<Booking> toBeDeleted) {
        for (Booking booking : toBeDeleted) {
            logger.warn("Booking "+booking.toString()+" clashes with another booking or is out of hours and therefore cannot be added");
            bookingList.remove(booking);

        }

        toBeDeleted.clear();

    }

    private OpeningHours createOpeningHours(String bookingLine) throws BookingListException {
        return new OpeningHours(getStartTime(bookingLine), getEndTime(bookingLine));

    }

    private String getStartTime(String bookingLine) {
        return bookingLine.split(" ")[0];
    }


    private String getEndTime(String bookingLine) {
        return bookingLine.split(" ")[1];
    }

    public List<Booking> getBookingList() {
        return bookingList;
    }
}
