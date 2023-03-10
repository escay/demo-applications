package nl.escay;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import nl.escay.entity.PersonDTO;
import nl.escay.entity.PersonRecord;

@QuarkusTest
public class PersistenceServiceTest {

    @Inject
    EntityManagerService entityManagerService;

    @Inject
    StatelessSessionService statelessSessionService;

    @Inject
    JdbcService jdbcService;
    
    @Inject
    JooqService jooqService;

    @Test
    public void testDifferentPersistenceApproaches() throws Exception {
        // Insert a record using the entity manager and @Enity object
        entityManagerService.createPerson(5L, "Tom", "Erichsen");
        entityManagerService.createPerson(8L, "Ben", "Johanssen");

        // Insert a record using the Jakarta Persistence Entity manager and sql only
        entityManagerService.createPersonUsingSql(11L, "Jerry", "Nicolas");
        entityManagerService.createPersonUsingSql(12L, "James", "Master");

        // Insert a record using the Hibernate specific StatelessSession and sql only
        statelessSessionService.createPersonUsingSqlAndStatelessSesssion(13L, "Harmony ", "Hill");
        statelessSessionService.createPersonUsingSqlAndStatelessSesssion(14L, "Aleeza ", "Prince");

        // Insert some records using only JDBC
        jdbcService.createPersonUsingJdbcSql(15L, "Hayley", "Hahn");
        jdbcService.createPersonUsingJdbcSql(16L, "Fabian", "Dean");

        // Insert some records using jOOQ
        jooqService.createPerson(17L, "Adam", "Morrow");
        jooqService.createPerson(18L, "Megan", "Stein");
        
        int expectedNrOfPersons = 10;
        
        // Query the database content using a native query and no transaction
        List<PersonDTO> personsAsDtoUsingTuple = entityManagerService.getPersonsAsDtoUsingTuple();
        assertEquals(expectedNrOfPersons, personsAsDtoUsingTuple.size());

        // Query the database content using a native query and no transaction
        List<PersonRecord> personsAsRecordUsingTuple = entityManagerService.getPersonsAsRecordUsingTuple();
        assertEquals(expectedNrOfPersons, personsAsRecordUsingTuple.size());

        // Query the database content using a StatelessSession and a native query and no
        // transaction
        List<PersonRecord> personsAsRecordUsingTupleAndStatelessSession = statelessSessionService
                .getPersonsAsRecordUsingTupleAndStatelessSession();
        assertEquals(expectedNrOfPersons, personsAsRecordUsingTupleAndStatelessSession.size());

        // Query the database content using a Jdbc Sql only
        List<PersonRecord> personsAsRecordUsingJdbcSql = jdbcService.getPersonsAsRecordUsingJdbcSql();
        assertEquals(expectedNrOfPersons, personsAsRecordUsingJdbcSql.size());

        // Query the database content using jOOQ library
        List<PersonRecord> personsAsRecordUsingJooq = jooqService.getPersonsAsRecord();
        assertEquals(expectedNrOfPersons, personsAsRecordUsingJooq.size());
    }
}
