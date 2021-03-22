package jwzp.wp.VetApp.models.values;

import java.time.LocalTime;

public class OpeningHours {
    public Days monday = Days.Monday;
    public Days tuesday = Days.Tuesday;
    public Days wednesday = Days.Wednesday;
    public Days thursday = Days.Thursday;
    public Days friday = Days.Friday;
    public Days saturday = Days.Saturday;
    public Days sunday = Days.Sunday;

    enum Days {
        Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday;
        public LocalTime start;
        public LocalTime end;
    }
}

