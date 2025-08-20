package iuh.fit.se.services.post_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import iuh.fit.se.entity.Post;
import iuh.fit.se.entity.enumerator.FunctionStatus;
import iuh.fit.se.services.post_service.dto.PostCreateRequestDto;
import iuh.fit.se.services.post_service.dto.SearchPostRequestDto;

public interface PostService {
	
	Page<Post> getAllPublicPosts(Pageable pageable, SearchPostRequestDto searchPostRequest);
	
	boolean isPostExist(String postId);
	
	Post createPost(PostCreateRequestDto post);
	/*
	 * Phương thức để chuyển trạng thái post sang accepted.
	 */
	Post validatePost(String postId);
	
	Post getAcceptedPostById(String postId);
	
	Post updatePost(String postId, PostCreateRequestDto post);
	
	Post modifyPost(String postId, FunctionStatus status);
	
	void deletePost(String postId);
	
//	phương thức để lấy tất cả các post của một user theo userId
	Page<Post> getPostsByUserId(String userId, FunctionStatus status, String title, Pageable pageable);
}
