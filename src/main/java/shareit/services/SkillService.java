package shareit.services;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import shareit.contracts.skill.CreateSkillRequest;
import shareit.data.Skill;
import shareit.errors.SkillException;
import shareit.validator.BeanValidator;
import shareit.repository.GlobalRepository;

@Service
public class SkillService {

    private final BeanValidator<CreateSkillRequest> validatorSkill = new BeanValidator<>();
    
    @Autowired
    private GlobalRepository globalRepository;

    public void createSkill(@Validated CreateSkillRequest request) throws Exception {

        Skill newSkill;

        var errors = validatorSkill.validate(request);

        if (!errors.isEmpty())
        {
            throw new SkillException(errors.iterator().next().getMessage());
        }

        newSkill = request.toSkill();

        var result = globalRepository.createSkill(newSkill);

        if (!result)
            throw new SkillException("Something went wrong!");

        globalRepository.commit();

    }

    public Collection<Skill> getAll() {
        return globalRepository.getSkills();
    }

    public Skill getSkillByName(String name) {

        Optional<Skill> validateSkill = globalRepository.getSkillByName(name);

        if (!validateSkill.isPresent()) {
            throw new SkillException("Skill not found by the name: " + name);
        }

        return validateSkill.get();
    }

    public boolean updateSkill(Skill newSkill, String name) throws Exception {

        Optional<Skill> validateSkill = globalRepository.getSkillByName(name);

        if (!validateSkill.isPresent()) {
            throw new SkillException("Skill not found by the name: " + name);
        }

        globalRepository.removeSkillByName(name);
        globalRepository.createSkill(newSkill);
        globalRepository.commit();

        return true;

    }

    public boolean removeSkill(String name) throws Exception {

        Optional<Skill> validateSkill = globalRepository.getSkillByName(name);

        if (!validateSkill.isPresent()) {
            throw new SkillException("Skill not found by the name: " + name);
        }

        if (validateSkill.get().getQtyProf() == 0) 
            throw new SkillException("Impossible to remove this skill! This skill is being used by " + validateSkill.get().getQtyProf() + " users.");

        globalRepository.removeSkillByName(name);
        globalRepository.commit();

        return true;

    }

}
