package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }

    //실제 로그인 처리 되는 로직
    //@PostMapping("/login")
    public String login(@Validated @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        log.info("loginMameber ={}", loginMember);
        //특정 필드의 문제가 아님.  reject()하면 글로벌 오류.  글로벌 오류는 자바코드로 작성하는게 좋다
        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        //로그인 성공 처리 (cookie)

        //쿠키 생성     //쿠키에 시간 정보를 주지 않으면 세션 쿠키(브라우저 종료시 모두 종료)  // 시간 정보(만료 날짜) 주면 영속 쿠키
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));//2번 파라미터가 String , 시퀀스를 넣어줌

        //쿠키를 생성 후, 서버에서 HTTP응답 보낼때 response에 넣어서 같이 보내줘
        response.addCookie(idCookie);

        return "redirect:/";
    }

    /**
     * 세션을 이용한 로그인 처리
     */
    //@PostMapping("/login")
    public String loginV2(@Validated @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        log.info("loginMameber ={}", loginMember);
        //특정 필드의 문제가 아님.  reject()하면 글로벌 오류.  글로벌 오류는 자바코드로 작성하는게 좋다
        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        //로그인 성공 처리
        //세션 관리자를 통해 세션을 생성하고 회원 데이터를 보관
        sessionManager.createSession(loginMember, response);


        return "redirect:/";
    }

    /**
     * HttpServlet이 제공하는 세션으로 해보자.
     */
    @PostMapping("/login")
    public String loginV3(@Validated @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        log.info("loginMameber ={}", loginMember);
        //특정 필드의 문제가 아님.  reject()하면 글로벌 오류.  글로벌 오류는 자바코드로 작성하는게 좋다
        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        //로그인 성공 처리
        //HttpSession은 request가 필요하다.
        //request.getSession(); ------- 세션이 있으면 있던 세션 반환, 없으면 신규 세션을 생성.
        HttpSession session = request.getSession();     //default가 getSession(true)다
        //세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER,loginMember);        //서블릿은 통일된 인터페이스를 제공한다. setAttribute 뭔가 보관해 !

        return "redirect:/";
    }

    //@PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        //쿠키를 지우는 방법 시간을 지워라

        /*Cookie cookie = new Cookie("memberId",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie); 을 추출함*/

        expireCookie(response,"memberId");
        return "redirect:/";
    }

    /**
     * 로그아웃시 세션이 만료됨.
     */
    //@PostMapping("/logout")
    public String logoutV2(HttpServletRequest request) {
        sessionManager.expire(request);
        return "redirect:/";
    }

    /**
     * 서블릿 세션을 이용한 로그아웃.
     */
    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest request) {
        //false로 했다.세션이 있으면 반환하고 없으면 새로 만들지말고 null을 반환해라
        HttpSession session =request.getSession(false);
        if(session!=null){
            //세션이랑 그 안에 있는 데이터까지 다 날라간다.
            session.invalidate();
        }
        return "redirect:/";
    }

    private static void expireCookie(HttpServletResponse response,String memberId) {
        Cookie cookie = new Cookie(memberId, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
