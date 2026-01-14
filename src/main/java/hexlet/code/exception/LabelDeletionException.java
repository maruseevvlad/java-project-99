package hexlet.code.exception;

public class LabelDeletionException extends RuntimeException {
    public LabelDeletionException(Long id) {
        super("Cannot delete label with id " + id + " because it has associated tasks");
    }
}
