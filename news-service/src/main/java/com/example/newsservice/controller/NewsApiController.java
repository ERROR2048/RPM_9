
package com.example.newsservice.controller;

import com.example.newsservice.model.NewsModel;
import com.example.newsservice.service.NewsService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/news")
@Tag(name = "Новости", description = "Управление новостями")
@SecurityRequirement(name = "bearerAuth")
public class NewsApiController {
    private final NewsService service;

    public NewsApiController(NewsService service) {
        this.service = service;
    }

    @GetMapping
    public List<NewsModel> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")    @Operation(summary = "Получить новость по ID")
    public NewsModel getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @GetMapping("/search")    @Operation(summary = "Поиск новостей по ключевому слову")
    public List<NewsModel> search(@RequestParam String keyword) {
        return service.search(keyword);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NewsModel create(@Valid @RequestBody NewsModel item) {
        return service.save(item);
    }

    @PutMapping("/{id}")     @Operation(summary = "Обновить новость")
    public NewsModel update(@PathVariable Integer id, @Valid @RequestBody NewsModel item) {
        return service.update(id, item);
    }

    @DeleteMapping("/{id}")    @Operation(summary = "Удалить новость")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
