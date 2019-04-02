package org.openmrs.module.insuranceclaims.api.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.Concept;
import org.openmrs.module.insuranceclaims.util.InsuranceClaimsConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Model class that represents a concept of item (product or service).
 * Used to join the OpenMRS concept with specific item.
 */
@Entity(name = "iclm.ItemConcept")
@Table(name = "iclm_item_concept")
public class ItemConcept extends BaseOpenmrsData {

	private static final long serialVersionUID = 4206096366339393009L;

	@Id
	@GeneratedValue
	@Column(name = "iclm_item_concept_id")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "concept", nullable = false)
	private Concept concept;

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "item", nullable = false)
	private Item item;

	public ItemConcept() {
	}

	/**
	 * Creates the representation of an item's concept
	 *
	 * @param concept - related concept object
	 * @param item    - related item object
	 */
	public ItemConcept(Concept concept, Item item) {
		super();
		this.concept = concept;
		this.item = item;
	}

	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public Concept getConcept() {
		return concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ItemConcept that = (ItemConcept) o;

		return new EqualsBuilder()
				.appendSuper(super.equals(o))
				.append(id, that.id)
				.append(concept, that.concept)
				.append(item, that.item)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(InsuranceClaimsConstants.HASH_CODE_INITIAL_NON_ZERO_ODD_NUMBER,
				InsuranceClaimsConstants.HASH_CODE_MULTIPLIER_NON_ZERO_ODD_NUMBER)
				.appendSuper(super.hashCode())
				.append(id)
				.append(concept)
				.append(item)
				.toHashCode();
	}
}
