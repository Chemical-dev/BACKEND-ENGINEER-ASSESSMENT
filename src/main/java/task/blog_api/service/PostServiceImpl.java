package task.blog_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import task.blog_api.dtos.StoryDto;
import task.blog_api.dtos.StoryResponse;
import task.blog_api.enums.ResponseCodes;
import task.blog_api.models.*;
import task.blog_api.payload.request.CommentDto;
import task.blog_api.payload.request.LikePostDto;
import task.blog_api.payload.request.PostDto;
import task.blog_api.repository.CommentRepository;
import task.blog_api.repository.LikeRepository;
import task.blog_api.repository.PostRepository;
import task.blog_api.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class PostServiceImpl implements PostService{
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    public PostServiceImpl(UserRepository userRepository, PostRepository postRepository, CommentRepository commentRepository, LikeRepository likeRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
    }

    @Override
    public ResponseEntity<ApiDataResponseDto>  createPost(PostDto postDto) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Optional<User> userOptional = userRepository.findByUsername(authentication.getName());
            if (userOptional.isEmpty()) {
                ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(ResponseCodes.BAD_REQUEST.toString(), "User Not Found",
                        new ArrayList<>());
                return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
            }

            Post post = new Post();
            post.setContent(postDto.getContent());
            post.setUser(userOptional.get());

            ApiDataResponseDto apiDataResponseDto = ApiDataResponseDto.builder().responseCode("00").responseMessage("Post Successful").payload(postRepository.save(post)).build();
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.OK);
        }catch(Exception ex){
            ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(ResponseCodes.BAD_REQUEST.toString(), "Post Not Successful",
                    new ArrayList<>());
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<ApiDataResponseDto> updatePost(PostDto postDto, Long postId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Optional<User> userOptional = userRepository.findByUsername(authentication.getName());
            if (userOptional.isEmpty()) {
                ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                        ResponseCodes.BAD_REQUEST.toString(),
                        "User Not Found",
                        new ArrayList<>()
                );
                return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
            }

            Optional<Post> postOptional = postRepository.findById(postId);
            if (postOptional.isEmpty()) {
                ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                        ResponseCodes.BAD_REQUEST.toString(),
                        "Post Not Found",
                        new ArrayList<>()
                );
                return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
            }

            Post existingPost = postOptional.get();
            if (!existingPost.getUser().equals(userOptional.get())) {
                ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                        ResponseCodes.BAD_REQUEST.toString(),
                        "You do not have permission to update this post",
                        new ArrayList<>()
                );
                return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
            }

            existingPost.setContent(postDto.getContent());

            ApiDataResponseDto apiDataResponseDto = ApiDataResponseDto.builder()
                    .responseCode("00")
                    .responseMessage("Post Updated Successfully")
                    .payload(postRepository.save(existingPost))
                    .build();

            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.OK);
        } catch(Exception ex) {
            ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                    ResponseCodes.BAD_REQUEST.toString(),
                    "Post Update Not Successful",
                    new ArrayList<>()
            );
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<ApiDataResponseDto> deletePost(Long postId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Optional<User> userOptional = userRepository.findByUsername(authentication.getName());
            if (userOptional.isEmpty()) {
                ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                        ResponseCodes.BAD_REQUEST.toString(),
                        "User Not Found",
                        new ArrayList<>()
                );
                return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
            }

            Optional<Post> postOptional = postRepository.findById(postId);
            if (postOptional.isEmpty()) {
                ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                        ResponseCodes.BAD_REQUEST.toString(),
                        "Post Not Found",
                        new ArrayList<>()
                );
                return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
            }

            Post existingPost = postOptional.get();
            if (!existingPost.getUser().equals(userOptional.get())) {
                ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                        ResponseCodes.BAD_REQUEST.toString(),
                        "You do not have permission to delete this post",
                        new ArrayList<>()
                );
                return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
            }
            postRepository.delete(existingPost);
            ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                    "00",
                    "Post Deleted Successfully",
                    new ArrayList<>()
            );

            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.OK);
        } catch(Exception ex) {
            ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                    ResponseCodes.BAD_REQUEST.toString(),
                    "Post Deletion Not Successful",
                    new ArrayList<>()
            );
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
        }
    }
    @Override
    public ResponseEntity<ApiDataResponseDto> getPost(Long postId) {
        try {
            Optional<Post> postOptional = postRepository.findById(postId);
            if (postOptional.isEmpty()) {
                ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                        ResponseCodes.NOT_FOUND.toString(),
                        "Post Not Found",
                        new ArrayList<>()
                );
                return new ResponseEntity<>(apiDataResponseDto, HttpStatus.NOT_FOUND);
            }
            Post post = postOptional.get();
            ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                    "00",
                    "Post Retrieved Successfully",
                    Collections.singletonList(post)
            );

            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.OK);
        } catch(Exception ex) {
            ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                    ResponseCodes.BAD_REQUEST.toString(),
                    "Error Retrieving Post",
                    new ArrayList<>()
            );
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<ApiDataResponseDto> createComment(CommentDto commentDto) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Optional<User> userOptional = userRepository.findByUsername(authentication.getName());
            if (userOptional.isEmpty()) {
                ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                        ResponseCodes.BAD_REQUEST.toString(),
                        "User Not Found",
                        new ArrayList<>()
                );
                return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
            }

            Optional<Post> postOptional = postRepository.findById(commentDto.getPostId());
            if (postOptional.isEmpty()) {
                ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                        ResponseCodes.NOT_FOUND.toString(),
                        "Post Not Found",
                        new ArrayList<>()
                );
                return new ResponseEntity<>(apiDataResponseDto, HttpStatus.NOT_FOUND);
            }

            Comment comment = new Comment();
            comment.setPost(postOptional.get());
            comment.setUser(userOptional.get());
            comment.setContent(comment.getContent());
            comment.setCreationDate(LocalDateTime.now());

            commentRepository.save(comment);
            postRepository.save(postOptional.get());

            ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                    "00",
                    "Comment Successfully Added",
                    Collections.singletonList(postOptional.get())
            );

            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.OK);
        }catch(Exception exception){
            ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                    ResponseCodes.BAD_REQUEST.toString(),
                    "Error Adding Comment",
                    new ArrayList<>()
            );
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
        }
    }
    @Override
    public ResponseEntity<ApiDataResponseDto> likePost(LikePostDto likePostDto) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Optional<User> userOptional = userRepository.findByUsername(authentication.getName());
            if (userOptional.isEmpty()) {
                ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                        ResponseCodes.BAD_REQUEST.toString(),
                        "User Not Found",
                        new ArrayList<>()
                );
                return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
            }

            Optional<Post> postOptional = postRepository.findById(likePostDto.getPostId());
            if (postOptional.isEmpty()) {
                ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                        ResponseCodes.NOT_FOUND.toString(),
                        "Post Not Found",
                        new ArrayList<>()
                );
                return new ResponseEntity<>(apiDataResponseDto, HttpStatus.NOT_FOUND);
            }
            Like like = likeRepository.findLikeByPostIdAndLikerId(likePostDto.getPostId(), likePostDto.getLikerId());
            if(like != null){
               like.setLikeValue(likePostDto.getLikeValue());
            }else {
                like = new Like();
                like.setLikeValue(likePostDto.getLikeValue());
                like.setPostId(likePostDto.getPostId());
                like.setLikerId(likePostDto.getLikerId());
            }
            likeRepository.save(like);
            postOptional.get().setLikesCount(likesCount(likePostDto.getPostId()));
            postRepository.save(postOptional.get());

            ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                    "00",
                    "Post Liked Successfully",
                    Collections.singletonList(postOptional.get())
            );
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.OK);
        }catch(Exception exception){
            ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                    ResponseCodes.BAD_REQUEST.toString(),
                    "Error Liking Post",
                    new ArrayList<>()
            );
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
        }
    }


    @Override
    public ResponseEntity<ApiDataResponseDto> getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Optional<User> userOptional = userRepository.findByUsername(authentication.getName());
            if (userOptional.isEmpty()) {
                ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                        ResponseCodes.BAD_REQUEST.toString(),
                        "User Not Found",
                        new ArrayList<>()
                );
                return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
            }
            Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
            Page<Post> stories = postRepository.findAll(pageable);
            List<Post> listOfPosts =  stories.stream().filter(story -> story.getUser()
                    .getUsername().equals(authentication.getName())).toList();;

            StoryResponse postResponse = new StoryResponse();
            postResponse.setContent(listOfPosts);
            postResponse.setPageNo(stories.getNumber());
            postResponse.setPageSize(stories.getSize());
            postResponse.setTotalElements(stories.getTotalElements());
            postResponse.setTotalPages(stories.getTotalPages());
            postResponse.setLast(stories.isLast());

            ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                    "00",
                    "Posts Fetched Successfully",
                    Collections.singletonList(postResponse)
            );
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.OK);
        }catch(Exception ex){
            ApiDataResponseDto apiDataResponseDto = new ApiDataResponseDto(
                    ResponseCodes.BAD_REQUEST.toString(),
                    "Error Fetching Posts",
                    new ArrayList<>()
            );
            return new ResponseEntity<>(apiDataResponseDto, HttpStatus.BAD_REQUEST);
        }

    }

    private  int likesCount(Long postId){
        List<Like> likes = likeRepository.findAllByPostIdAndLikeValue(postId, true);
        return likes.size();
    }
}
