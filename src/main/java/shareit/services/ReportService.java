package shareit.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static shareit.utils.StoreUtils.generateRandomFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import shareit.data.ProfArea;
import shareit.data.Skill;
import shareit.data.Talent;
import shareit.data.auth.IdentityUser;
import shareit.helper.Pair;
import shareit.repository.ReportRepository;

@Service
public class ReportService {
    
    @Autowired
    private TalentService talentService;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private SkillService skillService;

    @Autowired
    private MemberService memberService;

    public void generateReportBySkill() throws IOException {

        Collection<Talent> talents = talentService.getReallyAllTalents();
        Collection<Skill> skills = skillService.getAll();
        Collection<Pair<Skill, Float>> values = new ArrayList<>();


        for (Skill skill : skills) {
            
            float total = 0;
            
            for (Talent talent: talents) {
                if (talent.containsSkill(skill.getSkillId())) {
                    total += talent.getPricePerHour();
                }
            }

            total = total * 176; // sum of the pricePreHour (so 1 hour) multiply by the hours of the month (176 hours)

            values.add(new Pair<Skill,Float>(skill, total));

        }

        reportRepository.writeToFile(values, new String[] {
            "Nome", "Total" 
        }, generateRandomFile());

    }

    public void generateReportByProfAreaAndCountry(Collection<ProfArea> selectedProfArea, String countryName) throws IOException {

        Collection<IdentityUser> users = memberService.getAllMembers();
        Collection<IdentityUser> selectedUsers = new ArrayList<>();
        Map<Talent, Float> selectedTalents = new HashMap<>();
        Collection<Pair<ProfArea, Float>> values = new ArrayList<>(); // Collection To Write File

        int i;

        // Get User from country
        for (IdentityUser user : users) {
            if (user.getCountry().equals(countryName))
                selectedUsers.add(user);
        }
        
        // Busca todos os talentos que conteem as profAreas
        for (IdentityUser user : selectedUsers) {
            for (Talent talent : user.getTalents()) {
                          
                i = 0;

                for (ProfArea profArea : selectedProfArea) {
                    if (talent.containsProfAreaById(profArea.getProfAreaId())) {
                        i++;
                    }
                }

                if (i == selectedProfArea.size()) {
                    
                    selectedTalents.put(talent, talent.getPricePerHour());
                    
                }
            }   
        }

        for (ProfArea profArea : selectedProfArea) {
            
            float totalTalent = 0;

            for (Talent talent : selectedTalents.keySet()) {
                if (talent.containsProfAreaById(profArea.getProfAreaId())) {
                    totalTalent += talent.getPricePerHour();
                }
            }

            totalTalent *= 176; // sum of the pricePreHour (so 1 hour) multiply by the hours of the month (176 hours)

            values.add(new Pair<ProfArea,Float>(profArea, totalTalent));
        }
        
        reportRepository.writeToFile(values, new String[] {
            "Nome", "Total" 
        }, generateRandomFile());

    }
}
