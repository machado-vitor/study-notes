import java.util.Random;

public class RemoteService {
    private final Random random = new Random();
    private int callCount = 0;

    public String callExternalAPI() throws Exception {
        callCount++;

        if (random.nextDouble() < 0.5) {
            throw new Exception("Service temporarily unavailable");
        }

        return "Success after " + callCount + " attempts";
    }

    public String slowOperation() throws InterruptedException {
        Thread.sleep(3000);
        return "Completed slow operation";
    }

    public String reliableOperation() {
        return "Operation successful";
    }

    public void reset() {
        callCount = 0;
    }
}
