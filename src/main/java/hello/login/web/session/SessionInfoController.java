package hello.login.web.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

@RestController
@Slf4j
public class SessionInfoController {


    //세션 정보 확인하기
    @GetMapping("/session-info")
    public String sessionInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session==null){
            return "세션이 없습니다.";
        }

        session.getAttributeNames().asIterator()
                .forEachRemaining(name -> log.info("session name={}, value={}", name, session.getAttribute(name)));

        //세션이 기본으로 제공하는 것들
        log.info("sessionId={}", session.getId());
        log.info("getMaxInactiveInterval={}", session.getMaxInactiveInterval());
        log.info("createTIme={}", new Date(session.getCreationTime()));     //세션 만든시점
        log.info("lastAccessedTime ={}", new Date(session.getLastAccessedTime()));      //세션 마지막 접근시점.
        log.info("isNew={}", session.isNew());

        return "세션 출력";

    }
}
