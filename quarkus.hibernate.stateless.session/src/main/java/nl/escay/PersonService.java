package nl.escay;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.transform.Transformers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import nl.escay.entity.Person;
import nl.escay.entity.PersonDTO;
import nl.escay.entity.PersonRecord;

@ApplicationScoped
public class PersonService {
    
    @Inject
    EntityManager em; 

    @Transactional 
    public void createPerson(Long id, String firstName, String lastName) {
        Person person = new Person();
        person.setId(id);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        em.persist(person);
        
        // No need to call flush(), when the transaction is finished data is committed to the database.
    }
    
    @Transactional
    public void createPersonUsingSql(Long id, String firstName, String lastName) {
        em.createNativeQuery("INSERT INTO Person (id, firstName, lastName) VALUES (:id, :firstName, :lastName)")
            .setParameter("id", id)
            .setParameter("firstName", firstName)
            .setParameter("lastName", lastName)
            .executeUpdate();
        
        // No need to call flush(), when the transaction is finished data is committed to the database.
        //
        // Although this example works, I would not mix Entity objects with manually created objects. 
        // This example mainly shows inserting objects using only sql and no Entity classes is possible
        // using Jakarta persistence.
    }
    
    @Transactional(value = TxType.NEVER)
    public List<PersonDTO> getPersonsAsDtoUsingTuple() {
        @SuppressWarnings("unchecked")
        List<Tuple> resultTuples = em.createNativeQuery("select id, firstName, lastName from Person", Tuple.class).getResultList();

        // Convert to DTO using stream api, quite similar to Hibernate 6 .setTupleTransformer
        List<PersonDTO> personDtos = resultTuples.stream()
                .map(tuple -> {
                    PersonDTO dto = new PersonDTO();
                    dto.setPersonId((Integer) tuple.get(0));
                    dto.setFirstName((String) tuple.get(1));
                    dto.setLastName((String) tuple.get(2));
                    return dto;})
                .collect(Collectors.toList());

        System.out.println(Arrays.toString(personDtos.toArray()));
        return personDtos;        
    }

    public List<PersonRecord> getPersonsAsRecordUsingTuple() {
        @SuppressWarnings("unchecked")
        List<Tuple> resultTuples = em.createNativeQuery("select id, firstName, lastName from Person", Tuple.class).getResultList();

        // Convert to Record using stream api, quite similar to Hibernate 6 .setTupleTransformer
        List<PersonRecord> personRecords = resultTuples.stream()
                .map(tuple -> new PersonRecord(
                        (Integer) tuple.get(0), 
                        (String) tuple.get(1), 
                        (String) tuple.get(2)))
                .collect(Collectors.toList());
        
        System.out.println(Arrays.toString(personRecords.toArray()));
        return personRecords;
    }
    
    public void getPersonsAsDtoUsingHibernateSpecificTransformer() {
        // Cannot use
        // .unwrap(org.hibernate.query.Query.class)
        // and then call .setResultTransformer for DTO or Record conversion,
        // because Hibernate 5.6.14.Final will assume Entity class. You will get a 'Unkown Entity' MappingException.
        // In Hibernate 6.2 this is lifted to support also non Entity classes.
        //
        // The "DTO projections using a ConstructorResult" approach as described here:
        // https://vladmihalcea.com/the-best-way-to-map-a-projection-query-to-a-dto-with-jpa-and-hibernate/
        // Seems quite elaborate and error proof.
        //
        // But the approach with the 'Transformers.aliasToBean' is nice: 
        // .unwrap(org.hibernate.query.Query.class)
        // .setResultTransformer(Transformers.aliasToBean(PostDTO.class))

        if (false) {
            List<PersonDTO> result = em.createNativeQuery("select p.id as id, p.firstName, p.lastName as name from Person p")
                    .unwrap(org.hibernate.query.Query.class)
                    .setResultTransformer(Transformers.aliasToBean(PersonDTO.class))
                    .getResultList();
        }
        // Nice idea, compiles, but results in 
        // jakarta.persistence.PersistenceException: org.hibernate.PropertyNotFoundException: 
        // Could not resolve PropertyAccess for ID on class nl.escay.entity.PersonDTO
    }
    
}