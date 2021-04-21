package jwzp.wp.VetApp.controller;


import jwzp.wp.VetApp.controller.api.PetsController;
import jwzp.wp.VetApp.controller.api.ResponseToHttp;
import jwzp.wp.VetApp.models.dtos.PetData;
import jwzp.wp.VetApp.models.records.ClientRecord;
import jwzp.wp.VetApp.models.records.PetRecord;
import jwzp.wp.VetApp.models.values.Animal;
import jwzp.wp.VetApp.service.ClientsService;
import jwzp.wp.VetApp.service.PetsService;
import jwzp.wp.VetApp.service.Response;
import jwzp.wp.VetApp.service.ResponseErrorMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@ExtendWith(MockitoExtension.class)
public class PetsControllerTest {

    private ClientRecord owner;
    private PetRecord reksio;
    private PetRecord filemon;

    @Mock
    ClientsService clientsService;

    @Mock
    PetsService petsService;

    @BeforeEach
    private void setup() {
        owner = new ClientRecord(0, "Jan", "Kowalski");
        reksio = new PetRecord(1, "Reksio", LocalDate.parse("2010-01-01"), Animal.Dog, owner);
        filemon = new PetRecord(2, "Filemon", LocalDate.parse("2015-07-07"), Animal.Cat, owner);
    }

    @Test
    public void testGetAllPetsPositive() {
        List<PetRecord> pets = List.of(
            reksio, filemon
        );
        Mockito.when(petsService.getAllPets()).thenReturn(pets);
        var expected = ResponseEntity.ok(
                CollectionModel.of(
                        pets.stream()
                                .map(c -> c.add(linkTo(PetsController.class).slash(c.id).withSelfRel()))
                                .collect(Collectors.toList())
                ).add(linkTo(PetsController.class).withSelfRel())
        );
        var uut = new PetsController(petsService);

        var result = uut.getAllPets();

        assert result.equals(expected);
        Mockito.verify(petsService, Mockito.times(1)).getAllPets();
    }

    @Test
    public void testGetAllPetsEmpty() {
        List<PetRecord> noPets = Collections.emptyList();
        Mockito.when(petsService.getAllPets()).thenReturn(noPets);
        var expected = ResponseEntity.ok(
                CollectionModel.empty().add(linkTo(PetsController.class).withSelfRel())
        );
        var uut = new PetsController(petsService);

        var result = uut.getAllPets();

        assert result.equals(expected);
        Mockito.verify(petsService, Mockito.times(1)).getAllPets();
    }

    @Test
    public void testGetPetPositive() {
        int request = 1;
        Mockito.when(petsService.getPet(Mockito.any(Integer.class))).thenReturn(Optional.of(reksio));
        var expected = ResponseEntity.ok(
                reksio.add(linkTo(PetsController.class).slash(reksio.id).withSelfRel())
        );
        var uut = new PetsController(petsService);

        var result = uut.getPet(request);

        assert result.equals(expected);
        Mockito.verify(petsService, Mockito.times(1)).getPet(request);
    }

    @Test
    public void testGetPetPetNotFound() {
        int request = 0;
        Mockito.when(petsService.getPet(Mockito.any(Integer.class))).thenReturn(Optional.empty());
        var expected = ResponseToHttp.getFailureResponse(ResponseErrorMessage.PET_NOT_FOUND);
        var uut = new PetsController(petsService);

        var result = uut.getPet(request);

        assert result.equals(expected);
        Mockito.verify(petsService, Mockito.times(1)).getPet(request);
    }

    @Test
    public void testAddPetPositive(){
        PetData requested = new PetData("Reksio", LocalDate.parse("2010-01-01"), Animal.Dog, owner.id);
        var Pet = PetRecord.createPetRecord(requested.name, requested.birthday, requested.animal, owner);
        Mockito.when(petsService.addPet(Mockito.any(PetData.class))).thenReturn(Response.succeedResponse(Pet));
        var expected = ResponseEntity.status(HttpStatus.CREATED).body(
                Pet.add(linkTo(PetsController.class).slash(Pet.id).withSelfRel())
        );
        var uut = new PetsController(petsService);

        var result = uut.addPet(requested);

        assert result.equals(expected);
        Mockito.verify(petsService, Mockito.times(1)).addPet(requested);
    }

    @Test
    public void testAddPetMissedData(){
        PetData requested = new PetData(null, null, null, null);
        Mockito.when(petsService.addPet(Mockito.any(PetData.class)))
                .thenReturn(Response.errorResponse(ResponseErrorMessage.WRONG_ARGUMENTS));
        var expected = ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(ResponseErrorMessage.WRONG_ARGUMENTS.getMessage());
        var uut = new PetsController(petsService);

        var result = uut.addPet(requested);

        assert result.equals(expected);
        Mockito.verify(petsService, Mockito.times(1)).addPet(requested);
    }


}
