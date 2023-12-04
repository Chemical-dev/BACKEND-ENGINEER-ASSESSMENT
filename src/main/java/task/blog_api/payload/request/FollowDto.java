package task.blog_api.payload.request;

import lombok.Data;

@Data
public class FollowDto {
    private Long followedId;
    private Long followerId;
}
