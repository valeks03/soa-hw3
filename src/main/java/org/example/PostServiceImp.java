package org.example;

import com.example.social_network.PostOuterClass;
import com.example.social_network.PostServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostServiceImp extends PostServiceGrpc.PostServiceImplBase {

    private final PostRepository repository = new PostRepository();

    @Override
    public void createPost(PostOuterClass.CreatePostRequest request, StreamObserver<PostOuterClass.CreatePostResponse> responseObserver) {

        String id = UUID.randomUUID().toString();
        String now = Instant.now().toString();

        PostOuterClass.Post post = PostOuterClass.Post.newBuilder()
                .setId(id)
                .setTitle(request.getTitle())
                .setDescription(request.getDescription())
                .setCreatorId(request.getCreatorId())
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .setIsPrivate(request.getIsPrivate())
                .addAllTags(request.getTagsList())
                .build();

        repository.save(post);

        PostOuterClass.CreatePostResponse response = PostOuterClass.CreatePostResponse.newBuilder()
                .setPost(post)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deletePost(PostOuterClass.DeletePostRequest request,
                           StreamObserver<PostOuterClass.DeletePostResponse> responseObserver) {

        boolean success = (repository.delete(request.getId()) != null);
        PostOuterClass.DeletePostResponse response = PostOuterClass.DeletePostResponse.newBuilder()
                .setSuccess(success).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updatePost(PostOuterClass.UpdatePostRequest request,
                           StreamObserver<PostOuterClass.UpdatePostResponse> responseObserver) {
        PostOuterClass.Post existingPost = repository.getById(request.getId());
        if (existingPost == null) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Post not found")
                            .asRuntimeException()
            );
            return;
        }

        // Обновляем поля поста
        String now = Instant.now().toString();
        PostOuterClass.Post.Builder builder = existingPost.toBuilder();

        // Обновляем только если значение задано (для примера обновляем все поля)
        if (!request.getTitle().isEmpty()) {
            builder.setTitle(request.getTitle());
        }
        if (!request.getDescription().isEmpty()) {
            builder.setDescription(request.getDescription());
        }
        builder.setIsPrivate(request.getIsPrivate());
        builder.clearTags();
        builder.addAllTags(request.getTagsList());
        builder.setUpdatedAt(now);

        PostOuterClass.Post updatedPost = builder.build();
        repository.save(updatedPost);

        PostOuterClass.UpdatePostResponse response = PostOuterClass.UpdatePostResponse.newBuilder()
                .setPost(updatedPost)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    @Override
    public void getPostById(PostOuterClass.GetPostByIdRequest request, StreamObserver<PostOuterClass.GetPostByIdResponse> responseObserver) {
        PostOuterClass.Post post = repository.getById(request.getId());
        if (post == null) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Post not found")
                            .asRuntimeException()
            );
            return;
        }

        PostOuterClass.GetPostByIdResponse response = PostOuterClass.GetPostByIdResponse.newBuilder()
                .setPost(post)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    @Override
    public void getPosts(PostOuterClass.GetPostsRequest request, StreamObserver<PostOuterClass.GetPostsResponse> responseObserver) {
        // Список всех постов
        List<PostOuterClass.Post> allPosts = new ArrayList<>(repository.getAll());
        int totalCount = allPosts.size();
        int page = request.getPage();
        int pageSize = request.getPageSize();

        // Рассчитываем индексы для пагинации
        int fromIndex = Math.max(0, (page - 1) * pageSize);
        int toIndex = Math.min(fromIndex + pageSize, totalCount);
        List<PostOuterClass.Post> paginatedPosts = new ArrayList<>();
        if (fromIndex < toIndex) {
            paginatedPosts = allPosts.subList(fromIndex, toIndex);
        }

        PostOuterClass.GetPostsResponse response = PostOuterClass.GetPostsResponse.newBuilder()
                .addAllPosts(paginatedPosts)
                .setTotalCount(totalCount)
                .setPage(page)
                .setPageSize(pageSize)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void debugGetAllPosts(PostOuterClass.Empty request, StreamObserver<PostOuterClass.GetPostsResponse> responseObserver) {
        List<PostOuterClass.Post> allPosts = new ArrayList<>(repository.getAll());
        PostOuterClass.GetPostsResponse response = PostOuterClass.GetPostsResponse.newBuilder()
                .addAllPosts(allPosts)
                .setTotalCount(allPosts.size())
                .setPage(1)
                .setPageSize(allPosts.size())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
