package org.se.outl.Service;

import org.se.outl.Dto.UserDto;
import org.se.outl.Entity.User;

public interface UserService {
    User userAdd(UserDto user);
    User userFindByEmail(String email);
}
