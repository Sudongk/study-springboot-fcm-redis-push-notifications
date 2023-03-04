package com.myboard.exception.common;

import com.myboard.exception.ExternalException;
import com.myboard.exception.article.ArticleContentNullException;
import com.myboard.exception.article.ArticleNotFoundException;
import com.myboard.exception.article.ArticleTitleBlankException;
import com.myboard.exception.article.ArticleTitleLengthException;
import com.myboard.exception.articleComment.CommentBlankException;
import com.myboard.exception.articleComment.CommentLengthException;
import com.myboard.exception.articleComment.CommentNotFoundException;
import com.myboard.exception.board.*;
import com.myboard.exception.MyboardException;
import com.myboard.exception.firebase.FcmInitException;
import com.myboard.exception.search.*;
import com.myboard.exception.user.NotAuthorException;
import com.myboard.exception.user.UserNameDuplicatedException;
import com.myboard.exception.user.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum ErrorType {

    U001("U001", "해당 유저를 찾을 수 없습니다.", UserNotFoundException.class),
    U002("U002", "이미 존재하는 회원 이름입니다.", UserNameDuplicatedException.class),
    U003("U003", "회원명은 필수 입력값입니다.", ExternalException.class),
    U004("U004", "이름은 최소 1자 이상, 최대 20자까지 입력 가능합니다.", ExternalException.class),
    U005("U005", "회원명에 공백은 포함될 수 없습니다.", ExternalException.class),
    U006("U006", "작성자가 아니므로 권한이 없습니다.", NotAuthorException.class),
    U007("U007", "비밀번호는 필수 입력값입니다.", ExternalException.class),
    U008("U008", "비밀번호는 최소 8자 이상, 최대 20자까지 입력 가능합니다.", ExternalException.class),

    B001("B001", "게시판 이름은 30자 이하여야 합니다.", BoardNameLengthException.class),
    B002("B002", "게시판 이름은 필수 입력값입니다.", BoardNameBlankException.class),
    B003("B003", "존재하지 않는 게시판입니다.", BoardNotFoundException.class),
    B004("B004", "태그 이름은 필수 입력값입니다.", TagNameNullException.class),
    B005("B005", "태그는 20자 이하여야 합니다.", TagNameLengthException.class),

    A001("A001", "게시글 제목은 30자 이하여야 합니다.", ArticleTitleLengthException.class),
    A002("A002", "게시글 제목은 필수 입력값입니다.", ArticleTitleBlankException.class),
    A003("A003", "게시글 내용은 필수 입력값입니다.", ArticleContentNullException.class),
    A004("A004", "게시글 내용 최대 2000자까지 입력 가능합니다.", ExternalException.class),
    A005("A005", "존재하지 않는 게시글입니다.", ArticleNotFoundException.class),

    C001("C001", "댓글은 200자 이하여야 합니다.", CommentLengthException.class),
    C002("C002", "댓글 내용은 필수 입력값입니다.", CommentBlankException.class),
    C003("C003", "존재하지 않는 댓글입니다.", CommentNotFoundException.class),

    S001("S001", "페이지의 시작 값은 음수가 될 수 없습니다.", InvalidPageStartException.class),
    S002("S002", "유효하지 않은 페이지 크기입니다. 유효한 크기 : 20 ~ 50", InvalidPageSizeException.class),
    S003("S003", "유효하지 않은 검색 타입입니다. board, article", InvalidSearchTypeException.class),
    S004("S004", "검색어는 공백일 수 없습니다.", SearchKeywordNullException.class),
    S005("S005", "검색어는 30자 이하여야 합니다.", LongSearchKeywordException.class),
    S006("S006", "검색어는 1자 이상이어야 합니다.", ShortSearchKeywordException.class),

    FCM("FCM", "FCM 서버를 초기화시키지 못했습니다.", FcmInitException.class),

    X001("X001", "정의되지 않은 에러", UndefinedException.class),
    ;

    private final String code;
    private final String message;
    private final Class<? extends MyboardException> classType;

    private static final Map<Class<? extends MyboardException>, ErrorType> CLASS_ERROR_TYPE_MAP = new HashMap<>();

    // class 초기화 블럭
    static {
        Arrays.stream(values())
                .filter(ErrorType -> ErrorType.isNotExternalException())
                .forEach(errorType -> CLASS_ERROR_TYPE_MAP.put(errorType.classType, errorType));
    }

    public static ErrorType of(Class<? extends MyboardException> classType) {
        if (classType.equals(ExternalException.class)) {
            throw new UnsupportedOperationException("클래스로 ErrorType을 생성할 수 없는 예외입니다.");
        }
        return CLASS_ERROR_TYPE_MAP.getOrDefault(classType, ErrorType.X001);
    }

    public static ErrorType of(String code) {
        return Arrays.stream(values())
                .parallel()
                .filter(errorType -> errorType.hasSameCode(code))
                .findAny()
                .orElse(ErrorType.X001);
    }

    public boolean hasSameCode(String code) {
        return Objects.equals(this.code, code);
    }

    public boolean isNotExternalException() {
        return !classType.equals(ExternalException.class);
    }

}
