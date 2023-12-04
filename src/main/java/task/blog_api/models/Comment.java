package task.blog_api.models;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(	name = "comment",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "id"),
        })
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private LocalDateTime creationDate;
    @ManyToOne
    private User user;
    @ManyToOne
    private Post post;
}
