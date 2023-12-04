package task.blog_api.controllers;

import task.blog_api.annotations.DELETE;
import task.blog_api.annotations.GET;
import task.blog_api.annotations.POST;
import task.blog_api.annotations.PREAUTHORIZE;
import task.blog_api.constants.AppConstants;
;
import task.blog_api.payload.request.DeleteUser;
import task.blog_api.payload.request.PostDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import task.blog_api.service.ApiDataResponseDto;
import task.blog_api.service.PostService;

@RestController
@RequestMapping("/story")
public class PostController {

    @Autowired
    private PostService postService;

    @DELETE("/delete") @PREAUTHORIZE("hasRole('USER')")
    public ResponseEntity<ApiDataResponseDto> deleteStory(@RequestBody DeleteUser deleteUser) throws Exception {
        return postService.deletePost(deleteUser.getUserId());
    }

    @GET("/stories") @PREAUTHORIZE("hasRole('USER')")
    public ResponseEntity<ApiDataResponseDto> getAllStories(
            @RequestParam(value = "start", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int start,
            @RequestParam(value = "count", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int count,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
            ){
        return postService.getAllPosts(start, count, sortBy, sortDir);
    }

    @POST("/") @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiDataResponseDto> createPost(@RequestBody PostDto postDto){
        return postService.createPost(postDto);
    }

    @POST("/{postId}") @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiDataResponseDto> updatePost(@PathVariable("postId") Long postId, @RequestBody PostDto postDto){
        return postService.updatePost(postDto, postId);
    }
}
