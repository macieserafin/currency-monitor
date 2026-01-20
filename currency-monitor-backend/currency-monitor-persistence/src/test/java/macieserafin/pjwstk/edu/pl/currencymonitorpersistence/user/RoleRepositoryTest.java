package macieserafin.pjwstk.edu.pl.currencymonitorpersistence.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void shouldFindRoleByName() {
        // given
        Role role = new Role("ADMIN");
        roleRepository.save(role);

        // when
        var found = roleRepository.findByName("ADMIN");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("ADMIN");
    }

    @Test
    void shouldReturnEmptyWhenRoleDoesNotExist() {
        // when
        var found = roleRepository.findByName("USER");

        // then
        assertThat(found).isEmpty();
    }
}