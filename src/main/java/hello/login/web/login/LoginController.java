package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }

    //실제 로그인 처리 되는 로직
    @PostMapping("/login")
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

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        //쿠키를 지우는 방법 시간을 지워라

        /*Cookie cookie = new Cookie("memberId",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie); 을 추출함*/

        expireCookie(response,"memberId");
        return "redirect:/";
    }

    private static void expireCookie(HttpServletResponse response,String memberId) {
        Cookie cookie = new Cookie(memberId, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
