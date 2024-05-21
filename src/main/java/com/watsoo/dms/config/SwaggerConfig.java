package com.watsoo.dms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

	@Bean
	Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
				.apis(RequestHandlerSelectors.basePackage("com.watsoo.dms")).build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Driver Monitoring System Apis").description("Apis for Dms.").version("1.0")
				.build();
	}

	@Bean
	OpenAPI openAPI() {
		return new OpenAPI().addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
				.info(new Info().title("Driver Monitoring System API's").description("Driver Monitoring System API's").version("0.0.1"))
				.components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
	}

	private SecurityScheme createAPIKeyScheme() {
		return new SecurityScheme().type(SecurityScheme.Type.HTTP).bearerFormat("JWT").scheme("bearer");
	}
}
