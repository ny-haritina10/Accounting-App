package mg.module.accounting.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.module.accounting.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByLabel(String label);
}