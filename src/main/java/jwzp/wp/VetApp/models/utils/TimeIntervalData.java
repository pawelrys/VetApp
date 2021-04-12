package jwzp.wp.VetApp.models.utils;

import java.time.LocalDateTime;
import java.util.Objects;

public class TimeIntervalData {
    public LocalDateTime begin;
    public LocalDateTime end;

    public TimeIntervalData(LocalDateTime begin, LocalDateTime end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeIntervalData that = (TimeIntervalData) o;
        return Objects.equals(begin, that.begin) && Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(begin, end);
    }
}
