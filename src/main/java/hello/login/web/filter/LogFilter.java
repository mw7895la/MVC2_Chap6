package hello.login.web.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("log filter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //ServletRequest 은 HttpServletRequest의 부모인데 기능이 별로 없어서  다운 캐스팅 해줘야 한다.
        log.info("log filter doFilter");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        //모든 사용자의 요청 uri 남겨보자
        String requestURI = httpRequest.getRequestURI();
        //사용자들을 구분위해 UUID를 남기자
        String uuid =UUID.randomUUID().toString();

        try{
            log.info("Request [{}] [{}]",uuid,requestURI);
            //여기가 중요함
            chain.doFilter(request,response);   //일단 필터를 적용하여 정보 출력하고 다음 필터가 또 있으면 거기가 출력되고 없으면 서블릿으로 넘어간다.
        }catch(Exception e){
            throw e;
        }finally{
            //try의 로직이 다 끝나고 여기가 출력.
            log.info("Response [{}] [{}]", uuid, requestURI);
        }
    }

    @Override
    public void destroy() {
        log.info("log filter destroy");
    }
}
