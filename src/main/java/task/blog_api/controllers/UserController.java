package task.blog_api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import task.blog_api.annotations.*;
import task.blog_api.dtos.updateDto;
import task.blog_api.payload.request.DeleteRequest;
import task.blog_api.payload.request.LoginRequest;
import task.blog_api.payload.request.SignupRequest;
import task.blog_api.service.ApiDataResponseDto;
import task.blog_api.service.UserServiceImpl;

import javax.validation.Valid;

@RestController
@RequestMapping("/facebook/user")
public class UserController{
    @Autowired
    private UserServiceImpl userService;

    @DELETE("/") @PREAUTHORIZE("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiDataResponseDto> deleteUser(@RequestBody DeleteRequest deleteRequest){
        return userService.deleteUser(deleteRequest.getId(), deleteRequest.getEmail());
    }

    @GET("/users") @PREAUTHORIZE("hasRole('ADMIN')")
    public ResponseEntity<ApiDataResponseDto>  getAllUsers(){
        return userService.getAllUsers();
    }

    @PUT("/{id}") @PREAUTHORIZE("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiDataResponseDto> updateUser(@PathVariable("id") Long userId, @RequestBody updateDto updateDto) {
        return userService.updateUser(userId, updateDto);
    }

    @GET("/{id}") @PREAUTHORIZE("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiDataResponseDto> userDetails(@PathVariable("id") Long userId){
        return userService.getUser(userId);
    }

    @POST("/signin")
    public ResponseEntity<ApiDataResponseDto> login(@RequestBody  LoginRequest loginRequest){
        return userService.login(loginRequest);
    }

    @POST("/signup")
    public ResponseEntity<ApiDataResponseDto> signUp(@Valid @RequestBody SignupRequest signUpRequest){
        return userService.signUp(signUpRequest);
    }
}
