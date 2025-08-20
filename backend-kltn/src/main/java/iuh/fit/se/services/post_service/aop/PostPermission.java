package iuh.fit.se.services.post_service.aop;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostPermission {
    enum ActionType {
        CREATE, // Đăng bài
        UPDATE, // Cập nhật bài (member)
        GRANT, // Sửa bài (leader,admin)
        DELETE  // Xóa bài
    }
    
    ActionType action();
    String postIdParam() default "";
}