package hello.login.web.session;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 세션 관리해주는 기능
 */
@Component
public class SessionManager {

    public static final String SESSION_COOKIE_NAME = "mySessionId";
    //동시성 이슈가 있을 때는, 동시에 여러 쓰레드가 여기에 접근하게 되면 항상 ConcurrentHashMap을 써라
    private Map<String, Object> sessionStore = new ConcurrentHashMap<>();

    /**
     * 세션 생성
     * * 1.  SessionId 생성 (추정 불가능함 임의 값)
     * * 2.  세션 저장소에 SessionId와 보관할 값 저장
     * * 3.  SessionId로 응답 쿠키를 생성해서 클라이언트(웹브라우저)에 전달.
     */
    public void createSession(Object value, HttpServletResponse response){
        //SessionId 생성 (추정 불가능함 임의 값) 후 값을 세션에 저장
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId,value);      //sessionId와 사용자

        //쿠키 생성 //상수로 만드는 단축 키 !   Crtl + Alt + C
        Cookie mySessionCookie = new Cookie(SESSION_COOKIE_NAME, sessionId);//value 는 랜덤값
        response.addCookie(mySessionCookie);
    }

    /**
     * 세션 조회  ( 리턴 타입 Object )
     */
    public Object getSession(HttpServletRequest request) {
        /*//먼저 쿠키를 찾아라
        Cookie[] cookies = request.getCookies();
        if(cookies ==null){
            return null;
        }
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals(SESSION_COOKIE_NAME)){
                return sessionStore.get(cookie.getValue());     //사용자가 리턴된다.
            }
        }
        return null;*/      //findCookie() 메소드랑 같음.

        Cookie sessionCookie = findCookie(request,SESSION_COOKIE_NAME);
        if(sessionCookie == null){
            return null;
        }
        return sessionStore.get(sessionCookie.getValue());      //sessionCookie.getValue() 는 UUID 값. 그러면 해당하는  맴버 객체가 반환이 된다.
    }

    public Cookie findCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();        //요청 받은거에서 쿠키 꺼내라 !
        if(cookies==null){
            return null;
        }

        return Arrays.stream(cookies)
                 .filter(cookie ->cookie.getName().equals(cookieName))
                 .findAny()
                 .orElse(null);      // findAny()순서와 상관없이 빨리나온애 반환  findFirst() 먼저나온애 찾은거 반환 //없으면 null 반환
    }

    /**
     * 세션 만료
     */
    public void expire(HttpServletRequest request){
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if(sessionCookie!=null){
            sessionStore.remove(sessionCookie.getValue());      //세션 저장소의 해당 데이터를 통으로 한줄 지움.
        }
    }
}
