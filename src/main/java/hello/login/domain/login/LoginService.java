package hello.login.domain.login;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;

    /**
     * null 이면 로그인 실패
     */
    public Member login(String loginId, String password) {
        /*Optional<Member> findMemberOptional = memberRepository.findByLoginId(loginId);

        Member member = findMemberOptional.get();   //Optional안에 있으면 꺼내지고 없으면 예외가 터짐

        if (member.getPassword().equals(password)) {
            return member;
        }else{
            return null;
        }*/

        Optional<Member> byLoginId = memberRepository.findByLoginId(loginId);
        return byLoginId.filter(m -> m.getPassword().equals(password)).orElse(null);       //optional안에 들어있는 member의 패스워드가 같은가 있으면 반환하고 없으면 null반환
    }
}
