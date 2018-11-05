package sample.exceptions;

public class NotValidPasswordException extends Exception {

    @Override
    public String getMessage() {
        return "Usage error: enter the valid password";
    }
}
