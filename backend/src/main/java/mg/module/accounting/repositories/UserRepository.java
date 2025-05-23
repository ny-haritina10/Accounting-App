package mg.module.accounting.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.module.accounting.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
}