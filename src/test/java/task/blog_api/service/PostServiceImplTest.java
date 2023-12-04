package task.blog_api.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import task.blog_api.models.Post;
import task.blog_api.models.User;
import task.blog_api.payload.request.PostDto;
import task.blog_api.repository.PostRepository;
import task.blog_api.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class PostServiceImplTest {
    @InjectMocks
    private PostServiceImpl postServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Test
    void createPost() {
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("testUser");

        User user = new User();
        user.setUsername("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        PostDto postDto = new PostDto();
        postDto.setContent("Test post content");

        Post savedPost = new Post();
        savedPost.setContent(postDto.getContent());
        savedPost.setUser(user);
        when(postRepository.save(any(Post.class))).thenReturn(savedPost);

        ResponseEntity<ApiDataResponseDto> responseEntity = postServiceImpl.createPost(postDto);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("00", responseEntity.getBody().getResponseCode());
        assertEquals("Post Successful", responseEntity.getBody().getResponseMessage());
        assertEquals(savedPost, responseEntity.getBody().getPayload());
    }

    @Test
    void updatePost() {
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("testUser");

        User user = new User();
        user.setUsername("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        Post existingPost = new Post();
        existingPost.setId(1L);
        existingPost.setContent("Existing post content");
        existingPost.setUser(user);
        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));

        PostDto postDto = new PostDto();
        postDto.setContent("Updated post content");

        Post updatedPost = new Post();
        updatedPost.setId(1L);
        updatedPost.setContent(postDto.getContent());
        updatedPost.setUser(user);
        when(postRepository.save(any(Post.class))).thenReturn(updatedPost);

        ResponseEntity<ApiDataResponseDto> responseEntity = postServiceImpl.updatePost(postDto, 1L);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("00", responseEntity.getBody().getResponseCode());
        assertEquals("Post Updated Successfully", responseEntity.getBody().getResponseMessage());
        assertEquals(updatedPost, responseEntity.getBody().getPayload());
    }

    @Test
    void deletePost() {
    }

    @Test
    void getPost() {
    }

    @Test
    void createComment() {
    }

    @Test
    void likePost() {
    }

    @Test
    void getAllPosts() {
    }
}