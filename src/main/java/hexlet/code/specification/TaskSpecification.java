package hexlet.code.specification;

import hexlet.code.model.Task;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.JoinType;

public final class TaskSpecification {

    private TaskSpecification() {
    }

    public static Specification<Task> withTitleCont(String titleCont) {
        return (root, query, cb) -> {
            if (titleCont == null) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("title")), "%" + titleCont.toLowerCase() + "%");
        };
    }

    public static Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, cb) -> {
            if (assigneeId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("assignee").get("id"), assigneeId);
        };
    }

    public static Specification<Task> withStatus(String status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("taskStatus").get("slug"), status);
        };
    }

    public static Specification<Task> withLabelId(Long labelId) {
        return (root, query, cb) -> {
            if (labelId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.join("labels", JoinType.LEFT).get("id"), labelId);
        };
    }
}
