package Transportation.BusinessLayer.Resources;

public enum Role
{
    ADMINISTRATOR,
    DRIVER,
    TRANSPORTATION_MANAGER;

    public static Role fromString(String role) {
    return Role.valueOf(role.toUpperCase());
}
}
