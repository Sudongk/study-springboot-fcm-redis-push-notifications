package com.myboard;

import com.google.firebase.FirebaseApp;
import com.myboard.config.firebase.FirebaseInitializer;
import com.myboard.firebase.fcm.FCMService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FCMPushNotificationTest {

    @Mock
    private FirebaseInitializer firebaseInitializer;

    @Mock
    private FCMService fcmService;

    @Test
    public void contextLoads() {

    }

    @Test
    public void firebaseAppInitializationTest() {
        firebaseInitializer.firebaseInit();
        System.out.println(FirebaseApp.getApps());
        assertFalse(FirebaseApp.getApps().isEmpty());
    }

    @Test
    public void sendTestMessage() {

    }
}
