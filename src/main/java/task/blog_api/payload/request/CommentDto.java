package task.blog_api.payload.request;

import lombok.Data;

@Data
public class CommentDto {
    private Long postId;
    private String content;
}
