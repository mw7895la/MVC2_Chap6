package hello.login;

import hello.login.web.argumentresolver.LoginMemberArgumentResolver;
import hello.login.web.filter.LogFilter;
import hello.login.web.filter.LoginCheckFilter;
import hello.login.web.interceptor.LogInterceptor;
import hello.login.web.interceptor.LoginCheckInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.util.List;


@Configuration      //스프링 인터셉터를 쓰기위해 implements한다.
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())       //  excludePathPatterns  는 모든 경로중에서 해당 경로들은 인터셉터 호출 안시킬거야.
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error");

        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/members/add", "/login", "/logout", "/css/**", "/*.ico", "/error");  //모든 경로에서 일부는 exclude한다. 즉 일부는 로그인체크 인터셉터가 적용되지 않는다.
    }

    //FilterRegistrationBean<T extends Filter>  즉 인터페이스 Filter를 구현한것이라 보면된다.
    //@Bean
    public FilterRegistrationBean logFilter(){
        //스프링 부트로 사용할때 필터 이렇게 등록하면 된다.  스프링부트는 WAS를 자기가 들고 띄우기 떄문에 WAS를 띄울때 필터를 같이 넣어준다.

        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new LogFilter());        //우리가 만든 로그 필터(web/filter/LogFilter.java)를 넣어주면 됨.
        filterFilterRegistrationBean.setOrder(1);                       //필터가 체인으로 여러개 들어갈수 있으니 순서 정해주자.
        filterFilterRegistrationBean.addUrlPatterns("/*");              // 어떤 url 패턴에다가 적용할지.
        return filterFilterRegistrationBean;
    }



    //@Bean
    public FilterRegistrationBean loginCheckFilter(){
        //스프링 부트로 사용할때 필터 이렇게 등록하면 된다.  스프링부트는 WAS를 자기가 들고 띄우기 떄문에 WAS를 띄울때 필터를 같이 넣어준다.

        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new LoginCheckFilter());             //우리가 만든 필터(web/filter/LoginCheckFilter.java)
        filterFilterRegistrationBean.setOrder(2);                                //필터가 체인으로 여러개 들어갈수 있으니 순서 정해주자.
        filterFilterRegistrationBean.addUrlPatterns("/*");                       // 어떤 url 패턴에다가 적용할지. 전체를 적용하지만 LoginCheckFilter에서 적용시킬 화이트 리스트 빼고는 나머진 로그인 인증 체크 다 할꺼다.
        return filterFilterRegistrationBean;
    }
}
