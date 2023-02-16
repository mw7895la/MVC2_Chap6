package hello.login.web.session;

import hello.login.domain.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;

public class SessionManagerTest {

    SessionManager sessionManager = new SessionManager();

    @Test
    void sessionTest(){
        //세션 생성후 response까지 담기고 웹브라우저로 응답이 나갔다 보면돼.
        MockHttpServletResponse response = new MockHttpServletResponse();       //스프링이 도와준다.! 가짜로 request,response를 기능테스트 할 수 있게 해줌.
        Member member =new Member();
        sessionManager.createSession(member,response);      //HttpServletResponse를 넘겨줘야 되는데.. 인터페이스야.. 구현체가 있긴한데, 다 애매해..

        //여기서 부턴 웹브라우저의 요청이다.
        //요청에 응답쿠기가 저장되었는지 봐야한다.
        MockHttpServletRequest request = new MockHttpServletRequest();
        //웹브라우저에서 서버로 쿠키 전송
        request.setCookies(response.getCookies());      //"mySessionId=123414qwERI(SDFC1235QQW@!@#"


        //서버에서 확인 해 본다.(조회)
        Object result = sessionManager.getSession(request);     //요청으로 온것에서 쿠키 꺼내기
        Assertions.assertThat(result).isEqualTo(member);

        //세션 만료시키기 (삭제)
        sessionManager.expire(request);

        //잘 만료되었는지 확인
        Object expired = sessionManager.getSession(request);
        Assertions.assertThat(expired).isNull();
    }



}
