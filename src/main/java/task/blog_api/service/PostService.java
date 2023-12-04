package task.blog_api.service;

import org.springframework.http.ResponseEntity;
import task.blog_api.payload.request.CommentDto;
import task.blog_api.payload.request.LikePostDto;
import task.blog_api.payload.request.PostDto;

public interface PostService {
    ResponseEntity<ApiDataResponseDto> createPost(PostDto postDto);
    ResponseEntity<ApiDataResponseDto>  updatePost(PostDto postDto, Long postId);

    ResponseEntity<ApiDataResponseDto>  deletePost(Long postId);

    ResponseEntity<ApiDataResponseDto>  getPost(Long postId);
    ResponseEntity<ApiDataResponseDto>  createComment(CommentDto commentDto);

    ResponseEntity<ApiDataResponseDto>  likePost(LikePostDto likePostDto);

    public ResponseEntity<ApiDataResponseDto> getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir);
}
