package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;

    //@GetMapping("/")
    public String home() {
        return "home";
    }

    //새로운 홈 화면
    //@GetMapping("/")            //쿠키 값은 String이지만 스프링은 자동 Converting을 해준다.      //required false면 쿠키가값이 없어도 된다.
    public String homeLogin(@CookieValue(name="memberId", required=false) Long memberId, Model model){
        /**
         * 이제 로그인이 되면 상품관리를 들어갈 수 있는 홈 화면을 보여줘야 한다.
         * 로그인 안한 사용자도 일단 홈 화면은 들어올 수 있다.
         */
        //1. 먼저 쿠키를 받는다
        //쿠키가 있는 사용자
        Member loginMember = memberRepository.findById(memberId);

        if (memberId == null) {
            return "home";
        }
        model.addAttribute("member", loginMember);
        return "loginHome";         //사용자 전용 화면
    }


    /**
     * 위에 homeLogin은 쿠키로 받는것. 아래는 세션이다.
     */
    //@GetMapping("/")
    public String homeLoginV2(HttpServletRequest request, Model model){

        //세션 관리자에 저장된 회원 정보 조회
        Member member = (Member)sessionManager.getSession(request);

        if (member == null) {
            return "home";
        }
        model.addAttribute("member", member);
        return "loginHome";         //사용자 전용 화면
    }


    /**
     * 서블릿 세션을 이용한 홈로그인
     */
    //@GetMapping("/")
    public String homeLoginV3(HttpServletRequest request, Model model){
        //처음 들어온 사용자도. 홈 화면에(회원가입 로그인) 들어왔는데 세션이 만들어져버려.. 그래서 일단은 false ..
        //세션은 메모리를 쓰기 때문에 꼭 필요할때만 생성.
        HttpSession session = request.getSession(false);

        if(session==null){
            return "home";
        }
        Member loginMember = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);       //해당 session key로 얻은  member 객체를 캐스팅

        //세션에 회원 데이터가 없으면 home
        if(loginMember==null){
            return "home";
        }

        //세션이 유지되면 로그인으로 이동

        model.addAttribute("member", loginMember);
        return "loginHome";         //사용자 전용 화면
    }

    @GetMapping("/")
    public String homeLoginV3Spring(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required=false)Member loginMember, Model model){
        /**
         * 아래를 이 한줄로 줄인다. @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required=false)Member loginMember
         * 이미 로그인된 사용자를 찾을 때 이렇게 사용한다. 이 기능은 false로 되어있으니 세션을 새로 생성하지 않는다.
         */
        /*HttpSession session = request.getSession(false);

        if(session==null){
            return "home";
        }
        Member loginMember = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);*/



        //세션에 회원 데이터가 없으면 home
        if(loginMember==null){
            return "home";
        }

        //세션이 유지되면 로그인으로 이동

        model.addAttribute("member", loginMember);
        return "loginHome";         //사용자 전용 화면
    }
}