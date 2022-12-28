package nl.escay;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import static org.jooq.impl.DSL.*;
import org.jooq.*;
import org.jooq.impl.*;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import nl.escay.entity.PersonRecord;

@ApplicationScoped
public class JooqService {

    @Inject
    AgroalDataSource defaultDataSource;

    @Transactional
    public void createPerson(Long id, String firstName, String lastName) throws SQLException {
        // Not using jOOQ generated Tables and Columns at this moment.
        // Try out the plain SQL api of jOOQ instead:
        // https://www.jooq.org/doc/latest/manual/sql-building/plain-sql/
        // https://www.jooq.org/doc/latest/manual/getting-started/use-cases/jooq-as-a-sql-builder-without-codegeneration/

        try (Connection connection = defaultDataSource.getConnection();) {
            DSLContext dsl = DSL.using(connection, SQLDialect.H2);
            
            dsl.insertInto(table("person"))
                .values(id, firstName, lastName)
                .execute();
        } catch (SQLException e) {
            throw e;
        }
    }

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
