package nl.escay;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import nl.escay.entity.PersonDTO;
import nl.escay.entity.PersonRecord;

@QuarkusTest
public class PersonServiceTest {

    @Inject
    PersonService service;

    @Test
    public void testJpaNativeQuery() {
        // Insert a record using the entity manager
        service.createPerson(5L, "Tom", "Erichsen");
        service.createPerson(8L, "Ben", "Johanssen");
        
        service.createPersonUsingSql(11L, "Jerry", "Nicolas");
        service.createPersonUsingSql(12L, "James", "Master");

        // Query the database content using a native query and no transaction
        List<PersonDTO> personsAsDtoUsingTuple = service.getPersonsAsDtoUsingTuple();
        assertEquals(4, personsAsDtoUsingTuple.size());

        // Query the database content using a native query and no transaction
        List<PersonRecord> personsAsRecordUsingTuple = service.getPersonsAsRecordUsingTuple();
        assertEquals(4, personsAsRecordUsingTuple.size());
    }
}
