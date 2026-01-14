package hexlet.code.exception;

public class TaskStatusNotFoundException extends RuntimeException {
    public TaskStatusNotFoundException(Long id) {
        super("TaskStatus with id " + id + " not found");
    }

    public TaskStatusNotFoundException(String slug) {
        super("TaskStatus with slug " + slug + " not found");
    }
}
