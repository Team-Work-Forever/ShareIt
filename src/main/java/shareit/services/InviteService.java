package shareit.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import shareit.data.JobOffer;
import shareit.helper.Invitation;
import shareit.helper.ReverseInvite;
import shareit.repository.GlobalRepository;

@Service
public class InviteService {
    
    @Autowired
    private GlobalRepository globalRepository;

    public Collection<Invitation> getAllInvites() {
        return globalRepository.getInvites();
    }

    public Optional<Invitation> getInviteById(int id) {

        return getAllInvites()
            .stream()
                .filter(invite -> invite.getId() == id)
                .findAny();

    }

    public Collection<Invitation> getInboxReverseInvite() {

        Collection<Invitation> invites = globalRepository.getInvites();
        Collection<Invitation> selectedInvites = new ArrayList<>();

        Iterator<Invitation> it = invites.iterator();

        while( it.hasNext() ) {

            Invitation invite = it.next();

            if (invite.getInvitationType() instanceof ReverseInvite) {
                
                if (((ReverseInvite)invite.getInvitationType()).getApplication() instanceof JobOffer) {
                    if (invite.getExpire().isAfter(LocalDate.now())) {
                        selectedInvites.add(invite);
                    } else {
                        it.remove(); // If this invitation get expired then is removed!
                    }
                }

            }

        }

        return selectedInvites;

    }

}
