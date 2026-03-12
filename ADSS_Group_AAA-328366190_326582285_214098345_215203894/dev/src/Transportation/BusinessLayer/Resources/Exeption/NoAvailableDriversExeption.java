package Transportation.BusinessLayer.Resources.Exeption;

public class NoAvailableDriversExeption extends RuntimeException {
    public NoAvailableDriversExeption(String message) {
        super(message);
    }
}
