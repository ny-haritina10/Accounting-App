package mg.module.accounting.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.module.accounting.models.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByIdUser(Long idUser);
    void deleteByIdUserAndIdRole(Long idUser, Long idRole);
    void deleteByIdUser(Long idUser);
}