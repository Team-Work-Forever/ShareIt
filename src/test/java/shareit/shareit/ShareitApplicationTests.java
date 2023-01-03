package shareit.shareit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import shareit.repository.GlobalRepository;
import shareit.services.AuthenticationService;
import shareit.services.ExperienceService;
import shareit.services.JobOfferService;
import shareit.services.MemberService;
import shareit.services.ProfAreaService;
import shareit.services.SkillService;
import shareit.services.TalentService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ShareitApplicationTests {

	@Autowired
	private GlobalRepository globalRepository;

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private ExperienceService experienceService;

	@Autowired
	private JobOfferService jobOfferService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private ProfAreaService profAreaService;

	@Autowired
	private TalentService talentService;

	@Autowired
	private SkillService skillService;

	@Test
	void contextLoads() {
		assertNotNull(globalRepository);
		assertNotNull(authenticationService);
		assertNotNull(experienceService);
		assertNotNull(jobOfferService);
		assertNotNull(memberService);
		assertNotNull(profAreaService);
		assertNotNull(talentService);
		assertNotNull(skillService);
	}

}
