package library.usecase.exception;

public class RequestException extends Exception {
    public int status;
    public String message; 

    public RequestException(int status, String message) {
        super();
        this.status = status;
        this.message = message;
    }
    
    private static final long serialVersionUID = 1L;
}