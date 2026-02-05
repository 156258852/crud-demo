package com.example.demo.service;

import com.example.demo.client.petstore.api.PetApi;
import com.example.demo.client.petstore.model.Pet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetStoreService {

    private final PetApi petApi;

    public Mono<Pet> getPetById(Long petId) {
        return petApi.getPetById(petId);
    }

    public Flux<Pet> getPetsByStatus(String status) {
        return petApi.findPetsByStatus(List.of(status));
    }

    public Mono<Pet> updatePet(Pet pet) {
        // 更新操作返回Void，所以我们先执行更新，然后获取更新后的宠物信息
        return petApi.updatePet(pet)
            .then(getPetById(pet.getId()));
    }

    public Mono<Pet> addPet(Pet pet) {
        // 添加操作返回Void，所以我们先执行添加，然后获取新添加的宠物信息
        return petApi.addPet(pet)
            .then(getPetById(pet.getId()));
    }

    public Mono<Void> deletePet(Long petId) {
        return petApi.deletePet(petId, null);
    }
}