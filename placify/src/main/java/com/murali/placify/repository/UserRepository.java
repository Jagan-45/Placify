package com.murali.placify.repository;

import com.murali.placify.entity.User;
import com.murali.placify.model.StaffProfileResDto;
import com.murali.placify.model.StudentProfileResDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByMailID(@NotBlank @Email(message = "Enter valid email address") String mailID);

    Optional<User> findByMailID(String username);

    User getReferenceByMailID(String mailID);

    Optional<List<User>> findByBatch_BatchNameIn(List<String> assignToBatches);

    Optional<User> findByUsername(@NotBlank String username);

    @Query(value = "SELECT u.user_id FROM users_table u", nativeQuery = true)
    List<UUID> findAllIds();
}
