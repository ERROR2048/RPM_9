package com.example.dealerservice.controller;

import com.example.dealerservice.model.DealerShipModel;
import com.example.dealerservice.service.DealerShipService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class DealerShipApiControllerSecurityTest {


    @Configuration
    @EnableMethodSecurity
    static class MethodSecurityOnly {
        @Bean
        DealerShipService service() {
            return mock(DealerShipService.class);
        }

        @Bean
        DealerShipApiController controller(DealerShipService service) {
            return new DealerShipApiController(service);
        }
    }

    private AnnotationConfigApplicationContext context;

    private DealerShipApiController controllerAs(String role) {
        context = new AnnotationConfigApplicationContext(MethodSecurityOnly.class);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user", null, List.of(new SimpleGrantedAuthority(role))));
        return context.getBean(DealerShipApiController.class);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        if (context != null) {
            context.close();
        }
    }

    @Test
    void customerCanReadButCannotWrite() {
        DealerShipApiController controller = controllerAs("ROLE_CUSTOMER");

        assertDoesNotThrow(controller::getAll);
        assertThrows(AccessDeniedException.class,
                () -> controller.create(new DealerShipModel()));
        assertThrows(AccessDeniedException.class,
                () -> controller.update(1, new DealerShipModel()));
        assertThrows(AccessDeniedException.class, () -> controller.delete(1));
    }

    @Test
    void managerCanCreateAndUpdateButNotDelete() {
        DealerShipApiController controller = controllerAs("ROLE_MANAGER");

        assertDoesNotThrow(() -> controller.create(new DealerShipModel()));
        assertDoesNotThrow(() -> controller.update(1, new DealerShipModel()));
        assertThrows(AccessDeniedException.class, () -> controller.delete(1));
    }

    @Test
    void adminCanDelete() {
        DealerShipApiController controller = controllerAs("ROLE_ADMIN");

        assertDoesNotThrow(() -> controller.delete(1));
    }
}
