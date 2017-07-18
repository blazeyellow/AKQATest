package akqa;

import akqa.exceptions.BookingListException;
import akqa.pojo.output.BookingList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class BookingListTest {

    public String input =
            "0900 1730\n" +
                    "2011-03-17 10:17:06 EMP001\n" +
                    "2011-03-21 09:00 2\n" +
                    "2011-03-16 12:34:56 EMP002\n" +
                    "2011-03-21 09:00 2\n" +
                    "2011-03-16 09:28:23 EMP003\n" +
                    "2011-03-22 14:00 2\n" +
                    "2011-03-17 11:23:45 EMP004\n" +
                    "2011-03-22 16:00 1\n" +
                    "2011-03-15 17:29:12 EMP005\n" +
                    "2011-03-21 16:00 3";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testProcess_create_ok() throws Exception {
        BookingList bookingList = BookingList.process(input.getBytes());
        assertNotNull(bookingList);
        assertTrue(bookingList.getBookingList().size() > 0);

    }

    public String input1 =
            "0900 1730\n" +
            "2011-03-17 10:17:06 EMP001\n" +
            "2011-03-21 09:00 2";

    @Test
    public void testProcess_bookinglist_created_one_booking_ok() throws Exception {
        BookingList bookingList = BookingList.process(input1.getBytes());
        assertTrue(bookingList.getBookingList().size() == 1);

    }

    public String input2 =
            "9900 1730\n" +
            "2011-03-17 10:17:06 EMP001\n" +
            "2011-03-21 09:00 2";

    @Test
    public void testProcess_invalid_header_starttime_hrs_garbled() throws Exception {
        expectedException.expect(BookingListException.class);
        expectedException.expectMessage("Hours of business (start) hours are out of range");

        BookingList.process(input2.getBytes());

    }

    public String input3 =
            "0970 1730\n" +
            "2011-03-17 10:17:06 EMP001\n" +
            "2011-03-21 09:00 2";

    @Test
    public void testProcess_invalid_header_starttime_mins_garbled() throws Exception {
        expectedException.expect(BookingListException.class);
        expectedException.expectMessage("Hours of business (start) minutes are out of range");

        BookingList.process(input3.getBytes());

    }

    public String input4 =
            "0900 2430\n" +
            "2011-03-17 10:17:06 EMP001\n" +
            "2011-03-21 09:00 2";

    @Test
    public void testProcess_invalid_header_endtime_hrs_garbled() throws Exception {
        expectedException.expect(BookingListException.class);
        expectedException.expectMessage("Hours of business (end) hours are out of range");

        BookingList.process(input4.getBytes());

    }

    public String input5 =
            "0900 1761\n" +
            "2011-03-17 10:17:06 EMP001\n" +
            "2011-03-21 09:00 2";

    @Test
    public void testProcess_invalid_header_endtime_mins_garbled() throws Exception {
        expectedException.expect(BookingListException.class);
        expectedException.expectMessage("Hours of business (end) minutes are out of range");

        BookingList.process(input5.getBytes());

    }

    String expectedOut = "2011-03-21\n" +
                         "09:00 11:00 EMP002\n" +
                         "2011-03-22\n" +
                         "14:00 16:00 EMP003\n" +
                         "16:00 17:00 EMP004\n";

    @Test
    public void testGenerate_success() throws Exception {
        BookingList bookingList = BookingList.process(input.getBytes());
        byte [] actualOut = bookingList.generate();

        System.out.println(new String(actualOut));

        assertThat(expectedOut.getBytes(), equalTo(actualOut));

    }

    public String input6 =
                    "0900 1730\n" +
                    "2011-03-17 10:00:00 EMP001\n" +
                    "2011-03-21 09:00 2\n" +
                    "2011-03-16 10:00:00 EMP002\n" +
                    "2011-03-21 09:00 2\n" +
                    "2011-03-15 10:00:00 EMP003\n" +
                    "2011-03-21 09:00 2";

    String expectedOut6 =
            "2011-03-21\n" +
            "09:00 11:00 EMP003\n";

    @Test
    public void testGenerate_success_newer_bookings_gazzumped() throws Exception {
        BookingList bookingList = BookingList.process(input6.getBytes());
        byte [] actualOut = bookingList.generate();

        System.out.println(new String(actualOut));

        assertThat(expectedOut6.getBytes(), equalTo(actualOut));

    }

    // EMP002 booking clashes with EMP001 and EMP002 but was booked LAST and should therefore should NOT exist in output
    public String input7 =
            "0900 1730\n" +
            "2011-03-17 10:00:00 EMP001\n" +
            "2011-03-21 09:00 3\n" +
            "2011-03-17 11:00:00 EMP003\n" +
            "2011-03-21 13:00 3\n" +
            "2011-03-17 12:00:00 EMP002\n" +
            "2011-03-21 11:00 3\n";

    String expectedOut7 =
            "2011-03-21\n" +
            "09:00 12:00 EMP001\n" +
            "13:00 16:00 EMP003\n";

    @Test
    public void testGenerate_success_older_booking_rules() throws Exception {
        BookingList bookingList = BookingList.process(input7.getBytes());
        byte [] actualOut = bookingList.generate();

        System.out.println(new String(actualOut));

        assertThat(expectedOut7.getBytes(), equalTo(actualOut));

    }

    // EMP002 booking clashes with EMP001 and EMP002 but was booked FIRST and should therefore only exist in output
    public String input8 =
            "0900 1730\n" +
                    "2011-03-17 10:00:00 EMP001\n" +
                    "2011-03-21 09:00 3\n" +
                    "2011-03-17 11:00:00 EMP003\n" +
                    "2011-03-21 13:00 3\n" +
                    "2011-03-17 09:00:00 EMP002\n" +
                    "2011-03-21 11:00 3\n";

    String expectedOut8 =
            "2011-03-21\n" +
                    "11:00 14:00 EMP002\n";

    @Test
    public void testGenerate_success_newer_booking_rules() throws Exception {
        BookingList bookingList = BookingList.process(input8.getBytes());
        byte [] actualOut = bookingList.generate();

        System.out.println(new String(actualOut));

        assertThat(expectedOut8.getBytes(), equalTo(actualOut));

    }
 }
