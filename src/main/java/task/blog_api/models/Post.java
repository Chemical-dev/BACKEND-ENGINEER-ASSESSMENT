package task.blog_api.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(	name = "post",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "id"),
        })
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private Date creationDate;
    private int likesCount;
    @ManyToOne
    private User user;
    @OneToMany(mappedBy = "post")
    private List<Comment> comments;
}
