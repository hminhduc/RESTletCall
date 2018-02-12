package restletcall.netsuite.com.restletcall;

/**
 * Created by Kyuubi on 2018/01/22.
 */

public class Customer {
    private String id;
    private String entityid;
    private String companyname;
    private String firstName;
    private String lastName;
    private String middleName;

    public Customer(String id,String entityid,String companyname,String firstName,String lastName,String middleName) {
        this.id = id;
        this.entityid = entityid;
        this.companyname = companyname;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntityid() {
        return entityid;
    }

    public void setEntityid(String entityid) {
        this.entityid = entityid;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @Override
    public String toString() {
        return  entityid;
    }
}
