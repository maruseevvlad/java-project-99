package hexlet.code.exception;

public class TaskStatusDeletionException extends RuntimeException {
    public TaskStatusDeletionException(Long id) {
        super("Cannot delete task status with id " + id + " because it has associated tasks");
    }

    public TaskStatusDeletionException(Long id, Throwable cause) {
        super("Cannot delete task status with id " + id + " because it has associated tasks", cause);
    }
}
