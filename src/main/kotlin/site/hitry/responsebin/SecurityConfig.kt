package site.hitry.responsebin

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import site.hitry.responsebin.service.UserLoginDetailsService

@Configuration
@EnableWebSecurity
class SecurityConfig (
    var userDetailsService : UserLoginDetailsService
) : WebSecurityConfigurerAdapter() {
    @Bean
    public fun passwordEncoder() : BCryptPasswordEncoder
    {
        return BCryptPasswordEncoder();
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
            .authorizeRequests()
            .antMatchers("/resources/**", "/registration", "/register", "/", "/css/**", "/js/**", "/bin/request/**")
                .permitAll()
            .anyRequest()
                .authenticated()
            .and()
            .formLogin()
                .loginPage("/login"
                ).permitAll()
            .and()
            .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            .and().
                cors()
            .and().
                csrf().disable()
        ;
    }
}