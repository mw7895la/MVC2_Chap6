package hello.login.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {


    public static final String LOG_ID = "logId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String uuid = UUID.randomUUID().toString();     //reponse 할때 uuid를 afterCompletion으로 넘겨야되는데..?

        request.setAttribute(LOG_ID, uuid);        //일단 request.setAttribute 한다.

        //@RequestMapping @Controller이런걸 사용하면 HandlerMethod
        //ResourceHttpRequestHandler : 정적 리소스가 호출 되는 경우.

        //일반적인 @RequestMapping,@Controller 이런건 HandlerMethod 타입으로 handler가 넘어온다.
        if(handler instanceof HandlerMethod){
            //@RequestMapping은 잘 생각해보면 메소드 단위로 동작한다. (** Servlet Project 참고)
            //요청이 들어오면 저장해 둔 목록에서 요청 조건에 맞는 HandlerMethod를 참조해서 매핑되는 메소드라면 true다.

            HandlerMethod hm = (HandlerMethod) handler;//호출할 컨트롤러 메서드의 모든 정보가 포함되어있다.

            log.info("handler ==={}",hm.getMethod().getName());
        }

        log.info("REQUEST [{}] [{}] [{}]",uuid,requestURI,handler);
        return true;        //true면 handlerAdapter후에 handler(컨트롤러)가 실제 호출이 된다. // false면 여기서 끝난다.
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("PostHandle ={}", modelAndView);
        //컨트롤러에서 예외가 발생하면 postHandle은 호출 안됨.
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();

        Object logId = request.getAttribute(LOG_ID);   //preHandle에서 set한걸 get한다.  HttpServletRequest는 하나의 사용자에 대해서 갔다가 오는것 까지. 보장이 되니까.
        log.info("REQUEST [{}] [{}] [{}]",logId,requestURI,handler);

        //예외가 터진 경우
        if(ex != null){
            log.error("afterCompletion error !!",ex);
        }
    }
}
