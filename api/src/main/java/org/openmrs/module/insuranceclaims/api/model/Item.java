package org.openmrs.module.insuranceclaims.api.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Model class that represents a item (product or service).
 */
@Entity(name = "iclm.Item")
@Table(name = "iclm_item")
public class Item extends AbstractBaseOpenmrsData {

	private static final long serialVersionUID = 8352049103901464406L;

	@Id
	@GeneratedValue
	@Column(name = "iclm_item_id")
	private Integer id;

	@Basic
	@Column(name = "name", length = 255, nullable = false)
	private String name;

	@Basic
	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Basic
	@Column(name = "care_service")
	private boolean careService;

	public Item() {
	}

	/**
	 * Creates the representation of the item
	 *
	 * @param name        - the item name
	 * @param description - optional description of the item
	 * @param careService - used to distinguish the product (false) or service (true)
	 */
	public Item(String name, String description, boolean careService) {
		super();
		this.name = name;
		this.description = description;
		this.careService = careService;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isCareService() {
		return careService;
	}

	public void setCareService(boolean careService) {
		this.careService = careService;
	}
}
