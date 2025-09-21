package iuh.fit.se.post_service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import iuh.fit.se.entity.Post;
import iuh.fit.se.entity.User;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.entity.enumerator.UserRole;
import iuh.fit.se.services.post_service.dto.PostRequestDto;
import iuh.fit.se.services.post_service.service.PostService;
import iuh.fit.se.services.user_service.service.UserService;

@SpringBootTest
public class PostServiceTest {

	@Autowired
	private PostService postService;

	@Autowired
	private UserService userService;

	@Test
	@WithMockUser(username = "hegoplay", roles = {"MEMBER"})
	public void testWithUserRole() {

		PostRequestDto dto = new PostRequestDto("Test Post",
			"This is a test post content.",
			"converted_IUH-Logo_24x24.png_1755609775397",
			FunctionStatus.ACCEPTED);

		assertThrows(Exception.class, () -> {
			Post post = postService.createPost(dto);
		});
	}
}
