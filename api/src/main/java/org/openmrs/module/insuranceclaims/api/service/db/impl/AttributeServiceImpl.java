package org.openmrs.module.insuranceclaims.api.service.db.impl;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Provider;
import org.openmrs.ProviderAttribute;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.attribute.BaseAttribute;
import org.openmrs.module.insuranceclaims.api.service.db.AttributeService;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.LOCATION_EXTERNAL_ID_ATTRIBUTE_UUID;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.PATIENT_EXTERNAL_ID_IDENTIFIER_UUID;
import static org.openmrs.module.insuranceclaims.api.service.fhir.util.InsuranceClaimConstants.PROVIDER_EXTERNAL_ID_ATTRIBUTE_UUID;


@Repository("insuranceclaims.AttributeServiceDao")
public class AttributeServiceImpl extends BaseOpenmrsService
        implements AttributeService {

    private SessionFactory sessionFactory;

    @Override
    public List<Provider> getProviderByExternalIdAttribute(String externalId) {
        List<ProviderAttribute> result = getOmrsObjectAttributesByAttributeTypeValue(ProviderAttribute.class,
                externalId, PROVIDER_EXTERNAL_ID_ATTRIBUTE_UUID);
        return result.stream()
                .map(ProviderAttribute::getProvider)
                .collect(Collectors.toList());
    }

    @Override
    public List<Location> getLocationByExternalIdAttribute(String externalId) {
        List<LocationAttribute> result = getOmrsObjectAttributesByAttributeTypeValue(LocationAttribute.class,
                externalId, LOCATION_EXTERNAL_ID_ATTRIBUTE_UUID);
        return result.stream()
                .map(LocationAttribute::getLocation)
                .collect(Collectors.toList());
    }

    @Override
    public List<Patient> getPatientByExternalIdIdentifier(String externalId) {
        Criteria crit = getCurrentSession().createCriteria(PatientIdentifier.class, "attribute");
        crit.createAlias("attribute.identifierType", "attribute_type");

        crit.add(Restrictions.eq("attribute_type.uuid", PATIENT_EXTERNAL_ID_IDENTIFIER_UUID));
        crit.add(Restrictions.eq("attribute.identifier", externalId));
        List<PatientIdentifier> result = crit.list();
        return result.stream()
                .map(PatientIdentifier::getPatient)
                .collect(Collectors.toList());
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    private List getOmrsObjectAttributesByAttributeTypeValue(
            Class<? extends BaseAttribute> attributeClass, String attributeValue, String attributeUuid) {
        Criteria crit = getCurrentSession().createCriteria(attributeClass, "attribute");
        crit.createAlias("attribute.attributeType", "attribute_type");

        crit.add(Restrictions.eq("attribute_type.uuid", attributeUuid));
        crit.add(Restrictions.eq("attribute.valueReference", attributeValue));
        return crit.list();
    }

}
