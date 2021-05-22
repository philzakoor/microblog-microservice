package org.ac.cst8277.zakoor.phil.bms.dao;

import org.ac.cst8277.zakoor.phil.bms.dtos.Constants;
import org.ac.cst8277.zakoor.phil.bms.dtos.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class JdbcBmsRepository implements BmsRepository{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Map<UUID, Post> getAllPosts() {
        Map<UUID, Post> posts = new HashMap<>();

        List<Object> oPosts =jdbcTemplate.query(Constants.GET_ALL_POSTS,
                (rs, rowNum) -> new Post(DaoHelper.bytesArrayToUuid(rs.getBytes("id")),
                        DaoHelper.bytesArrayToUuid(rs.getBytes("uid")),
                        rs.getString("content"), rs.getInt("created")));

        for (Object oPost : oPosts) {
            if (!posts.containsKey(((Post) oPost).getId())){
                Post post = new Post();
                post.setId(((Post) oPost).getId());
                post.setUid(((Post) oPost).getUid());
                post.setContent(((Post) oPost).getContent());
                post.setCreated(((Post) oPost).getCreated());
                posts.put(((Post) oPost).getId(), post);
            }
        }

        return posts;
    }

    @Override
    public Map<UUID, Post> getAllPostsByUser(UUID userId) {
        Map<UUID, Post> posts = new HashMap<>();

        List<Object> oPosts =jdbcTemplate.query(Constants.GET_ALL_POST_BY_USER,
                (rs, rowNum) -> new Post(DaoHelper.bytesArrayToUuid(rs.getBytes("id")),
                        DaoHelper.bytesArrayToUuid(rs.getBytes("uid")),
                        rs.getString("content"), rs.getInt("created")), userId.toString());

        for (Object oPost : oPosts) {
            if (!posts.containsKey(((Post) oPost).getId())){
                Post post = new Post();
                post.setId(((Post) oPost).getId());
                post.setUid(((Post) oPost).getUid());
                post.setContent(((Post) oPost).getContent());
                post.setCreated(((Post) oPost).getCreated());
                posts.put(((Post) oPost).getId(), post);
            }
        }
        return posts;
    }

    @Override
    public Post getPostById(UUID postId) {
        Post post = new Post();

        List<Object> posts =jdbcTemplate.query(Constants.GET_POST_BY_ID,
                (rs, rowNum) -> new Post(DaoHelper.bytesArrayToUuid(rs.getBytes("id")),
                        DaoHelper.bytesArrayToUuid(rs.getBytes("uid")),
                        rs.getString("content"), rs.getInt("created")),
                postId.toString());

        for (Object oPost : posts) {
            if (post.getId() == null){
                post.setId(((Post) oPost).getId());
                post.setUid(((Post) oPost).getUid());
                post.setContent(((Post) oPost).getContent());
                post.setCreated(((Post) oPost).getCreated());
            }
        }

        return post;
    }

    @Override
    public UUID createPost(Post post) {
        long timestamp = Instant.now().getEpochSecond();
        UUID postId = UUID.randomUUID();

        try {
            jdbcTemplate.update(Constants.CREATE_POST, postId.toString(), post.getUid().toString(), post.getContent(), timestamp);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return postId;
    }

    @Override
    public int deletePost(UUID postId) {
        return jdbcTemplate.update(Constants.DELETE_POST, postId.toString());
    }

    @Override
    public UUID editPost(UUID postID, Post post) {

        try {
            jdbcTemplate.update(Constants.EDIT_POST, post.getContent(), postID.toString());
        } catch (Exception e) {
            return null;
        }
        return postID;
    }
}
