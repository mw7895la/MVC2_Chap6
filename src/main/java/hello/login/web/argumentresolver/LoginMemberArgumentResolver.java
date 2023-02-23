package hello.login.web.argumentresolver;

import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    //ArgumentResolver 의 supportsParameter() 를 호출해서 해당 파라미터를 지원하는지 체크하고,
    //지원하면 resolveArgument() 를 호출해서 실제 객체를 생성한다. 그리고 이렇게 생성된 객체가 컨트롤러 호출시 넘어가는 것이다.
    @Override
    public boolean supportsParameter(MethodParameter parameter) {       //캐시를 사용해서 처음 호출되고 이후로는 호출 안된다.
        log.info("supportsParameter 실행");
        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);//이 parameter 정보에서 @Login어노테이션이 있는지 물어보는 것.
        //컨트롤러 호출 전에 파라미터에 @Login가 들어있는지 물어보는 것.
        boolean hasMemberType = Member.class.isAssignableFrom(parameter.getParameterType());
        log.info("parameter.getParameterType()  ={}", parameter.getParameterType());
        //Item.class.isAssignableFrom(clazz); 의 의미는?? - clazz가 Item 클래스(인터페이스)를 통해 구현한 것인지 확인하는것.
        //getParamterTpye()이면 Member 클래스다.

        return hasLoginAnnotation && hasMemberType;     //true면 아래 resolveArgument가 실행된다.       //false면 여기서 끝남.
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        log.info("resolve Argument 실행");
        HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();
        HttpSession session = request.getSession(false);

        if(session == null){            //세션이 null이면 homeLoginV3ArgumentResolver 의 파라미터 Member타입의 loginMember에 null 넣어버린다.
            return null;
        }

        return session.getAttribute(SessionConst.LOGIN_MEMBER);     //세션이 있으면  homeLoginV3ArgumentResolver()의 파라미터로, 호출된 Member가 넘어간다.

    }
}
