package com.myboard.firebase.fcm;

import com.myboard.entity.Article;
import com.myboard.entity.User;
import com.myboard.repository.article.ArticleRepository;
import com.myboard.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationService {

    private final FCMTokenManager fcmTokenManager;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final FCMService fcmService;

    @Value("${firebase.notifications.defaults.title}")
    private String defaultTitle;

    @Value("${firebase.notifications.defaults.message}")
    private String defaultMessage;

    @Value("${firebase.notifications.defaults.token}")
    private String defaultToken;

    // 게시글에 댓글이 달리면 알림 전송
    @Async
    public void commentNotification(Long articleId, Long commentAuthorId) {
        log.info("PushNotificationService commentNotification");
        Optional<Article> targetArticle = articleRepository.findArticleByIdFetchJoin(articleId);
        Optional<User> commentAuthor = userRepository.findById(commentAuthorId);

        if (targetArticle.isPresent() && commentAuthor.isPresent()) {
            Long targetUserId = targetArticle.get().getUser().getId();
            String targetArticleTitle = targetArticle.get().getTitle();
            String targetUsername = targetArticle.get().getUser().getUsername();
            String commentAuthorName = commentAuthor.get().getUsername();
            String targetUserToken = fcmTokenManager.getToken(String.valueOf(targetUserId));

            Optional.ofNullable(targetUserToken).ifPresent(token -> fcmService.sendMessage(
                    token,
                    "새로운 댓글이 작성되었습니다!",
                    targetUsername + "님의" + targetArticleTitle + "게시글에" + commentAuthorName + "님이 댓글을 남겼습니다.")
            );
        }
    }

    @Async
    public void testCommentNotification() {
        fcmService.sendMessage(defaultToken, defaultTitle, defaultMessage);
    }
}
