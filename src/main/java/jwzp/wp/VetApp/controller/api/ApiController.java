package jwzp.wp.VetApp.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(path="/api/visits")
@RestController
public class ApiController {

    @GetMapping(path="/test")
    public ResponseEntity<?> test(){
        return ResponseEntity.ok().body("OK");
    }
}
