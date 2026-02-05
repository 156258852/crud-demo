package com.example.demo.controller;

import com.example.demo.service.PetStoreService;
import com.example.demo.client.petstore.model.Pet;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/petstore")
@RequiredArgsConstructor
@Tag(name = "PetStore BFF", description = "调用 PetStore 下游 API 的 BFF 接口")
public class PetStoreController {

    private final PetStoreService petStoreService;

    @Operation(
        summary = "Get pet by ID",
        description = "Retrieve a specific pet from the PetStore API by its ID"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved pet",
        content = @Content(schema = @Schema(implementation = Pet.class))
    )
    @ApiResponse(responseCode = "404", description = "Pet not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/pets/{id}")
    public Mono<Pet> getPetById(
            @Parameter(description = "Pet ID", required = true)
            @PathVariable Long id) {
        return petStoreService.getPetById(id);
    }

    @Operation(
        summary = "Find pets by status",
        description = "Retrieve multiple pets from the PetStore API filtered by status"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved pets",
        content = @Content(schema = @Schema(implementation = Pet.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid status value")
    @GetMapping("/pets")
    public Flux<Pet> getPetsByStatus(
            @Parameter(description = "Pet status (available, pending, sold)", required = false)
            @RequestParam(defaultValue = "available") String status) {
        return petStoreService.getPetsByStatus(status);
    }

    @Operation(
        summary = "Add a new pet",
        description = "Add a new pet to the PetStore"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully created pet",
        content = @Content(schema = @Schema(implementation = Pet.class))
    )
    @ApiResponse(responseCode = "405", description = "Invalid input")
    @PostMapping("/pets")
    public Mono<Pet> addPet(
            @Parameter(description = "Pet object to add", required = true)
            @Valid @RequestBody Pet pet) {
        return petStoreService.addPet(pet);
    }

    @Operation(
        summary = "Update an existing pet",
        description = "Update information for an existing pet in the PetStore"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully updated pet",
        content = @Content(schema = @Schema(implementation = Pet.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid ID supplied")
    @ApiResponse(responseCode = "404", description = "Pet not found")
    @PutMapping("/pets")
    public Mono<Pet> updatePet(
            @Parameter(description = "Pet object with updated information", required = true)
            @Valid @RequestBody Pet pet) {
        return petStoreService.updatePet(pet);
    }

    @Operation(
        summary = "Delete a pet",
        description = "Delete a pet from the PetStore"
    )
    @ApiResponse(responseCode = "200", description = "Successfully deleted pet")
    @ApiResponse(responseCode = "400", description = "Invalid ID supplied")
    @ApiResponse(responseCode = "404", description = "Pet not found")
    @DeleteMapping("/pets/{id}")
    public Mono<Void> deletePet(
            @Parameter(description = "Pet ID to delete", required = true)
            @PathVariable Long id) {
        return petStoreService.deletePet(id);
    }
}