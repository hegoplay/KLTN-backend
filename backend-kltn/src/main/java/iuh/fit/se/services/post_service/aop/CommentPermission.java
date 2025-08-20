package iuh.fit.se.services.post_service.aop;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommentPermission {
    enum ActionType {
        MODIFY, // Sửa bài
        DELETE  // Xóa bài
    }
    
    ActionType action();
    String commentIdParam() default "";
}