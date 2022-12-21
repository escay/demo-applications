package demo.hibernate.stateless.session;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.type.StandardBasicTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.Tuple;

/**
 * The purpose of this test class is to play around with createNativeQuery calls
 * and try to perform a query in a stateless session to avoid any Hibernate
 * caching logic.<br>
 * Some usage might be: having multiple applications instances connected to the same database and 
 * you do not want to handle EntityManager aspects like 1st and 2nd level cache. But you still want
 * to be able to use transactions in this application instance to store data. 
 */
public class NativeQueryTest {

    private SessionFactory sessionFactory;
    private StatelessSession session;
    
    @BeforeEach
    public void setup() {
        sessionFactory = MyHibernateUtil.createSessionFactory();
        session = sessionFactory.openStatelessSession();
        
        // Create table and insert record
        session.getTransaction().begin();
        session.createNativeMutationQuery("""
				CREATE TABLE persons (
                        id int,
                        first_name varchar(255),
                        last_name varchar(255)
				);""").executeUpdate();
        session.createNativeMutationQuery("INSERT INTO persons (id, first_name, last_name) VALUES (5, 'Tom', 'Erichsen');").executeUpdate();
        session.createNativeMutationQuery("INSERT INTO persons (id, first_name, last_name) VALUES (8, 'Ben', 'Johanssen');").executeUpdate();
        session.getTransaction().commit();
    }

    @AfterEach
    public void teardown() {
        session.close();
        sessionFactory.close();
    }
    
	@Test
	public void testCreateNativeQuery() {
	    // Do NOT use Object[].class as found in the 6.1 documentation examples, 
	    // see case https://hibernate.atlassian.net/browse/HHH-15914
		List<Tuple> persons = session.createNativeQuery("SELECT id, first_name, last_name FROM persons ORDER BY id", Tuple.class).list();
		// Expect 2 rows
		assertEquals(2, persons.size());
		// Validate the first row
		Tuple firstResult = persons.get(0);
		assertEquals(5, firstResult.get(0));
		assertEquals("Tom", firstResult.get(1));
		assertEquals("Erichsen", firstResult.get(2));
	}
	
    @Test
    public void testCreateNativeQueryTransformToDTO() {
        List<PersonDTO> persons = session.createNativeQuery("SELECT id, first_name, last_name FROM persons ORDER BY id", Tuple.class)
                .addScalar("id", StandardBasicTypes.INTEGER)
                .addScalar("first_name", StandardBasicTypes.STRING)
                .addScalar("last_name", StandardBasicTypes.STRING)
                .setTupleTransformer((tuples, aliases) -> {
                    System.out.println("Transform tuple");
                    PersonDTO personDTO = new PersonDTO();
                    personDTO.setPersonId((int) tuples[0]);
                    personDTO.setFirstName((String) tuples[1]);
                    personDTO.setLastName((String) tuples[2]);
                    return personDTO;
                }).list();
        // Expect 2 rows
        assertEquals(2, persons.size());
        // Validate the first row
        PersonDTO firstResult = persons.get(0);
        assertEquals(5, firstResult.getPersonId());
        assertEquals("Tom", firstResult.getFirstName());
        assertEquals("Erichsen", firstResult.getLastName());
    }
    
    @Test
    public void testCreateNativeQueryTransformToRecord() {
        List<PersonRecord> persons = session.createNativeQuery("SELECT id, first_name, last_name FROM persons ORDER BY id", Tuple.class)
                .addScalar("id", StandardBasicTypes.INTEGER)
                .addScalar("first_name", StandardBasicTypes.STRING)
                .addScalar("last_name", StandardBasicTypes.STRING)
                .setTupleTransformer((tuples, aliases) -> {
                    return new PersonRecord((int) tuples[0], (String) tuples[1], (String) tuples[2]);
                }).list();
        // Expect 2 rows
        assertEquals(2, persons.size());
        // Validate the first row
        PersonRecord firstResult = persons.get(0);
        assertEquals(5, firstResult.id());
        assertEquals("Tom", firstResult.firstName());
        assertEquals("Erichsen", firstResult.lastName());
    }
}
