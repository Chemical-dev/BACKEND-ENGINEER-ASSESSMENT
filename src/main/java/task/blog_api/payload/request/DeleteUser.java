package task.blog_api.payload.request;

import lombok.Getter;

@Getter
public class DeleteUser {
    private Long id;
    private Long userId;
}