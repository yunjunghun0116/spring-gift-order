package gift.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        var info = new Info()
                .version("v1.0")
                .title("카카오테크캠퍼스-선물하기")
                .description("프론트엔드와 협업을 위한 API 문서");
        return new OpenAPI().info(info);
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            var excludePaths = Set.of("/api/members/oauth/kakao", "/api/members/login", "/api/members/register", "/api/kakao/get-oauth");
            openApi.getPaths().forEach((path, pathItem) -> {
                if (!excludePaths.contains(path)) {
                    for (var operation : pathItem.readOperations()) {
                        var header = new HeaderParameter()
                                .name("Authorization")
                                .required(Boolean.TRUE);
                        operation.addParametersItem(header);
                    }
                }
            });
        };
    }
}
