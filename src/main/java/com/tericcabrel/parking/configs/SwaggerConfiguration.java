package com.tericcabrel.parking.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    public static final String TAG_CAR_RECHARGE = "Car Recharge";
    public static final String TAG_CAR_TYPE = "Car's Type";
    public static final String TAG_CUSTOMER = "Customer";
    public static final String TAG_PARKING_SLOT = "Parking Slot";
    public static final String TAG_ROLE = "Role";
    public static final String TAG_USER = "User";

    @Bean
    public Docket api(SwaggerProperties swaggerProperties) {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.tericcabrel.parking.controllers"))
                .paths(PathSelectors.regex("/.*"))
                .build()
                .apiInfo(apiEndPointsInfo(swaggerProperties))
                .produces(DEFAULT_PRODUCES_AND_CONSUMES)
                .consumes(DEFAULT_PRODUCES_AND_CONSUMES)
                .pathMapping("/")
                .protocols(Collections.singleton("HTTP"))
                .useDefaultResponseMessages(Boolean.valueOf(swaggerProperties.getUseDefaultResponseMessages()))
                .tags(new Tag(TAG_CAR_RECHARGE, "Operations pertaining to car's recharge creation, update, fetch and delete"))
                .tags(new Tag(TAG_CAR_TYPE, "Operations pertaining to car's type creation, update, fetch and delete"))
                .tags(new Tag(TAG_CUSTOMER, "Operations pertaining to customer creation, update, fetch and delete"))
                .tags(new Tag(TAG_PARKING_SLOT, "Operations pertaining to parking's slot creation, update, fetch and delete"))
                .tags(new Tag(TAG_ROLE, "Operations pertaining to role creation, update, assign, revoke, fetch and delete"))
                .tags(new Tag(TAG_USER, "Operations pertaining to registration, authentication"));
    }

    private ApiInfo apiEndPointsInfo(SwaggerProperties swaggerProperties) {
        return new ApiInfoBuilder().title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
                .contact(new Contact("Eric Cabrel TIOGO", "https://tericcabrel.com", "tericcabrel@yahoo.com"))
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .version("1.0")
                .build();
    }

    /**
     * Disable Try it out button
     *
     * @return instance of UiConfiguration
     */
    @Bean
    public UiConfiguration tryItOutConfig() {
        final String[] methodsWithTryItOutButton = { "" };
        return UiConfigurationBuilder.builder().supportedSubmitMethods(methodsWithTryItOutButton).build();
    }

    private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES = new HashSet<>(Collections.singletonList("application/json"));
}
