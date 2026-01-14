package hexlet.code.util;

import hexlet.code.model.User;
import java.util.Random;

public final class ModelGenerator {

    private ModelGenerator() {
    }

    private static final Random RANDOM = new Random();
    private static final String[] FIRST_NAMES = {"John", "Jane", "Alex", "Emily", "Michael", "Sarah"};
    private static final String[] LAST_NAMES = {"Doe", "Smith", "Johnson", "Williams", "Brown", "Jones"};

    public static User getUser() {
        User user = new User();
        user.setEmail(generateRandomEmail());
        user.setFirstName(FIRST_NAMES[RANDOM.nextInt(FIRST_NAMES.length)]);
        user.setLastName(LAST_NAMES[RANDOM.nextInt(LAST_NAMES.length)]);
        user.setPassword(TestUtils.TEST_USER_PASSWORD);
        return user;
    }

    private static String generateRandomEmail() {
        String[] domains = {"gmail.com", "yahoo.com", "hotmail.com", "example.com"};
        String name = "user" + RANDOM.nextInt(1000);
        return name + "@" + domains[RANDOM.nextInt(domains.length)];
    }
}
