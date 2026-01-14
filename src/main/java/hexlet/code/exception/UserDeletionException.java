package hexlet.code.exception;

public class UserDeletionException extends RuntimeException {
    public UserDeletionException(Long userId) {
        super("Cannot delete user with id " + userId + " because they have assigned tasks");
    }
}
