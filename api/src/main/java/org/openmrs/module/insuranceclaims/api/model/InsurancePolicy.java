package org.openmrs.module.insuranceclaims.api.model;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.openmrs.Patient;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Model class that represents an insurance policy
 */
@Entity(name = "iclm.InsurancePolicy")
@Table(name = "iclm_policy")
@Inheritance(strategy = InheritanceType.JOINED)
public class InsurancePolicy extends AbstractBaseOpenmrsData {

	private static final long serialVersionUID = -4340488805384799463L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "iclm_policy_id")
	private Integer id;

	@Basic
	@Column(name = "start_date")
	private Date startDate;

	@Basic
	@Column(name = "expiry_date")
	private Date expiryDate;

	@Basic
	@Column(name = "policy_number")
	private String policyNumber;

	@Basic
	@Column(name = "allowed_money")
	private BigDecimal allowedMoney;

	@Basic
	@Column(name = "used_money")
	private BigDecimal usedMoney;

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "patient", nullable = false)
	private Patient patient;

	@Basic
	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private InsurancePolicyStatus status;

	public InsurancePolicy() {
	}

	/**
	 * Creates the representation of an insurance policy
	 *
	 * @param startDate    - the policy start date
	 * @param expiryDate   - the policy expiry date
	 * @param patient      - related patient
	 * @param status - the policy status
	 */
	public InsurancePolicy(Date startDate, Date expiryDate, Patient patient,
			InsurancePolicyStatus status, String policyNumber) {
		super();
		this.startDate = startDate == null ? null : (Date) startDate.clone();
		this.expiryDate = expiryDate == null ? null : (Date) expiryDate.clone();
		this.patient = patient;
		this.status = status;
		this.policyNumber = policyNumber;
	}

	@Override
	public Integer getId() {
		return this.id;
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

	public Date getExpiryDate() {
		return expiryDate == null ? null : (Date) expiryDate.clone();
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate == null ? null : (Date) expiryDate.clone();
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public InsurancePolicyStatus getStatus() {
		return status;
	}

	public void setStatus(InsurancePolicyStatus status) {
		this.status = status;
	}

	public void setAllowedMoney(BigDecimal allowedMoney) {
		this.allowedMoney = allowedMoney;
	}

	public void setUsedMoney(BigDecimal usedMoney) {
		this.usedMoney = usedMoney;
	}

	public BigDecimal getAllowedMoney() {
		return allowedMoney;
	}

	public BigDecimal getUsedMoney() {
		return usedMoney;
	}
}
