package akqa.pojo.input;

import java.util.Date;

public class RequestedBy extends InputRecord {

    private Date bookedOn;
    private String empId;

    public RequestedBy(String empId, Date bookedOn) {
        this.empId = empId;
        this.bookedOn = bookedOn;

    }
    public Date getBookedOn() {
        return bookedOn;
    }

    public String getEmpId() {
        return empId;
    }
}
