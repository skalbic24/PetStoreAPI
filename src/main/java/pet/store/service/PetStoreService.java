package pet.store.service;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreData.PetStoreCustomer;
import pet.store.controller.model.PetStoreData.PetStoreEmployee;
import pet.store.dao.CustomerDao;
import pet.store.dao.EmployeeDao;
import pet.store.dao.PetStoreDao;
import pet.store.entity.Customer;
import pet.store.entity.Employee;
import pet.store.entity.PetStore;

@Service
public class PetStoreService {

    @Autowired
    private PetStoreDao petStoreDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private CustomerDao customerDao;

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
        petStore.setPetStorePhone(data.getPetStorePhone());

    }

    private void copyEmployeeFields(Employee employee, PetStoreEmployee petStoreEmployee) {
        employee.setEmployeeFirstName(petStoreEmployee.getEmployeeFirstName());
        employee.setEmployeeId(petStoreEmployee.getEmployeeId());
        employee.setEmployeeJobTitle(petStoreEmployee.getEmployeeJobTitle());
        employee.setEmployeeLastName(petStoreEmployee.getEmployeeLastName());
        employee.setEmployeePhone(petStoreEmployee.getEmployeePhone());
    }

    private void copyCustomerFields(Customer customer, PetStoreCustomer petStoreCustomer) {
        customer.setCustomerEmail(petStoreCustomer.getCustomerEmail());
        customer.setCustomerFirstName(petStoreCustomer.getCustomerFirstName());
        customer.setCustomerId(petStoreCustomer.getCustomerId());
        customer.setCustomerLastName(petStoreCustomer.getCustomerLastName());
    }

    private Employee findOrCreateEmployee(Long petStoreId, Long employeeId) {
        if (Objects.isNull(employeeId)) {
            return new Employee();
        }
        return findEmployeeById(petStoreId, employeeId);
    }

    private Customer findOrCreateCustomer(Long petStoreId, Long customerId) {
        if (Objects.isNull(customerId)) {
            return new Customer();
        }
        return findCustomerById(petStoreId, customerId);
    }

    private Employee findEmployeeById(Long petStoreId, Long employeeId) {
        Employee employee = employeeDao.findById(employeeId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Employee with ID=" + employeeId + " was not found."));
        if (!Objects.equals(employee.getPetStore().getPetStoreId(), petStoreId)) {
            throw new IllegalArgumentException("The employee with ID=" + employeeId
                    + " is not employed by the pet store with ID=" + petStoreId + ".");
        }
        return employee;
    }

    private Customer findCustomerById(Long petStoreId, Long customerId) {
        Customer customer = customerDao.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Customer with ID=" + customerId + " was not found."));

        boolean found = false;

        for (PetStore petStore : customer.getPetStores()) {
            if (Objects.equals(petStore.getPetStoreId(), petStoreId)) {
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("The customer with ID=" + customerId
                    + " is not a member of the pet store with ID=" + petStoreId + ".");
        }

        return customer;
    }

    @Transactional(readOnly = false)
    public PetStoreEmployee saveEmployee(Long petStoreId, PetStoreEmployee petStoreEmployee) {
        PetStore petStore = findPetStoreById(petStoreId);
        Long employeeId = petStoreEmployee.getEmployeeId();
        Employee employee = findOrCreateEmployee(petStoreId, employeeId);

        copyEmployeeFields(employee, petStoreEmployee);

        employee.setPetStore(petStore);
        petStore.getEmployees().add(employee);

        Employee dbEmployee = employeeDao.save(employee);

        return new PetStoreEmployee(dbEmployee);
    }

    @Transactional
    public PetStoreCustomer saveCustomer(Long petStoreId, PetStoreCustomer petStoreCustomer) {
        PetStore petStore = findPetStoreById(petStoreId);
        Long customerId = petStoreCustomer.getCustomerId();
        Customer customer = findOrCreateCustomer(petStoreId, customerId);

        copyCustomerFields(customer, petStoreCustomer);

        customer.getPetStores().add(petStore);
        petStore.getCustomers().add(customer);

        Customer dbCustomer = customerDao.save(customer);

        return new PetStoreCustomer(dbCustomer);
    }

    @Transactional(readOnly = true)
    public List<PetStoreData> retrieveAllPetStores() {
        List<PetStore> petStores = petStoreDao.findAll();
        List<PetStoreData> result = new LinkedList<>();

        for (PetStore petStore : petStores) {
            PetStoreData psd = new PetStoreData(petStore);
            psd.getCustomers().clear();
            psd.getEmployees().clear();
            result.add(psd);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public PetStoreData retrievePetStoreById(Long petStoreId) {
        return new PetStoreData(findPetStoreById(petStoreId));
    }

    private PetStore findPetStoreById(Long petStoreId) {
        return petStoreDao.findById(petStoreId).orElseThrow(() ->
                new NoSuchElementException("PetStore with ID=" + petStoreId + " was not found."));
    }

    @Transactional(readOnly = false)
    public void deletePetStoreById(Long petStoreId) {
        PetStore petStore = findPetStoreById(petStoreId);
        petStoreDao.delete(petStore);
    }
    
    @Transactional
    public void deleteEmployeeById(Long petStoreId, Long employeeId) {
        Employee employee = findEmployeeById(petStoreId, employeeId);
        employeeDao.delete(employee);
    }
    
    @Transactional(readOnly = true)
    public PetStoreCustomer getCustomerByIdAndStore(Long petStoreId, Long customerId) {
        Customer customer = findCustomerById(petStoreId, customerId);
        return new PetStoreCustomer(customer);
    }


}

