package sem.hoa.domain.entities;

import javax.persistence.*;

import lombok.NoArgsConstructor;

@Entity
@Table(name = "hoas")
@NoArgsConstructor
public class HOA extends HasEvents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "hoa_name", nullable = false, unique = true)
    private String hoaName;
    // TODO Add the other fields of the HOA

    @Column(name = "country", nullable = false, unique = false)
    private String country;

    @Column(name = "city", nullable = false, unique = false)
    private String city;


    /**
     * Create an HOA with only name, country and city.
     *
     * @param name - name of HOA
     * @param country - country of HOA
     * @param city - city of HOA
     */
    public HOA(String name, String country, String city) {
        this.hoaName = name;
        this.country = country;
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public void setHoaName(String hoaName) {
        this.hoaName = hoaName;
    }

    public String getHoaName() {
        return this.hoaName;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }
}
