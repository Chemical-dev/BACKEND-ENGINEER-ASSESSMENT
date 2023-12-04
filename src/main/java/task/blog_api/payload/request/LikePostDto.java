package task.blog_api.payload.request;

import lombok.Data;

@Data
public class LikePostDto {
    private Long postId;
    private Long likerId;
    private Boolean likeValue;
}
