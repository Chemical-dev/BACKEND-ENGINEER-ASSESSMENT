package task.blog_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import task.blog_api.models.Comment;
import task.blog_api.models.User;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
