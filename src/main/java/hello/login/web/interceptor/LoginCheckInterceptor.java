package hello.login.web.interceptor;

import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
    /**
     * 생각해보면 로그인 체크는  preHandle만 있으면 된다.
     */

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        log.info("인증 체크 인터셉터 실행 ={}", requestURI);

        HttpSession session = request.getSession();

        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
            log.info("미인증 사용자 요청");

            //로그인으로redirect
            response.sendRedirect("/login?redirectURL=" + requestURI);
            return false;       //여기서 끝내겠다. 더이상 진행 안하고.
        }
        //근데 LoginCheckFilter에서 화이트 리스트 이런거 했었는데..?  스프링 인터셉터에선 로그인 필터체크시 그런 복잡한 작업이 필요없다. WebConfig에 빈으로 등록할때 작업을 해주자.

        return true;        //컨트롤러로 가겠다.
    }
}
