package shareit.services;

import org.springframework.validation.annotation.Validated;

import shareit.contracts.skill.CreateSkillRequest;

public interface SkillImpl {
    
    void createSkill(@Validated CreateSkillRequest request) throws Exception;

}
