package com.deliverytech.delivery.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Esta classe configura a interface do Swagger UI para que ela entenda
// e exiba a opção de autenticação via Bearer Token (JWT).
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                // Adiciona o requisito de segurança a todos os endpoints
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                // Define o esquema de segurança que será usado
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP) // Tipo HTTP
                                                .scheme("bearer") // Esquema Bearer
                                                .bearerFormat("JWT") // Formato JWT
                                )
                )
                .info(new Info().title("Delivery API").version("v1.0.0").description(
                        "API para o sistema de Delivery. Para testar os endpoints protegidos, " +
                        "primeiro use o /api/auth/register para criar um usuário ADMIN, " +
                        "copie o token da resposta e cole-o no botão 'Authorize' no formato 'Bearer <token>'."
                ));
    }
}
