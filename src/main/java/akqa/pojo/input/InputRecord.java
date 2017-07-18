package akqa.pojo.input;

import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class InputRecord {

    protected static Logger logger = Logger.getLogger(InputRecord.class);

    public static InputRecord createInputRecord(String inputRecord) {
        String [] inputRecordParts = inputRecord.split(" ");

        InputRecord instance;
        if (inputRecordParts.length==3) {
            try {
                Integer.parseInt(inputRecordParts[2]);
                instance = new Duration(inputRecordParts[0], inputRecordParts[1], inputRecordParts[2]);

                return instance;

            } catch (NumberFormatException e) {}

            instance = new RequestedBy(inputRecordParts[2], createDate(inputRecordParts[0], inputRecordParts[1]));

            return instance;

        } else {
            return null;

        }

    }

    private static Date createDate(String inputRecordPart, String inputRecordPart1) {
        Date bookedOn = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
        try {
            bookedOn = formatter.parse(inputRecordPart);
            Calendar cal = Calendar.getInstance();
            cal.setTime(bookedOn);
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(inputRecordPart1.split(":")[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(inputRecordPart1.split(":")[1]));
            bookedOn = cal.getTime();

        } catch (ParseException e) {
            logger.warn("Booked-on date is invalid");

        }

        return bookedOn;

    }

}
