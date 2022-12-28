package nl.escay;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import nl.escay.entity.PersonRecord;

@ApplicationScoped
public class JooqService {

    @Inject
    AgroalDataSource defaultDataSource;
    
    public List<PersonRecord> getPersonsAsRecord() throws SQLException {
        Connection connection = defaultDataSource.getConnection();
        DSLContext dsl = DSL.using(connection, SQLDialect.H2);
        
        List<PersonRecord> personRecords = null;

        // Make plain SQL call using jOOQ
        Result<Record> fetchResult = dsl.fetch("select id, firstName, lastName from Person");
        
        // Convert Result (which implements java.util.List) to Record using stream api
        personRecords = fetchResult.stream()
                .map(tuple -> new PersonRecord(
                        (Integer) tuple.get(0), 
                        (String) tuple.get(1), 
                        (String) tuple.get(2)))
                .collect(Collectors.toList());
        
        System.out.println(Arrays.toString(personRecords.toArray()));
        return personRecords;
    }
}
