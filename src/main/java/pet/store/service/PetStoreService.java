package pet.store.service;

import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pet.store.controller.model.PetStoreData;
import pet.store.dao.PetStoreDao;
import pet.store.entity.PetStore;

@Service
public class PetStoreService {

    @Autowired
    private PetStoreDao petStoreDao;

    @Transactional(readOnly = false)
    public PetStoreData savePetStore(PetStoreData petStoreData) {
        PetStore petStore = findOrCreatePetStore(petStoreData.getPetStoreId());
        copyPetStoreFields(petStore, petStoreData);
        return new PetStoreData(petStoreDao.save(petStore));
    }

    private PetStore findOrCreatePetStore(Long petStoreId) {
        if (petStoreId == null) {
            return new PetStore();
        } else {
            return petStoreDao.findById(petStoreId)
                .orElseThrow(() -> new NoSuchElementException(
                    "PetStore with ID=" + petStoreId + " was not found."));
        }
    }

    private void copyPetStoreFields(PetStore petStore, PetStoreData data) {
        petStore.setPetStoreName(data.getPetStoreName());
        petStore.setPetStoreAddress(data.getPetStoreAddress());
        petStore.setPetStoreCity(data.getPetStoreCity());
        petStore.setPetStoreState(data.getPetStoreState());
        petStore.setPetStoreZip(data.getPetStoreZip());
    }
}
