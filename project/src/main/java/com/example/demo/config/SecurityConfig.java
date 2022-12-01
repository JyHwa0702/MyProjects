package com.example.demo.config;

import com.example.demo.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    @Bean
    public BCryptPasswordEncoder encodePwd(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf().disable()
                .headers().frameOptions().disable()
                //h2-console 화면을 사용하기 위해 해당 옵션들을 disable

                .and()
                .authorizeRequests()
                //URL별 권한 관리를 설정하는 옵션의 시작점
                //authorizeRequests가 선언되어야만 antMatchers옵션을 사용가능.
                .antMatchers("/","/Savory-gh-pages/**").permitAll()
                .antMatchers("/user/**").permitAll()
                //권한 관리 대상을 지정하는 옵션,permit 권한제한 없음.
                .anyRequest().hasRole("USER")
                //설정된 값을 이외 나머지 URL들을 나타냄.
                //authenticated() 메서드를 통해서 나머지 URL들은 모두 인증된 사용자에게만 허용

                .and()
                .formLogin()
                    .loginPage("/user/login")
                    .usernameParameter("id")
                    .passwordParameter("password")
                    .loginProcessingUrl("/")
                    .defaultSuccessUrl("/")
                    .failureUrl("/")

                .and()
                .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")

                .and()
                .oauth2Login().userInfoEndpoint().userService(customOAuth2UserService);
                //OAuth2 로그인 기능에 대한 여러 설정의 진입점.
                //userInfoEndpoint : 로그인이 성공된 이후 사용자 정보를 가져올때 설정담당.
                //userSErvice : 소셜 로그인 성공시 후속조치 진행할 UserService 인터페이스 구현체등록
                //사용자 정보를 가져온 정보를 기반으로 추가적으로 진행하고자 하는 기능들을 명시가능.


        return http.build();


    }
}