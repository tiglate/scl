package ludo.mentis.aciem.scl.service;

import ludo.mentis.aciem.scl.model.UserDTO;
import ludo.mentis.aciem.scl.model.UserSearchDTO;
import ludo.mentis.aciem.scl.util.ReferencedWarning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserService {

	Page<UserDTO> findAll(UserSearchDTO searchDTO, Pageable pageable);

    UserDTO get(Long id);

    Long create(UserDTO userDTO);

    void update(Long id, UserDTO userDTO);

    void delete(Long id);

    boolean emailExists(String email);

    boolean usernameExists(String username);

    ReferencedWarning getReferencedWarning(Long id);

}
