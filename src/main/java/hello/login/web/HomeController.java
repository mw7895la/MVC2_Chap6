package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;

    //@GetMapping("/")
    public String home() {
        return "home";
    }

    //새로운 홈 화면
    @GetMapping("/")            //쿠키 값은 String이지만 스프링은 자동 Converting을 해준다.      //required false면 쿠키가값이 없어도 된다.
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
}