package sigtuna.discord.exception;

public class CooldownException extends Exception {

    private static final long serialVersionUID = 1L;

    public CooldownException(String message) {
        super(message);
    }

    public CooldownException() {

    }
}
