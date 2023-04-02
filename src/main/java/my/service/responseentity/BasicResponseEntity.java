package my.service.responseentity;

public class BasicResponseEntity {

    public BasicResponseEntity(String message) {
        this.message = message;
    }
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
