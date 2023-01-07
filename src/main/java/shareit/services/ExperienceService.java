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

    public Collection<Experience> getAllExperiences(String talentName) {

        var authUser = authenticationService.getAuthenticatedUser();

        Optional<Talent> talent = authUser.getTalents()
            .stream()
                .filter(t -> t.getName().equals(talentName))
                .findFirst();

        if (!talent.isPresent())
            throw new TalentException("There is no talent with that name");

        return talent.get().getExperiences();

    }

    public Experience getExperienceByTitle(String title) {

        var authUser = authenticationService.getAuthenticatedUser();
        Collection<Talent> talents = authUser.getTalents();

        try {
            
            for (Talent talent : talents) {
                return talent.getExperienceByTitle(title);
            }

        } catch (Exception e) {
            throw new TalentException(e.getMessage());
        }

        throw new TalentException("Was not found any Experience with that title!");        

    }

    public boolean associateJobOffer(Experience experience, JobOffer jobOffer) {

        return true;

    }

    public void removeJobOfferById(int id) {
        
        var authUser = authenticationService.getAuthenticatedUser();
        Collection<Talent> talents = authUser.getTalents();

        try {
            for (Talent talent : talents) {
                for(Experience experience : talent.getExperiences()) {
                    experience.removeJobOfferById(id);
                }
            }
        } catch (Exception e) {
            throw new JobOfferException(e.getMessage());
        }
 
    }

}
