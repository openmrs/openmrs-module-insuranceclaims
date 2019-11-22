package org.openmrs.module.insuranceclaims.api.model;

import org.hibernate.annotations.ColumnDefault;
import org.openmrs.Concept;

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
import java.util.Objects;

/**
 * Model class that represent a bill.
 * Provides information about the set of provided items.
 */
@Entity(name = "iclm.Bill")
@Table(name = "iclm_bill")
public class Bill extends AbstractBaseOpenmrsData {

    private static final long serialVersionUID = 7220927103234164526L;

    @Id
    @GeneratedValue
    @Column(name = "iclm_bill_id")
    private Integer id;

    @Basic
    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Basic
    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Basic
    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Basic
    @ColumnDefault(value = "'ENTERED'")
    @Column(name = "payment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.ENTERED;

    @Basic
    @Column(name = "payment_type")
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @ManyToOne
    @JoinColumn(name = "diagnosis")
    private Concept diagnosis;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate == null ? null : (Date) startDate.clone();
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate == null ? null : (Date) startDate.clone();
    }

    public Date getEndDate() {
        return endDate == null ? null : (Date) endDate.clone();
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate == null ? null : (Date) endDate.clone();
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public Concept getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(Concept diagnosis) {
        this.diagnosis = diagnosis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Bill)) {
            return false;
        }
        final Bill other = (Bill) o;

        return Objects.equals(this.startDate, other.startDate)
                && Objects.equals(this.endDate, other.endDate)
                && Objects.equals(this.paymentStatus, other.paymentStatus)
                && Objects.equals(this.paymentType, other.paymentType)
                && Objects.equals(this.totalAmount, other.totalAmount)
                && Objects.equals(this.diagnosis, other.diagnosis);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid());
    }
}
