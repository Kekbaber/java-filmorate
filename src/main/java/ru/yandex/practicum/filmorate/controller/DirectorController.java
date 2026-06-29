package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.request.CreateDirectorRequest;
import ru.yandex.practicum.filmorate.dto.request.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService service;

    @GetMapping
    public List<Director> findAll() {
        log.info("GET /directors");
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable long id) {
        log.info("GET /directors/{}", id);
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director create(@Valid @RequestBody CreateDirectorRequest request) {
        log.info("POST /directors: {}", request.getName());
        return service.create(request);
    }

    @PutMapping
    public Director update(@Valid @RequestBody UpdateDirectorRequest request) {
        log.info("PUT /directors: {}", request.getId());
        return service.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        log.info("DELETE /directors/{}", id);
        service.delete(id);
    }
}
