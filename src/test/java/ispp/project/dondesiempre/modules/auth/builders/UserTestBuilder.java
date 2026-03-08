package ispp.project.dondesiempre.modules.auth.builders;

import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.repositories.UserRepository;
import ispp.project.dondesiempre.mockEntities.RandomBasicDataGenerator;
import lombok.Builder;

/**
 * Test Builder for {@link User}.
 * Provides defaults and allows overriding fields for tests.
 */
@Builder
public class UserTestBuilder {

    @Builder.Default
    private String email = RandomBasicDataGenerator.generateRandomEmail("user");

    @Builder.Default
    private String password = RandomBasicDataGenerator.generateRandomName("pass");

    /**
     * Builds an in-memory {@link User}.
     */
    public User entity() {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }

    /**
     * Persists the user using the provided repository.
     *
     * @param userRepository repository to save the user
     * @return persisted {@link User}
     */
    public User persist(UserRepository userRepository) {
        return userRepository.save(this.entity());
    }
}