package shareit.services;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import shareit.data.Experience;
import shareit.data.JobOffer;
import shareit.data.Talent;
import shareit.errors.JobOfferException;
import shareit.errors.TalentException;

@Service
public class ExperienceService {
    
    @Autowired
    private AuthenticationService authenticationService;

    public Collection<Experience> getAllExperiences(int talentId) {

        var authUser = authenticationService.getAuthenticatedUser();

        Optional<Talent> talent = authUser.getTalents()
            .stream()
                .filter(t -> t.getTalentId() == talentId)
                .findFirst();

        if (!talent.isPresent())
            throw new TalentException("There is no talent with that ID!");

        return talent.get().getExperiences();

    }

    public Experience getExperienceById(int experienceId) {

        var authUser = authenticationService.getAuthenticatedUser();
        Collection<Talent> talents = authUser.getTalents();

        for (Talent talent : talents) {
            if (talent.containsExperience(experienceId)) {
                return talent.getExperienceById(experienceId).get();
            }
        }

        throw new TalentException("Was not found any Experience with that ID!");        

    }

    public boolean associateJobOffer(Experience experience, JobOffer jobOffer) {

        return true;

    }

    public void removeJobOfferById(int jobOfferId) {
        
        var authUser = authenticationService.getAuthenticatedUser();
        Collection<Talent> talents = authUser.getTalents();

        try {
            for (Talent talent : talents) {
                for(Experience experience : talent.getExperiences()) {
                    experience.removeJobOfferById(jobOfferId);
                }
            }
        } catch (Exception e) {
            throw new JobOfferException(e.getMessage());
        }
 
    }

}
