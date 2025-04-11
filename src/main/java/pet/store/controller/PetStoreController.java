package pet.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import pet.store.controller.model.PetStoreData;
import pet.store.service.PetStoreService;

@RestController
@RequestMapping("/pet_store")
@Slf4j
public class PetStoreController {

    @Autowired
    private PetStoreService petStoreService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PetStoreData createPetStore(@RequestBody PetStoreData petStoreData) {
        log.info("Creating pet store: {}", petStoreData);
        return petStoreService.savePetStore(petStoreData);
    }

    @PutMapping("/{petStoreId}")
    public PetStoreData updatePetStore(@PathVariable Long petStoreId,
                                       @RequestBody PetStoreData petStoreData) {
        log.info("Updating pet store with ID={}", petStoreId);
        petStoreData.setPetStoreId(petStoreId);
        return petStoreService.savePetStore(petStoreData);
    }
}
