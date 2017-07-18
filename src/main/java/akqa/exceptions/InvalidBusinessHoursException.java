package akqa.exceptions;

public class InvalidBusinessHoursException extends Exception {

    public InvalidBusinessHoursException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public InvalidBusinessHoursException(String s) {
        super(s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public InvalidBusinessHoursException(String s, Throwable throwable) {
        super(s, throwable);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public InvalidBusinessHoursException(Throwable throwable) {
        super(throwable);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
