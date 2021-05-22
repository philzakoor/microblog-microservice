package org.ac.cst8277.zakoor.phil.bms.dao;

import org.ac.cst8277.zakoor.phil.bms.dtos.Post;

import java.util.Map;
import java.util.UUID;

public interface BmsRepository {
    Map<UUID, Post> getAllPosts();
    Map<UUID, Post> getAllPostsByUser(UUID userId);
    Post getPostById(UUID postId);
    UUID createPost(Post post);
    int deletePost(UUID postId);
    UUID editPost (UUID postID, Post post);
}
