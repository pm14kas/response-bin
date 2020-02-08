package site.hitry.responsebin

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.multipart.commons.CommonsMultipartResolver
import site.hitry.responsebin.entity.repository.BinRepository
import site.hitry.responsebin.entity.repository.RequestRepository
import site.hitry.responsebin.service.RequestServlet
import javax.servlet.http.HttpServlet


@Configuration
class ApplicationConfig {

    @Autowired
    lateinit var binRepository: BinRepository

    @Autowired
    lateinit var requestRepository: RequestRepository

    @Bean
    fun requestServlet(): ServletRegistrationBean<HttpServlet> {
        val servRegBean = ServletRegistrationBean<HttpServlet>()
        servRegBean.servlet = RequestServlet(this.binRepository, this.requestRepository, this.multipartResolver())
        servRegBean.addUrlMappings("/bin/request/*")
        servRegBean.setLoadOnStartup(1)

        return servRegBean
    }

    @Bean(name = ["multipartResolver"])
    fun multipartResolver(): CommonsMultipartResolver {
        val multipartResolver = CommonsMultipartResolver()
        multipartResolver.setMaxUploadSize(8192)

        return multipartResolver
    }
}