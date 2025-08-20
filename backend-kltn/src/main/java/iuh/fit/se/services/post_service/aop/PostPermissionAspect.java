package iuh.fit.se.services.post_service.aop;
import java.util.Collection;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import iuh.fit.se.entity.Post;
import iuh.fit.se.errorHandler.AccessDeniedException;
import iuh.fit.se.services.post_service.aop.PostPermission.ActionType;
import iuh.fit.se.services.post_service.repository.PostRepository;
import iuh.fit.se.services.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class PostPermissionAspect {

	private final PostRepository postRepository;

	@Around("@annotation(postPermission)")
	public Object checkPostPermission(
		ProceedingJoinPoint joinPoint,
		PostPermission postPermission
	) throws Throwable {
		Authentication authentication = SecurityContextHolder
			.getContext()
			.getAuthentication();
		String currentUsername = authentication.getName();
		Collection<String> authorities = authentication
			.getAuthorities()
			.stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.toList());

		// Kiểm tra quyền đăng bài
		if (postPermission.action() == PostPermission.ActionType.CREATE) {
			if (isAdminOrMember(authorities)) {
				return joinPoint.proceed();
			}
			throw new AccessDeniedException(
				"Chỉ ADMIN và MEMBER được đăng bài");
		}

		// Kiểm tra quyền xem bài

		if (postPermission.action() == ActionType.GRANT) {
			if (isAdminOrMember(authorities)) {
				return joinPoint.proceed();
			}
			throw new AccessDeniedException(
				"Chỉ ADMIN và MEMBER được thực thi");
		}

		// Kiểm tra quyền sửa/xóa bài
		String postId = getPostIdFromArgs(joinPoint.getArgs(),
			postPermission.postIdParam());
		Post post = postRepository
			.findById(postId)
			.orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));

		if (isLeader(authorities)
			|| (isMember(authorities) && isAuthor(post, currentUsername))) {
			return joinPoint.proceed();
		}

		throw new AccessDeniedException(
			"Bạn không có quyền thực hiện hành động này");
	}

	private boolean isAdminOrMember(Collection<String> authorities) {
		return isLeader(authorities) || isMember(authorities);
	}

	private boolean isLeader(Collection<String> authorities) {
		return authorities.contains("ROLE_LEADER")
			|| authorities.contains("ROLE_ADMIN");
	}

	private boolean isMember(Collection<String> authorities) {
		return authorities.contains("ROLE_MEMBER");
	}

	private boolean isAuthor(Post post, String username) {
		return post.getWriter().getUsername().equals(username);
	}

	private String getPostIdFromArgs(Object[] args, String paramName) {
		// Triển khai logic lấy postId từ tham số
		// Giả sử postId là tham số đầu tiên nếu không chỉ định paramName
		if (paramName.isEmpty()) {
			return (String) args[0];
		}

		// Nếu có chỉ định paramName, cần thêm logic để tìm đúng tham số
		// Có thể sử dụng Spring Expression Language (SpEL) cho phức tạp hơn
		// Ở đây là ví dụ đơn giản
		for (Object arg : args) {
			if (arg instanceof String && arg.equals(paramName)) {
				return (String) arg;
			}
		}
		throw new IllegalArgumentException(
			"Không tìm thấy postId trong tham số");
	}
}