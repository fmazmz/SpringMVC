package org.example.springmvc.users;

import org.example.springmvc.users.dto.CreateUserDTO;
import org.example.springmvc.users.model.User;

public interface UserService {

    void create(CreateUserDTO dto);

    User getCurrentUser();
}
