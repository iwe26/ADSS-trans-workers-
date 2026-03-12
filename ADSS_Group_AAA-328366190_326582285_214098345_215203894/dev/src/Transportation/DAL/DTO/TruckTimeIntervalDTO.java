package Transportation.DAL.DTO;

import java.time.LocalDateTime;
import java.util.Objects;

public class TruckTimeIntervalDTO {
    private final String truckLicense;
    private final LocalDateTime datetime1;
    private final LocalDateTime datetime2;

    public TruckTimeIntervalDTO(String truckLicense, LocalDateTime datetime1, LocalDateTime datetime2) {
        this.truckLicense = truckLicense;
        this.datetime1 = datetime1;
        this.datetime2 = datetime2;
    }

    public String getTruckLicense() { return truckLicense; }
    public LocalDateTime getDatetime1() { return datetime1; }
    public LocalDateTime getDatetime2() { return datetime2; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TruckTimeIntervalDTO)) return false;
        TruckTimeIntervalDTO that = (TruckTimeIntervalDTO) o;
        return Objects.equals(truckLicense, that.truckLicense) &&
                Objects.equals(datetime1, that.datetime1) &&
                Objects.equals(datetime2, that.datetime2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(truckLicense, datetime1, datetime2);
    }
}