package com.example.dealerservice.controller;

import com.example.dealerservice.model.DealerShipModel;
import com.example.dealerservice.service.DealerShipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dealers")
@Tag(name = "Дилеры",
        description = "CRUD операции с дилерскими центрами. Чтение доступно любому "
                + "авторизованному пользователю, изменение — ролям ADMIN и MANAGER, "
                + "удаление — только ADMIN.")
@SecurityRequirement(name = "bearerAuth")
public class DealerShipApiController {

    private final DealerShipService service;

    public DealerShipApiController(DealerShipService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Получить всех дилеров",
            description = "Возвращает список всех дилерских центров")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Найдено"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "503", description = "Service Unavailable"),
            @ApiResponse(responseCode = "504", description = "Gateway Timeout")
    })
    public List<DealerShipModel> getAll() {
        return service.getAll();
    }

    @GetMapping("/page")
    @Operation(summary = "Получить дилеров постранично",
            description = "Возвращает страницу списка дилеров")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Найдено"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public List<DealerShipModel> getPage(
            @Parameter(description = "Номер страницы, начиная с 0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы")
            @RequestParam(defaultValue = "10") int size) {
        return service.getPage(Math.max(page, 0), Math.min(Math.max(size, 1), 100));
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск дилеров",
            description = "Ищет дилеров по идентификатору, названию или городу")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Найдено"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public List<DealerShipModel> search(
            @Parameter(description = "Поисковая строка")
            @RequestParam(required = false, defaultValue = "") String keyword) {
        return service.search(keyword);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить дилера по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Найдено"),
            @ApiResponse(responseCode = "404", description = "Дилер не найден"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public DealerShipModel getById(
            @Parameter(description = "Идентификатор дилера")
            @PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Создать дилера",
            description = "Создаёт нового дилера. Доступно ролям ADMIN и MANAGER")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Создано"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public DealerShipModel create(@Valid @RequestBody DealerShipModel item) {
        return service.create(item);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Обновить дилера",
            description = "Обновляет данные дилера. Доступно ролям ADMIN и MANAGER")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Обновлено"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Дилер не найден"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public DealerShipModel update(
            @Parameter(description = "Идентификатор дилера")
            @PathVariable int id,
            @Valid @RequestBody DealerShipModel item) {
        return service.update(id, item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить дилера",
            description = "Удаляет дилера. Доступно только роли ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Удалено"),
            @ApiResponse(responseCode = "404", description = "Дилер не найден"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public void delete(
            @Parameter(description = "Идентификатор дилера")
            @PathVariable int id) {
        service.delete(id);
    }
}
