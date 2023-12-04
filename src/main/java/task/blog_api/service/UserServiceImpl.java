package task.blog_api.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import task.blog_api.dtos.updateDto;
import task.blog_api.enums.ResponseCodes;
import task.blog_api.models.ERole;
import task.blog_api.models.Role;
import task.blog_api.models.User;
import task.blog_api.payload.request.FollowDto;
import task.blog_api.payload.request.LoginRequest;
import task.blog_api.payload.request.SignupRequest;
import task.blog_api.payload.response.JwtResponse;
import task.blog_api.repository.RoleRepository;
import task.blog_api.repository.UserRepository;
import task.blog_api.security.jwt.JwtUtils;
import task.blog_api.security.services.UserDetailsImpl;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public ResponseEntity<ApiDataResponseDto> follow(FollowDto followDto) {
        try {
            Optional<User> followedUserOptional = userRepository.findById(followDto.getFollowedId());
            Optional<User> followerUserOptional = userRepository.findById(followDto.getFollowerId());

            if (followedUserOptional.isEmpty() || followerUserOptional.isEmpty()) {
                ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                        ResponseCodes.BAD_REQUEST.toString(),
                        "User Not Found",
                        new ArrayList<>()
                );
                return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
            }
            User followedUser = followedUserOptional.get();
            User followerUser = followerUserOptional.get();

            followedUser.getFollowing().add(followerUser);
            userRepository.save(followedUser);

            ApiDataResponseDto apiDataResponseDto = ApiDataResponseDto.builder()
                    .responseCode("00")
                    .responseMessage("Follow Successful")
                    .payload(followedUser)
                    .build();
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.OK);
        } catch (Exception exception) {
            ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                    ResponseCodes.BAD_REQUEST.toString(),
                    "Follow Not Successful",
                    new ArrayList<>()
            );
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
        }
    }
    @Override
    public ResponseEntity<ApiDataResponseDto> unfollow(FollowDto followDto) {
        try {
            Optional<User> followedUserOptional = userRepository.findById(followDto.getFollowedId());
            if (followedUserOptional.isEmpty()) {
                ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                        ResponseCodes.BAD_REQUEST.toString(),
                        "User Not Found",
                        new ArrayList<>()
                );
                return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
            }
            Optional<User> followerUserOptional = userRepository.findById(followDto.getFollowerId());
            if (followerUserOptional.isEmpty()) {
                ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                        ResponseCodes.BAD_REQUEST.toString(),
                        "User Not Found",
                        new ArrayList<>()
                );
                return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
            }
            User followedUser = followedUserOptional.get();
            User followerUser = followerUserOptional.get();

            followedUser.getFollowing().remove(followerUser);
            userRepository.save(followedUser);

            ApiDataResponseDto apiDataResponseDto = ApiDataResponseDto.builder()
                    .responseCode("00")
                    .responseMessage("Unfollow Successful")
                    .payload(followedUser)
                    .build();
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.OK);
        } catch (Exception exception) {
            ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                    ResponseCodes.BAD_REQUEST.toString(),
                    "Unfollow Not Successful",
                    new ArrayList<>()
            );
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
        }
    }
    @Override
    public ResponseEntity<ApiDataResponseDto> signUp(SignupRequest signUpRequest){
        try{
            if (userRepository.existsByUsername(signUpRequest.getUsername())) {
                ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                        ResponseCodes.BAD_REQUEST.toString(),
                        "Error: Username is already taken!",
                        new ArrayList<>()
                );
                return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
            }

            if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                        ResponseCodes.BAD_REQUEST.toString(),
                        "Error: Email is already in use!",
                        new ArrayList<>()
                );
                return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
            }
            User user = new User(signUpRequest.getUsername(),
                    signUpRequest.getEmail(),
                    encoder.encode(signUpRequest.getPassword()), signUpRequest.getProfilePicture());

            Set<String> strRoles = signUpRequest.getRole();
            Set<Role> roles = new HashSet<>();

            if (strRoles == null) {
                Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(userRole);
            } else {
                strRoles.forEach(role -> {
                    switch (role) {
                        case "admin":
                            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(adminRole);

                            break;
                        default:
                            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(userRole);
                    }
                });
            }
            user.setRoles(roles);
            userRepository.save(user);

            ApiDataResponseDto apiDataResponseDto = ApiDataResponseDto.builder()
                    .responseCode("00")
                    .responseMessage("User registered successfully!")
                    .payload( userRepository.save(user))
                    .build();
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.OK);
        }catch(Exception exception){
            ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                    ResponseCodes.BAD_REQUEST.toString(),
                    "User Registration Unsuccessful",
                    new ArrayList<>()
            );
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
        }
    }
    @Override
    public ResponseEntity<ApiDataResponseDto> login(LoginRequest loginRequest){
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());


            ApiDataResponseDto apiDataResponseDto = ApiDataResponseDto.builder()
                    .responseCode("00")
                    .responseMessage("Login Successful!")
                    .payload(new JwtResponse(jwt,
                            userDetails.getId(),
                            userDetails.getUsername(),
                            userDetails.getEmail(),
                            roles))
                    .build();
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.OK);
        }catch(Exception exception){
            ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                    ResponseCodes.BAD_REQUEST.toString(),
                    "Login Unsuccessful",
                    new ArrayList<>()
            );
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
        }
    }
    @Override
    public ResponseEntity<ApiDataResponseDto> deleteUser(Long id, String email){
        Optional<User> userModel = this.userRepository.findById(id);
        try {
            if(userModel.isEmpty()){
                ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                        ResponseCodes.BAD_REQUEST.toString(),
                        "No user found with email : " + email,
                        new ArrayList<>()
                );
                return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
            }
            userRepository.delete(userModel.get());

            ApiDataResponseDto apiDataResponseDto = ApiDataResponseDto.builder()
                    .responseCode("00")
                    .responseMessage("User Successfully Deleted")
                    .build();
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.OK);
        } catch (Exception exception) {
            ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                    ResponseCodes.BAD_REQUEST.toString(),
                    "delete failed",
                    new ArrayList<>()
            );
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<ApiDataResponseDto> updateUser(Long id, updateDto updateDto){
        Optional<User> userModel = this.userRepository.findById(id);
        try {
            if(userModel.isEmpty()){
                ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                        ResponseCodes.BAD_REQUEST.toString(),
                        "No user found with id  : " + id,
                        new ArrayList<>()
                );
                return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
            }
            User user = userModel.get();
            user.setUsername(updateDto.getUsername());
            user.setEmail(updateDto.getEmail());
            user.setRoles(updateDto.getRoles());

            ApiDataResponseDto apiDataResponseDto = ApiDataResponseDto.builder()
                    .responseCode("00")
                    .responseMessage("User Successfully Updated")
                    .payload(userRepository.save(user))
                    .build();
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.OK);
        }catch(Exception exception){
            ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                    ResponseCodes.BAD_REQUEST.toString(),
                    "Update failed",
                    new ArrayList<>()
            );
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<ApiDataResponseDto> getUser(Long id) {
        Optional<User> userModel = this.userRepository.findById(id);
        try {
            if (userModel.isEmpty()) {
                ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                        ResponseCodes.BAD_REQUEST.toString(),
                        "No user found with id  : " + id,
                        new ArrayList<>()
                );
                return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
            }
            ApiDataResponseDto apiDataResponseDto = ApiDataResponseDto.builder()
                    .responseCode("00")
                    .responseMessage("User Fetched Successfully")
                    .payload(userModel.get())
                    .build();
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.OK);
        } catch (Exception exception) {
            ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                    ResponseCodes.BAD_REQUEST.toString(),
                    "No user found",
                    new ArrayList<>()
            );
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<ApiDataResponseDto> getAllUsers(){
        List<User> userList = this.userRepository.findAll();
        try {
            if(userList.isEmpty()){
                ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                        ResponseCodes.BAD_REQUEST.toString(),
                        "No user found",
                        new ArrayList<>()
                );
                return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
            }
            ApiDataResponseDto apiDataResponseDto = ApiDataResponseDto.builder()
                    .responseCode("00")
                    .responseMessage("Users Fetched Successfully")
                    .payload(userList)
                    .build();
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.OK);
        }catch(Exception exception){
            ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                    ResponseCodes.BAD_REQUEST.toString(),
                    "No user found",
                    new ArrayList<>()
            );
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
        }
    }
}
