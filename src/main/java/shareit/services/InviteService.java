package shareit.services;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import shareit.helper.Invitation;
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

}
