package org.openmrs.module.insuranceclaims.api.model;

import org.openmrs.Concept;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

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
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Basic
    @Column(name = "payment_type")
    private PaymentType paymentType;

    @ManyToOne
    @JoinColumn(name = "diagnosis")
    private Concept diagnosis;

    public Bill() {
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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
}