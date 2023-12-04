package task.blog_api.dtos;

import lombok.Data;
import task.blog_api.models.BodyType;
import task.blog_api.models.User;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
@Data
public class StoryDto {
    private String heading;
    private String body;
    @Enumerated(EnumType.STRING)
    private BodyType type;
    private User user;
}
