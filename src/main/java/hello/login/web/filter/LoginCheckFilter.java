package hello.login.web.filter;

import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class LoginCheckFilter implements Filter {
    private static final String[] whitelist = {"/", "/members/add", "/login", "/logout", "/css/*"};     //로그인 안됬다고 css호출 안되면 안됨..

    /**
     * init()과 destroy()는 Filter 인터페이스에서 default로 되어있기 때문에 굳이 구현을 안해도 된다. 그래서 지웠다.
     */


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        HttpServletResponse httpResponse = (HttpServletResponse) response;


        try{
            log.info("인증 체크 필터 시작 {} ", requestURI);
            //체크 로직을 만들자.
            if(isLoginCheckPath(requestURI)){
                //whitelist가 아니라면-> 필터를 거쳐야 되는 URI라면 !
                log.info("인증 체크 로직 실행 ={}", requestURI);
                HttpSession session = httpRequest.getSession(false);
                if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
                    //클라이언트가 만약 요청시 해당 JSESSIONID로 된 value 즉, SessionConst.LOGIN_MEMBER로 된 멤버 객체가 없다면 미인증 사용자라는 것.
                    //그러면 로그인 페이지로 쫓아내야지
                    log.info("미인증 사용자 요청 = {}", requestURI);
                    httpResponse.sendRedirect("/login?redirectURL="+requestURI);
                    //로그인 페이지로 보낼건데. 그다음에 로그인을 했어, 그러면 다시 이 페이지로 오도록 한 것.
                    //처음에 localhost:8080/items로 들어갔는데 미인증 사용자라서 localhost:8080/login 페이지로 쫓겨났다. 이제 로그인을 실제로 했을 때 내가 처음에 갔던 페이지로(localhost:8080/items) 가도록 한 것이 위의 sendRedirect()

                    return; //sendRedirect후 return; 했다  -> 다음 서블릿이나 컨트롤러 호출 안하겠다는 것. return을 하더라도 아래 finally는 호출됨.
                }
            }
            //체크 해야되는 경로면 하고, 화이트 리스트면 다음 필터로 넘어가면 된다.
            chain.doFilter(request, response);
        }catch(Exception e){
            throw e;        //예외 로깅 가능하지만, 톰캣까지 예외를 보내줘야 한다.
        }finally{
            log.info("인증 체크 필터 종료 {}",requestURI);
        }
    }

    /**
     * 화이트 리스트의 경우 인층 체크를 하지 말자.
     */
                //로그인 체크 하는 경로인지 확인하는것이기 때문에 !로 (그래야 return false로 넘어간다. 그럼 체크를 안하지)
    private boolean isLoginCheckPath(String requestURI) {
        //whitelist는 배열이다 그래서 requestURI랑 1:N 비교해봐야지.
        return !PatternMatchUtils.simpleMatch(whitelist,requestURI);     //whitelist와 requestURI가 단순하게 패턴이 매칭이 되는가!
    }

}
