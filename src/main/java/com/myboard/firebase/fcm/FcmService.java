package com.myboard.firebase.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;
import com.myboard.entity.Article;
import com.myboard.entity.User;
import com.myboard.repository.article.ArticleRepository;
import com.myboard.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmTokenManager fcmTokenManager;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    private void sendMessage(String token, String title, String contents) {
        // setToken 혹은 setTopic을 이용해 메세지의 타겟을 결정
        Message message = Message.builder()
                .setToken(token)
                .setWebpushConfig(
                        WebpushConfig.builder()
                                .putHeader("HeaderKey", "HeaderValue")
                                .setNotification(new WebpushNotification(title, contents))
                                .build()
                )
                .build();
        try {
            String messageResponse = FirebaseMessaging.getInstance().sendAsync(message).get();
            log.info("Sent Message: {}", messageResponse);
        } catch (ExecutionException | InterruptedException e) {
            throw new IllegalStateException("알림 전송에 실패하였습니다.");
        }
    }

    // 게시글에 댓글이 달리면 알림 전송
    @Async
    public void commentNotification(Long articleId, Long commentAuthorId) {
        Optional<Article> targetArticle = articleRepository.findArticleByIdFetchJoin(articleId);
        Optional<User> commentAuthor = userRepository.findById(commentAuthorId);

        if (targetArticle.isPresent() && commentAuthor.isPresent()) {
            Long targetUserId = targetArticle.get().getUser().getId();
            String targetArticleTitle = targetArticle.get().getTitle();
            String targetUsername = targetArticle.get().getUser().getUsername();
            String commentAuthorName = commentAuthor.get().getUsername();
            Optional<String> targetUserToken = fcmTokenManager.getToken(targetUserId);

            targetUserToken.ifPresent(token -> sendMessage(
                    token,
                    "새로운 댓글이 있습니다",
                    targetUsername + "님의" + targetArticleTitle + "게시글에" + commentAuthorName + "님이 댓글을 남겼습니다.")
            );
        }
    }
}
