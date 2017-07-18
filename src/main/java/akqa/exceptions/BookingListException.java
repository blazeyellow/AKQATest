package akqa.exceptions;

public class BookingListException extends Exception {
    public BookingListException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public BookingListException(String s) {
        super(s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public BookingListException(String s, Throwable throwable) {
        super(s, throwable);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public BookingListException(Throwable throwable) {
        super(throwable);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
