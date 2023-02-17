package hello.login;

import hello.login.web.filter.LogFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;


@Configuration
public class WebConfig {

    //FilterRegistrationBean<T extends Filter>  즉 인터페이스 Filter를 구현한것이라 보면된다.
    @Bean
    public FilterRegistrationBean logFilter(){
        //스프링 부트로 사용할때 필터 이렇게 등록하면 된다.  스프링부트는 WAS를 자기가 들고 띄우기 떄문에 WAS를 띄울때 필터를 같이 넣어준다.

        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new LogFilter());        //우리가 만든 로그 필터(web/filter/LogFilter.java)를 넣어주면 됨.
        filterFilterRegistrationBean.setOrder(1);                       //필터가 체인으로 여러개 들어갈수 있으니 순서 정해주자.
        filterFilterRegistrationBean.addUrlPatterns("/*");              // 어떤 url 패턴에다가 적용할지.
        return filterFilterRegistrationBean;
    }


}
