package support;

// Stores the result of an attempted action.
public class ActionResult {

    private final boolean success;
    private final String message;

    public ActionResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    // Creates a successful result.
    public static ActionResult success(String message) {
        return new ActionResult(true, message);
    }

    // Creates a failed result.
    public static ActionResult failure(String message) {
        return new ActionResult(false, message);
    }
}
