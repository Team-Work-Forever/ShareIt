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

    /**
     * Create Skill, validating it's attributes
     * @param request CreateSkillRequest
     * @throws Exception
     */
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

    /**
     * Get All Skills
     * @return Collection
     */
    public Collection<Skill> getAll() {
        return globalRepository.getSkills();
    }

    /**
     * Get Skill by Id
     * @param id Given Skill Id
     * @return Skill
     */
    public Skill getSkillById(int id) {

        Optional<Skill> validateSkill = globalRepository.getSkillById(id);

        if (!validateSkill.isPresent()) {
            throw new SkillException("Skill not found by the id: " + id);
        }

        return validateSkill.get();
    }

    /**
     * Update's Skill
     * @param newSkill Given New Skill
     * @param id Given Original Skill Id
     * @return true if Original Skill is updated successfully
     * @throws Exception
     */
    public boolean updateSkill(Skill newSkill, int id) throws Exception {

        Optional<Skill> validateSkill = globalRepository.getSkillById(id);

        if (!validateSkill.isPresent()) {
            throw new SkillException("Skill not found by the id: " + id);
        }

        globalRepository.removeSkillById(id);
        globalRepository.createSkill(newSkill);
        globalRepository.commit();

        return true;

    }

    /**
     * Remove Skill By Id
     * @param id Given Skill Id
     * @return true if Skill is removed
     * @throws Exception
     */
    public boolean removeSkill(int id) throws Exception {

        Optional<Skill> validateSkill = globalRepository.getSkillById(id);

        if (!validateSkill.isPresent()) {
            throw new SkillException("Skill not found by the id: " + id);
        }

        if (validateSkill.get().getQtyProf() != 0) 
            throw new SkillException("Impossible to remove this skill! This skill is being used by " + validateSkill.get().getQtyProf() + " users.");

        globalRepository.removeSkillById(id);
        globalRepository.commit();

        return true;

    }

}
