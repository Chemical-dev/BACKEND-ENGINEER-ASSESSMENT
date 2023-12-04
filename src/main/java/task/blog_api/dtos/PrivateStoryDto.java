package task.blog_api.dtos;

import task.blog_api.models.BodyType;
import lombok.Data;
import lombok.Getter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Data
public class PrivateStoryDto {
   private String heading;
    private String body;
    @Enumerated(EnumType.STRING)
    private BodyType type;
}
