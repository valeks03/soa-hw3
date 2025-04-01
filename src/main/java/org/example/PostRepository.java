package org.example;


import com.example.social_network.PostOuterClass.Post;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class PostRepository {


    private final ConcurrentHashMap<String, Post> posts = new ConcurrentHashMap<>();


    public void save(Post post) {
        posts.put(post.getId(), post);
    }


    public Post getById(String id) {
        return posts.get(id);
    }


    public Post delete(String id) {
        return posts.remove(id);
    }


    public Collection<Post> getAll() {
        return posts.values();
    }


    public void printAllPosts() {
        posts.forEach((id, post) -> {
            System.out.println("ID: " + id + ", Post: " + post);
        });
    }
}