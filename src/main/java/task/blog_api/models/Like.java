package task.blog_api.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(	name = "like",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "id"),
        })
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long likerId;
    private Long postId;
    private Boolean likeValue = false;
}
