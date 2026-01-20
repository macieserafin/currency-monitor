package macieserafin.pjwstk.edu.pl.currencymonitorpersistence.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByUsername() {
        // given
        User user = new User("admin", "secret");
        userRepository.save(user);

        // when
        var found = userRepository.findByUsername("admin");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("admin");
        assertThat(found.get().isEnabled()).isTrue();
    }

    @Test
    void shouldReturnEmptyWhenUserDoesNotExist() {
        // when
        var found = userRepository.findByUsername("ghost");

        // then
        assertThat(found).isEmpty();
    }
}