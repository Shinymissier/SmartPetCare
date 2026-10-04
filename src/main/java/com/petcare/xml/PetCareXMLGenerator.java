package com.petcare.xml;

import com.petcare.model.Appointment;
import com.petcare.model.MedicalRecord;
import com.petcare.model.Pet;
import com.petcare.model.Vaccination;
import com.petcare.util.DateUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;

public class PetCareXMLGenerator {

    /**
     * Generate complete PetCareRecord XML containing Pets, Vaccinations, Medical Records, and Appointments.
     */
    public static String generateCompleteXML(List<Pet> pets, List<Vaccination> vaccinations,
                                             List<MedicalRecord> medicalRecords, List<Appointment> appointments,
                                             String userName, boolean includeDtd) throws Exception {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        // Root element
        Element rootElement = doc.createElement("PetCareRecord");
        rootElement.setAttribute("exportDate", LocalDate.now().toString());
        rootElement.setAttribute("generatedBy", userName != null ? userName : "Smart Pet Care Tracker");
        rootElement.setAttribute("version", "1.0");
        doc.appendChild(rootElement);

        // 1. Pets
        if (pets != null) {
            for (Pet pet : pets) {
                Element petElem = doc.createElement("Pet");
                appendTextElement(doc, petElem, "PetID", pet.getId());
                appendTextElement(doc, petElem, "Name", pet.getName());
                appendTextElement(doc, petElem, "Species", pet.getSpecies());
                if (pet.getBreed() != null) appendTextElement(doc, petElem, "Breed", pet.getBreed());
                if (pet.getDateOfBirth() != null) appendTextElement(doc, petElem, "DateOfBirth", DateUtil.formatIsoDate(pet.getDateOfBirth()));
                if (pet.getGender() != null) appendTextElement(doc, petElem, "Gender", pet.getGender());
                if (pet.getWeight() != null) appendTextElement(doc, petElem, "Weight", pet.getWeight().toString());
                if (pet.getColor() != null) appendTextElement(doc, petElem, "Color", pet.getColor());
                if (pet.getAllergies() != null) appendTextElement(doc, petElem, "Allergies", pet.getAllergies());
                if (pet.getHealthConditions() != null) appendTextElement(doc, petElem, "HealthConditions", pet.getHealthConditions());
                if (pet.getNotes() != null) appendTextElement(doc, petElem, "Notes", pet.getNotes());
                rootElement.appendChild(petElem);
            }
        }

        // 2. Vaccinations
        if (vaccinations != null && !vaccinations.isEmpty()) {
            Element vacsElem = doc.createElement("Vaccinations");
            for (Vaccination v : vaccinations) {
                Element vElem = doc.createElement("Vaccination");
                appendTextElement(doc, vElem, "VaccinationID", v.getId());
                appendTextElement(doc, vElem, "PetID", v.getPetId());
                if (v.getPetName() != null) appendTextElement(doc, vElem, "PetName", v.getPetName());
                appendTextElement(doc, vElem, "VaccineName", v.getVaccineName());
                appendTextElement(doc, vElem, "VaccinationDate", DateUtil.formatIsoDate(v.getVaccinationDate()));
                if (v.getNextDueDate() != null) appendTextElement(doc, vElem, "NextDueDate", DateUtil.formatIsoDate(v.getNextDueDate()));
                if (v.getVetName() != null) appendTextElement(doc, vElem, "Veterinarian", v.getVetName());
                if (v.getClinicName() != null) appendTextElement(doc, vElem, "Clinic", v.getClinicName());
                if (v.getNotes() != null) appendTextElement(doc, vElem, "Notes", v.getNotes());
                appendTextElement(doc, vElem, "Status", v.getStatus());
                vacsElem.appendChild(vElem);
            }
            rootElement.appendChild(vacsElem);
        }

        // 3. Medical Records
        if (medicalRecords != null && !medicalRecords.isEmpty()) {
            Element medsElem = doc.createElement("MedicalRecords");
            for (MedicalRecord m : medicalRecords) {
                Element mElem = doc.createElement("MedicalRecord");
                appendTextElement(doc, mElem, "RecordID", m.getId());
                appendTextElement(doc, mElem, "PetID", m.getPetId());
                if (m.getPetName() != null) appendTextElement(doc, mElem, "PetName", m.getPetName());
                appendTextElement(doc, mElem, "VisitDate", DateUtil.formatIsoDate(m.getVisitDate()));
                appendTextElement(doc, mElem, "Diagnosis", m.getDiagnosis());
                if (m.getTreatment() != null) appendTextElement(doc, mElem, "Treatment", m.getTreatment());
                if (m.getMedication() != null) appendTextElement(doc, mElem, "Medication", m.getMedication());
                if (m.getVetName() != null) appendTextElement(doc, mElem, "Veterinarian", m.getVetName());
                if (m.getClinicName() != null) appendTextElement(doc, mElem, "Clinic", m.getClinicName());
                if (m.getCost() != null) appendTextElement(doc, mElem, "Cost", m.getCost().toString());
                if (m.getFollowUpDate() != null) appendTextElement(doc, mElem, "FollowUpDate", DateUtil.formatIsoDate(m.getFollowUpDate()));
                if (m.getNotes() != null) appendTextElement(doc, mElem, "Notes", m.getNotes());
                medsElem.appendChild(mElem);
            }
            rootElement.appendChild(medsElem);
        }

        // 4. Appointments
        if (appointments != null && !appointments.isEmpty()) {
            Element apptsElem = doc.createElement("Appointments");
            for (Appointment a : appointments) {
                Element aElem = doc.createElement("Appointment");
                appendTextElement(doc, aElem, "AppointmentID", a.getId());
                appendTextElement(doc, aElem, "PetID", a.getPetId());
                if (a.getPetName() != null) appendTextElement(doc, aElem, "PetName", a.getPetName());
                appendTextElement(doc, aElem, "Title", a.getTitle());
                appendTextElement(doc, aElem, "AppointmentType", a.getAppointmentType());
                appendTextElement(doc, aElem, "AppointmentDate", DateUtil.formatTimestamp(a.getAppointmentDate()));
                if (a.getProviderName() != null) appendTextElement(doc, aElem, "ProviderName", a.getProviderName());
                if (a.getLocation() != null) appendTextElement(doc, aElem, "Location", a.getLocation());
                appendTextElement(doc, aElem, "Status", a.getStatus());
                if (a.getNotes() != null) appendTextElement(doc, aElem, "Notes", a.getNotes());
                apptsElem.appendChild(aElem);
            }
            rootElement.appendChild(apptsElem);
        }

        return transformToString(doc, includeDtd);
    }

    private static void appendTextElement(Document doc, Element parent, String tagName, String text) {
        if (text != null) {
            Element el = doc.createElement(tagName);
            el.appendChild(doc.createTextNode(text));
            parent.appendChild(el);
        }
    }

    private static String transformToString(Document doc, boolean includeDtd) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        if (includeDtd) {
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "petcare.dtd");
        }

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }
}
