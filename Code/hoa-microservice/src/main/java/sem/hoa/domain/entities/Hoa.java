package sem.hoa.domain.entities;


import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.GenerationType;

import java.util.Objects;

@Entity
@Table(name = "hoas")
@NoArgsConstructor
public class Hoa extends HasEvents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "hoa_name", nullable = false, unique = true)
    private String hoaName;
    // TODO Add the other fields of the Hoa

    @Column(name = "country", nullable = false, unique = false)
    private String country;

    @Column(name = "city", nullable = false, unique = false)
    private String city;


    /**
     * Constructor for a new HOA.
     *
     * @param name    - name of HOA
     * @param country - country of HOA
     * @param city    - city of HOA
     */
    public Hoa(String name, String country, String city) {
        this.hoaName = name;
        this.country = country;
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHoaName() {
        return hoaName;
    }

    public void setHoaName(String hoaName) {
        this.hoaName = hoaName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Hoa)) {
            return false;
        }
        Hoa hoa = (Hoa) o;
        return id == hoa.id && Objects.equals(hoaName, hoa.hoaName) && Objects.equals(country, hoa.country) && Objects.equals(city, hoa.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, hoaName, country, city);
    }
}
