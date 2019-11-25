package org.openmrs.module.insuranceclaims.api.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.openmrs.Concept;
import org.openmrs.Patient;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Model class that represent a provided item.
 * Contains information about the goods and services provided by the health facility to the specific patients.
 */
@Entity(name = "iclm.ProvidedItem")
@Table(name = "iclm_provided_item")
public class ProvidedItem extends AbstractBaseOpenmrsData {

    private static final long serialVersionUID = 100458655928687702L;

    @Id
    @GeneratedValue
    @Column(name = "iclm_provided_item_id")
    private Integer id;

    @Basic
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Basic
    @Column(name = "date_of_served")
    private Date dateOfServed;

    @Basic
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProcessStatus status;

    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "patient", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "item", nullable = false)
    private Concept item;

    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "bill")
    private Bill bill;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getDateOfServed() {
        return dateOfServed == null ? null : (Date) dateOfServed.clone();
    }

    public void setDateOfServed(Date dateOfServed) {
        this.dateOfServed = dateOfServed == null ? null : (Date) dateOfServed.clone();
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Concept getItem() {
        return item;
    }

    public void setItem(Concept item) {
        this.item = item;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return EqualsBuilder.reflectionEquals(this, o, "id", "uuid");
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "id", "uuid");
    }
}
