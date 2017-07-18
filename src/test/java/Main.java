import akqa.pojo.output.BookingList;

public class Main {

    public static String input =
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

    public static void main(String[] args) throws Exception {
        BookingList bookingList = BookingList.process(input.getBytes());
        System.out.println(new String(bookingList.generate()));

    }
}
