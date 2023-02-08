package com.myboard.config.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Configuration
public class QuerydslConfig {

    // 동시성 문제가 발생하지 않기 위해 사용
    // 스프링 컨테이너가 초기화될 때 @PersistenceContext으로 주입받은 EntityManager를 Proxy로 감싸서.
    // EntityManager 호출마다 Proxy를 통해 EntityManager를 생성하여 Thread-Safe를 보장합니다.
    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

}
