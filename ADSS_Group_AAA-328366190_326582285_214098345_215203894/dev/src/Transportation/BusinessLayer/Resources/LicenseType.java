package Transportation.BusinessLayer.Resources;

public enum LicenseType {
    B,
    C,
    D,
    E;

    public static LicenseType fromInt(int ordinal) {
        LicenseType[] values = LicenseType.values();
        if (ordinal < 0 || ordinal >= values.length) {
            throw new IllegalArgumentException("Invalid license ordinal: " + ordinal);
        }
        return values[ordinal];
    }
}
