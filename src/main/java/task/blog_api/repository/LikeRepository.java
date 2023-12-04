package task.blog_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.w3c.dom.stylesheets.LinkStyle;
import task.blog_api.models.Comment;
import task.blog_api.models.Like;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Like findLikeByPostIdAndLikerId(Long postId, Long likerId);
    List<Like> findAllByPostIdAndLikeValue(Long postId, Boolean likeValue);
}
