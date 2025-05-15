package stquokka.codeStudy.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stquokka.codeStudy.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("""
        SELECT p
        FROM User p
        WHERE p.id = :userLongId
    """)
    Optional<User> findByUserLongId(Long userLongId);

    @Query("""
        SELECT p
        FROM User p
        WHERE p.email = :email
    """)
    User findByEmailCustom(String email);

    @Query("""
        SELECT COUNT(p) > 0
        FROM User p
        WHERE p.nickname = :nickname AND p.isDeleted = false
    """)
    boolean existsByNickname(@Param("nickname") String nickname);

    @Query("""
        SELECT COUNT(p) > 0
        FROM User p
        WHERE p.email = :email AND p.isDeleted = false
    """)
    boolean existsByEmail(@Param("email") String email);

    @Query("""
        SELECT p
        FROM User p
        WHERE p.email = :email AND p.isDeleted = false
    """)
    Optional<User> findNotDeletedUserByUserId(@Param("email") String email);

    @Query("""
        SELECT p
        FROM User p
        WHERE p.isDeleted = true
    """)
    List<User> findDeletedUsers();
    Optional<User> findByNickname(String nickname);
    Optional<User> findByNicknameAndIsDeletedFalse(String nickname);

}
