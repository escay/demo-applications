package nl.escay;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import nl.escay.entity.PersonRecord;

@ApplicationScoped
public class JdbcService {

    @Inject
    AgroalDataSource defaultDataSource;

    @Transactional
    public void createPersonUsingJdbcSql(Long id, String firstName, String lastName)
            throws SQLException {

        try (Connection connection = defaultDataSource.getConnection();
                PreparedStatement prepareStatement = connection.prepareStatement(
                        "INSERT INTO Person (id, firstName, lastName) VALUES (?, ?, ?)");) {
            // Disadvantage: can only use index, no named references to ":id" like in Hibernate or JOOQ.
            // Disadvantage: no sql logging documentation. No parameter logging like in Hibernate 6.
            prepareStatement.setInt(1, id.intValue());
            prepareStatement.setString(2, firstName);
            prepareStatement.setString(3, lastName);
            prepareStatement.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }

    public List<PersonRecord> getPersonsAsRecordUsingJdbcSql() throws SQLException {
        List<PersonRecord> personRecords = new ArrayList<>();
        
        try (Connection connection = defaultDataSource.getConnection();
                PreparedStatement prepareStatement = connection.prepareStatement(
                    "select id, firstName, lastName from Person");
                ResultSet rs = prepareStatement.executeQuery()) {
            
            // Disadvantage: cannot use stream. Must use ResultSet
            // jOOQ has made a nice comparison: https://www.jooq.org/java-8-and-sql
            // Disadvantage: Unluckily we must apply the wasNull overhead code from the old days.
            // Disadvantage: no sql logging documentation. No parameter logging like in Hibernate 6.
            while (rs.next()) {
                Integer id = rs.getInt("id");
                if (rs.wasNull()) id = null;
                String firstName = rs.getString("firstName");
                if (rs.wasNull()) firstName = null;
                String lastName = rs.getString("lastName");
                if (rs.wasNull()) lastName = null;
                
                personRecords.add(new PersonRecord(id, firstName, lastName));
            }
        }
        
        System.out.println(Arrays.toString(personRecords.toArray()));
        return personRecords;
    }
    
}
