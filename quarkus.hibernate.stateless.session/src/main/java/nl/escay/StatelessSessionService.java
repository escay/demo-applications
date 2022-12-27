package nl.escay;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import nl.escay.entity.PersonRecord;

@ApplicationScoped
public class StatelessSessionService {
    
    @Inject
    EntityManager em; 

    @Transactional
    public void createPersonUsingSqlAndStatelessSesssion(Long id, String firstName, String lastName) {
        // If you know Hibernate is the implementation and you do not care about using 
        // Hibernate specific code you can use the Hibernate StatelessSession interface.
        Session hibernateSession = em.unwrap(Session.class);
        SessionFactory sessionFactory = hibernateSession.getSessionFactory();
        
        try (StatelessSession openStatelessSession = sessionFactory.openStatelessSession()) {
            openStatelessSession.createNativeQuery("INSERT INTO Person (id, firstName, lastName) VALUES (:id, :firstName, :lastName)")
                .setParameter("id", id)
                .setParameter("firstName", firstName)
                .setParameter("lastName", lastName)
                .executeUpdate();
        }

        // No need to use
        // session.getTransaction().begin();
        // session.getTransaction().commit();
    }
    
    public List<PersonRecord> getPersonsAsRecordUsingTupleAndStatelessSession() {
        List<PersonRecord> personRecords = null;

        // If you know Hibernate is the implementation and you do not care about using 
        // Hibernate specific code you can use the Hibernate StatelessSession interface.
        Session hibernateSession = em.unwrap(Session.class);
        SessionFactory sessionFactory = hibernateSession.getSessionFactory();
        
        try (StatelessSession openStatelessSession = sessionFactory.openStatelessSession()) {
            List<Tuple> resultTuples = openStatelessSession
                    .createNativeQuery("select id, firstName, lastName from Person", Tuple.class).getResultList();
            
            // Convert to Record using stream api, quite similar to Hibernate 6 .setTupleTransformer
            personRecords = resultTuples.stream()
                    .map(tuple -> new PersonRecord(
                            (Integer) tuple.get(0), 
                            (String) tuple.get(1), 
                            (String) tuple.get(2)))
                    .collect(Collectors.toList());
        }
        
        System.out.println(Arrays.toString(personRecords.toArray()));
        return personRecords;
    }
       
}