package com.example.emobile.repository;

import com.example.emobile.model.User;
import com.example.emobile.util.TestDataBuilderUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldFindByEmail() {
        var savedUser = saveUser(TestDataBuilderUtil.validUser());

        assertThat(userRepository.findByEmail(savedUser.getEmail()))
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getEmail()).isEqualTo(savedUser.getEmail());
                    assertThat(user.getRole()).isEqualTo(savedUser.getRole());
                });
    }

    @Test
    public void shouldReturnTrueIfEmailExists() {
        var savedUser = saveUser(TestDataBuilderUtil.validUser());

        boolean exists = userRepository.existsByEmail(savedUser.getEmail());

        assertThat(exists).isTrue();
    }

    @Test
    public void shouldReturnFalseIfEmailNotExists() {
        boolean exists = userRepository.existsByEmail("notfound@example.com");

        assertThat(exists).isFalse();
    }

    private User saveUser(User testUser) {
        return userRepository.save(testUser);
    }
}