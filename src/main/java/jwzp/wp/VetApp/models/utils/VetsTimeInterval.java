package jwzp.wp.VetApp.models.utils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class VetsTimeInterval {
    public final LocalDateTime begin;
    public final LocalDateTime end;
    public final List<Integer> vetIds;

    public VetsTimeInterval(LocalDateTime begin, LocalDateTime end, List<Integer> vetIds) {
        this.begin = begin;
        this.end = end;
        this.vetIds = vetIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VetsTimeInterval that = (VetsTimeInterval) o;
        return Objects.equals(begin, that.begin) && Objects.equals(end, that.end) && Objects.equals(vetIds, that.vetIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(begin, end, vetIds);
    }
}
