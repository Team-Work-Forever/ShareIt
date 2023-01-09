package shareit.services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import shareit.data.Experience;
import shareit.data.Talent;
import shareit.errors.JobOfferException;

@Service
public class ExperienceService {
    
    @Autowired
    private AuthenticationService authenticationService;

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
