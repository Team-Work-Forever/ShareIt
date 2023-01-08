package shareit.services;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import shareit.contracts.profArea.CreateProfAreaRequest;
import shareit.data.ProfArea;
import shareit.errors.ProfAreaException;
import shareit.repository.GlobalRepository;
import shareit.validator.BeanValidator;

@Service
public class ProfAreaService {
    
    @Autowired
    private GlobalRepository globalRepository;

    private final BeanValidator<CreateProfAreaRequest> validatorProfArea = new BeanValidator<>();

    public void createProfArea(@Validated CreateProfAreaRequest request) throws Exception {

        var errors = validatorProfArea.validate(request);

        if (!errors.isEmpty())
        {
            throw new ProfAreaException(errors.iterator().next().getMessage());
        }

        ProfArea newProfArea = request.toProfArea();

        boolean result = globalRepository.createProfArea(newProfArea);

        if (!result)
            throw new ProfAreaException("Something went wrong!");

        globalRepository.commit();

    }

    public Collection<ProfArea> getAll() {
        return globalRepository.getProfAreas();
    }

    public ProfArea getProfAreaById(int id) {

        Optional<ProfArea> validateProfArea = globalRepository.getProfAreaById(id);

        if (!validateProfArea.isPresent()) {
            throw new ProfAreaException("Professional area not found by the id: " + id);
        }

        return validateProfArea.get();
    }

    public boolean updateProfArea(ProfArea newProfArea, int id) throws Exception {

        Optional<ProfArea> validateProfArea = globalRepository.getProfAreaById(id);

        if (!validateProfArea.isPresent()) {
            throw new ProfAreaException("Professional area not found by the id: " + id);
        }

        globalRepository.removeProfAreaById(id);
        globalRepository.createProfArea(newProfArea);
        globalRepository.commit();

        return true;

    }

    public boolean removeProfArea(int id) throws Exception {

        Optional<ProfArea> validateProfArea = globalRepository.getProfAreaById(id);

        if (!validateProfArea.isPresent()) {
            throw new ProfAreaException("Professional Area not found by the id: " + id);
        }

        if (validateProfArea.get().getQtyProf() != 0) 
            throw new ProfAreaException("Impossible to remove this Professional Area! This Professional Area is being used by " + validateProfArea.get().getQtyProf() + " users.");

        globalRepository.removeProfAreaById(id);
        globalRepository.commit();

        return true;

    }


}
