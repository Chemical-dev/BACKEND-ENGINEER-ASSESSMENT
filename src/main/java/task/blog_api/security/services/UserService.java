package task.blog_api.security.services;

import task.blog_api.dtos.updateDto;
import org.springframework.stereotype.Service;
import task.blog_api.models.User;

import java.util.List;

@Service
public interface UserService {
    String deleteUser(Long id, String email) throws Exception;
    User updateUser(Long id, updateDto updateDto) throws Exception;
    User userDetails(Long id) throws Exception;
    List<User> getAllUsers();


}
