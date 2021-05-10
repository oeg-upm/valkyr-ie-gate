/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.oeg.valkyrie.web;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 *
 * @author Pablo
 */
@Configuration
@EnableSwagger2
public class SwaggerConf {
    
    	@Bean
	    public Docket api() {
	 		return new Docket(DocumentationType.SWAGGER_2)
	          .select()
	          .apis(RequestHandlerSelectors.basePackage("es.upm.oeg.valkyrie.web"))         
	          .paths(PathSelectors.any())
	          .build()
                  .useDefaultResponseMessages(false)
	          .apiInfo(apiEndPointsInfo());
	    }
	 	private ApiInfo apiEndPointsInfo() {
	        return new ApiInfoBuilder().title("VALKYR-IE REST API")
	            .description("This is the documentation for the VALKYR-IE REST API of NER process in LYNX project")
	            .contact(new Contact("Pablo Calleja Ibáñez & Raúl García Castro", "http://www.oeg-upm.net/", "pcalleja@fi.upm.es rgarcia@fi.upm.es"))
	            .license("Apache 2.0")
	            .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
	            .version("1.0.0")
	            .build();
	    }
    
}
