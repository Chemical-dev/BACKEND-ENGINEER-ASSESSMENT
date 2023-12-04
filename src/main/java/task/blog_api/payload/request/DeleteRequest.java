package task.blog_api.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteRequest {
    private Long id;
    private String email;
}
