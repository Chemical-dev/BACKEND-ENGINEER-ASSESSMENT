package task.blog_api.dtos;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class PublicStoryDto {
    String heading;
    String body;
    String type;
}
