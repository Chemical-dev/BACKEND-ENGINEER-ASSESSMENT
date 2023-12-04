package task.blog_api.service;

import org.springframework.http.ResponseEntity;
import task.blog_api.dtos.updateDto;
import task.blog_api.payload.request.FollowDto;
import task.blog_api.payload.request.LoginRequest;
import task.blog_api.payload.request.PostDto;
import task.blog_api.payload.request.SignupRequest;

public interface UserService {
    ResponseEntity<ApiDataResponseDto> follow(FollowDto followDto);
    ResponseEntity<ApiDataResponseDto> unfollow(FollowDto followDto);

    public ResponseEntity<ApiDataResponseDto> signUp(SignupRequest signUpRequest);
    public ResponseEntity<ApiDataResponseDto> login(LoginRequest loginRequest);

    public ResponseEntity<ApiDataResponseDto> deleteUser(Long id, String email);
    public ResponseEntity<ApiDataResponseDto> updateUser(Long id, updateDto updateDto);

    public ResponseEntity<ApiDataResponseDto> getUser(Long id);

    public ResponseEntity<ApiDataResponseDto> getAllUsers();

}
