package hello.login.domain.member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class MemberRepository {
    //원래 Core 프로젝트 처럼 인터페이스로 빼서 오버라이딩 하는게 좋지만 여긴 그게 주가 아니니 그냥 ㄱ

    private static Map<Long, Member> store = new HashMap<>();       //static 사용
    private static long sequence=0L;

    public Member save(Member member){
        member.setId(++sequence);
        log.info("save member={}",member);
        store.put(member.getId(), member);
        return member;

    }

    public Member findById(Long id) {
        return store.get(id);
    }

    //로그인 id로 찾는것도 필요   //Optional은 못찾을수도 있으니..
    public Optional<Member> findByLoginId(String loginId) {
        List<Member>all=findAll();

        for (Member m : all) {
            if(m.getLoginId().equals(loginId)){
                return Optional.of(m);
            }
        }
        return Optional.empty();        //Optional 껍데기 통안에 객체가 있을 수도 있고 없을 수도 있다.

        //return findAll().stream().filter(m->m.getLoginId().equals(loginId)).findFirst(); 위에랑 똑같음. 리스트를 stream이란걸로 바꿔 (루프돈다고 생각) m이 해당 조건을 만족하면 다음단계로 넘어가고 만족못하면 버려져. findFirst() 먼저 나온애를 받아다가 반환해버리는것.
    }

    public List<Member> findAll(){
        return new ArrayList<>(store.values());        //store의 value들이 전부다 리스트로 변환
    }

    public void clearStore(){
        store.clear();
    }
}
