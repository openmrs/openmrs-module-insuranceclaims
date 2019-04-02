package org.openmrs.module.insuranceclaims.api.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.Patient;
import org.openmrs.module.insuranceclaims.util.InsuranceClaimsConstants;

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Model class that represents an insurance policy
 */
@Entity(name = "iclm.InsurancePolicy")
@Table(name = "iclm_policy")
public class InsurancePolicy extends BaseOpenmrsData {

	private static final long serialVersionUID = -4340488805384799463L;

	@Id
	@GeneratedValue
	@Column(name = "iclm_policy_id")
	private Integer id;

	@Basic
	@Column(name = "start_date")
	private Date startDate;

	@Basic
	@Column(name = "expiry_date")
	private Date expiryDate;

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "patient", nullable = false)
	private Patient patient;

	@Basic
	@Column(name = "policy_status", nullable = false)
	private InsurancePolicyStatus policyStatus;

	public InsurancePolicy() {
	}

	/**
	 * Creates the representation of an insurance policy
	 *
	 * @param startDate    - the policy start date
	 * @param expiryDate   - the policy expiry date
	 * @param patient      - related patient
	 * @param policyStatus - the policy status
	 */
	public InsurancePolicy(Date startDate, Date expiryDate, Patient patient,
			InsurancePolicyStatus policyStatus) {
		super();
		this.startDate = startDate == null ? null : (Date) startDate.clone();
		this.expiryDate = expiryDate == null ? null : (Date) expiryDate.clone();
		this.patient = patient;
		this.policyStatus = policyStatus;
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

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public InsurancePolicyStatus getPolicyStatus() {
		return policyStatus;
	}

	public void setPolicyStatus(InsurancePolicyStatus policyStatus) {
		this.policyStatus = policyStatus;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		InsurancePolicy that = (InsurancePolicy) o;

		return new EqualsBuilder()
				.appendSuper(super.equals(o))
				.append(id, that.id)
				.append(startDate, that.startDate)
				.append(expiryDate, that.expiryDate)
				.append(patient, that.patient)
				.append(policyStatus, that.policyStatus)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(InsuranceClaimsConstants.HASH_CODE_INITIAL_NON_ZERO_ODD_NUMBER,
				InsuranceClaimsConstants.HASH_CODE_MULTIPLIER_NON_ZERO_ODD_NUMBER)
				.appendSuper(super.hashCode())
				.append(id)
				.append(startDate)
				.append(expiryDate)
				.append(patient)
				.append(policyStatus)
				.toHashCode();
	}
}
