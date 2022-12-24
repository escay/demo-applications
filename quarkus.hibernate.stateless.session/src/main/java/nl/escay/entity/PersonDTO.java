package nl.escay.entity;

public class PersonDTO {
    private Integer personId;
    private String firstName;
    private String lastName;

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "PersonDTO [personId=" + personId + ", firstName=" + firstName + ", lastName=" + lastName + "]";
    }
}
