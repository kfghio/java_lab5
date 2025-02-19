package itmo.dev.cat_microservice.exceptions;

public class OwnerNotFoundException extends RuntimeException{

    public OwnerNotFoundException(String message) {
        super(message);
    }
}
