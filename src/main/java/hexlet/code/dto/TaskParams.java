package hexlet.code.dto;

public record TaskParams(String titleCont,
                         Long assigneeId,
                         String status,
                         Long labelId) { }
