package profect.eatcloud.domain.Manager.Exception;

public class ManagerNotFoundException extends RuntimeException {
    private final String managerId;

    public ManagerNotFoundException(String managerId) {
        super(String.format("Manager not found with ID: %s", managerId));
        this.managerId = managerId;
    }

    public String getManagerId() { return managerId; }
}