package jwzp.wp.VetApp.controller.ui;

import jwzp.wp.VetApp.service.ClientsService;
import jwzp.wp.VetApp.service.PetsService;
import jwzp.wp.VetApp.service.VetsService;
import jwzp.wp.VetApp.service.VisitsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UiController {

    private final VisitsService visitsService;
    private final PetsService petsService;
    private final ClientsService clientsService;
    private final VetsService vetsService;

    @Autowired
    public UiController(VisitsService visitsService, PetsService petsService, ClientsService clientsService, VetsService vetsService) {
        this.visitsService = visitsService;
        this.petsService = petsService;
        this.clientsService = clientsService;
        this.vetsService = vetsService;
    }

    @GetMapping("/visits")
    public String getAllVisits(Model model) {
        if(visitsService.getAllVisits().size() == 0) {
            model.addAttribute("visits", null);
        } else{
            model.addAttribute("visits", visitsService.getAllVisits());
        }
        return "visits";
    }

    @GetMapping("/pets")
    public String getAllPets(Model model) {
        if(petsService.getAllPets().size() == 0) {
            model.addAttribute("pets", null);
        } else{
            model.addAttribute("pets", petsService.getAllPets());
        }
        return "pets";
    }

    @GetMapping("/clients")
    public String getAllClients(Model model) {
        if(clientsService.getAllClients().size() == 0) {
            model.addAttribute("clients", null);
        } else{
            model.addAttribute("clients", clientsService.getAllClients());
        }
        return "clients";
    }

    @GetMapping("/vets")
    public String getAllVets(Model model) {
        if(vetsService.getAllVets().size() == 0) {
            model.addAttribute("vets", null);
        } else{
            model.addAttribute("vets", vetsService.getAllVets());
        }
        return "vets";
    }
}
