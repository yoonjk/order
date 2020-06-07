package com.ibm.lab.order.config;

import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
						.select()
						.apis(RequestHandlerSelectors.any())
						.paths(PathSelectors.ant("/api/**"))
						.build()
						.apiInfo(apiInfo());
//		                .useDefaultResponseMessages(false)
//		                .globalResponseMessage(RequestMethod.GET,
//		                        newArrayList(new ResponseMessageBuilder()
//		                                        .code(500)
//		                                        .message("Internal Server Error")
//		                                        .responseModel(new ModelRef("Error"))
//		                                        .build(),
//		                                new ResponseMessageBuilder()
//		                                        .code(400)
//		                                        .message("Bad Request")
//		                                        .build(),
//		                                new ResponseMessageBuilder()
//		                                        .code(404)
//		                                        .message("Not Found")
//		                                        .build()));
	}


		
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Spring API Documentation")
                .description("Order API에 대한 문서입니다")
                .license("korea lab").licenseUrl("http://com.ibm.com/kr").version("1").build();
    }	
	

}
