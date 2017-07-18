package akqa.pojo.input;

public class Duration extends InputRecord {

    private String date;
    private String time;
    private String duration;

    public Duration(String date, String time, String duration) {
        this.date = date;
        this.time = time;
        this.duration = duration;

    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDuration() {
        return duration;
    }
}
