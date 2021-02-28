package jwzp.wp.VetApp.controller.ui;

import jwzp.wp.VetApp.service.VisitsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UiController {

    private final VisitsService visitsService;

    @Autowired
    public UiController(VisitsService visitsService) {
        this.visitsService = visitsService;
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
}
