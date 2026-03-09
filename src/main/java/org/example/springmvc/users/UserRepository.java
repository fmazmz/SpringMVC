package org.example.springmvc.users;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends ListCrudRepository<User, UUID> {
    boolean existsByEmail(String email);
}
