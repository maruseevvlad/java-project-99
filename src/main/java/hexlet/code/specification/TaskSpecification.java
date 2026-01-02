package hexlet.code.specification;

import hexlet.code.model.Task;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecification {
    public static Specification<Task> titleContains(String titleCont) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("title")),
                "%" + titleCont.toLowerCase() + "%"
        );
    }

    public static Specification<Task> hasAssignee(Long assigneeId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
                root.get("assignee").get("id"),
                assigneeId
        );
    }

    public static Specification<Task> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
                root.join("taskStatus").get("slug"),
                status
        );
    }

    public static Specification<Task> hasLabel(Long labelId) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return criteriaBuilder.equal(root.join("labels").get("id"), labelId);
        };
    }
}
