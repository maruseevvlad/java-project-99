package hexlet.code.exception;

public class LabelNotFoundException extends RuntimeException {
    public LabelNotFoundException(Long id) {
        super("Label with id " + id + " not found");
    }

    public LabelNotFoundException(String name) {
        super("Label with name " + name + " not found");
    }
}
