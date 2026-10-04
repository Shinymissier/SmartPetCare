package com.petcare.xml;

import com.petcare.model.Appointment;
import com.petcare.model.MedicalRecord;
import com.petcare.model.Pet;
import com.petcare.model.Vaccination;
import com.petcare.util.DateUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PetCareXMLParser {

    public static class ParseResult {
        private List<Pet> pets = new ArrayList<>();
        private List<Vaccination> vaccinations = new ArrayList<>();
        private List<MedicalRecord> medicalRecords = new ArrayList<>();
        private List<Appointment> appointments = new ArrayList<>();
        private List<String> parsingNotes = new ArrayList<>();

        public List<Pet> getPets() { return pets; }
        public List<Vaccination> getVaccinations() { return vaccinations; }
        public List<MedicalRecord> getMedicalRecords() { return medicalRecords; }
        public List<Appointment> getAppointments() { return appointments; }
        public List<String> getParsingNotes() { return parsingNotes; }
    }

    public static ParseResult parseXML(InputStream inputStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setFeature("http://xml.org/sax/features/validation", false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setEntityResolver((publicId, systemId) -> {
            InputStream in = PetCareXMLParser.class.getClassLoader().getResourceAsStream("schema/petcare.dtd");
            return in != null ? new InputSource(in) : new InputSource(new StringReader(""));
        });
        Document doc = builder.parse(inputStream);
        return extractData(doc);
    }

    public static ParseResult parseXML(String xmlContent) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setFeature("http://xml.org/sax/features/validation", false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setEntityResolver((publicId, systemId) -> {
            InputStream in = PetCareXMLParser.class.getClassLoader().getResourceAsStream("schema/petcare.dtd");
            return in != null ? new InputSource(in) : new InputSource(new StringReader(""));
        });
        Document doc = builder.parse(new InputSource(new StringReader(xmlContent)));
        return extractData(doc);
    }

    private static ParseResult extractData(Document doc) {
        ParseResult result = new ParseResult();
        doc.getDocumentElement().normalize();

        // 1. Extract Pets using DOM NodeList
        NodeList petNodes = doc.getElementsByTagName("Pet");
        for (int i = 0; i < petNodes.getLength(); i++) {
            Node node = petNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) node;
                Pet pet = new Pet();
                pet.setId(getTagValue(el, "PetID"));
                pet.setName(getTagValue(el, "Name"));
                pet.setSpecies(getTagValue(el, "Species"));
                pet.setBreed(getTagValue(el, "Breed"));
                pet.setDateOfBirth(DateUtil.parseDate(getTagValue(el, "DateOfBirth")));
                pet.setGender(getTagValue(el, "Gender"));
                
                String weightStr = getTagValue(el, "Weight");
                if (weightStr != null && !weightStr.isEmpty()) {
                    try {
                        pet.setWeight(new BigDecimal(weightStr));
                    } catch (NumberFormatException ignored) {}
                }
                
                pet.setColor(getTagValue(el, "Color"));
                pet.setAllergies(getTagValue(el, "Allergies"));
                pet.setHealthConditions(getTagValue(el, "HealthConditions"));
                pet.setNotes(getTagValue(el, "Notes"));

                result.getPets().add(pet);
            }
        }
        result.getParsingNotes().add("Parsed " + result.getPets().size() + " pet(s) via DOM Parser.");

        // 2. Extract Vaccinations
        NodeList vacNodes = doc.getElementsByTagName("Vaccination");
        for (int i = 0; i < vacNodes.getLength(); i++) {
            Node node = vacNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) node;
                Vaccination v = new Vaccination();
                v.setId(getTagValue(el, "VaccinationID"));
                v.setPetId(getTagValue(el, "PetID"));
                v.setPetName(getTagValue(el, "PetName"));
                v.setVaccineName(getTagValue(el, "VaccineName"));
                v.setVaccinationDate(DateUtil.parseDate(getTagValue(el, "VaccinationDate")));
                v.setNextDueDate(DateUtil.parseDate(getTagValue(el, "NextDueDate")));
                v.setVetName(getTagValue(el, "Veterinarian"));
                v.setClinicName(getTagValue(el, "Clinic"));
                v.setNotes(getTagValue(el, "Notes"));
                
                String status = getTagValue(el, "Status");
                if (status != null && !status.isEmpty()) {
                    v.setStatus(status);
                } else {
                    v.recalculateStatus();
                }

                result.getVaccinations().add(v);
            }
        }
        result.getParsingNotes().add("Parsed " + result.getVaccinations().size() + " vaccination record(s) via DOM Parser.");

        // 3. Extract Medical Records
        NodeList medNodes = doc.getElementsByTagName("MedicalRecord");
        for (int i = 0; i < medNodes.getLength(); i++) {
            Node node = medNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) node;
                MedicalRecord m = new MedicalRecord();
                m.setId(getTagValue(el, "RecordID"));
                m.setPetId(getTagValue(el, "PetID"));
                m.setPetName(getTagValue(el, "PetName"));
                m.setVisitDate(DateUtil.parseDate(getTagValue(el, "VisitDate")));
                m.setDiagnosis(getTagValue(el, "Diagnosis"));
                m.setTreatment(getTagValue(el, "Treatment"));
                m.setMedication(getTagValue(el, "Medication"));
                m.setVetName(getTagValue(el, "Veterinarian"));
                m.setClinicName(getTagValue(el, "Clinic"));

                String costStr = getTagValue(el, "Cost");
                if (costStr != null && !costStr.isEmpty()) {
                    try {
                        m.setCost(new BigDecimal(costStr));
                    } catch (NumberFormatException ignored) {}
                }

                m.setFollowUpDate(DateUtil.parseDate(getTagValue(el, "FollowUpDate")));
                m.setNotes(getTagValue(el, "Notes"));
                result.getMedicalRecords().add(m);
            }
        }
        result.getParsingNotes().add("Parsed " + result.getMedicalRecords().size() + " medical record(s) via DOM Parser.");

        // 4. Extract Appointments
        NodeList apptNodes = doc.getElementsByTagName("Appointment");
        for (int i = 0; i < apptNodes.getLength(); i++) {
            Node node = apptNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) node;
                Appointment a = new Appointment();
                a.setId(getTagValue(el, "AppointmentID"));
                a.setPetId(getTagValue(el, "PetID"));
                a.setPetName(getTagValue(el, "PetName"));
                a.setTitle(getTagValue(el, "Title"));
                a.setAppointmentType(getTagValue(el, "AppointmentType"));
                a.setAppointmentDate(DateUtil.parseTimestamp(getTagValue(el, "AppointmentDate")));
                a.setProviderName(getTagValue(el, "ProviderName"));
                a.setLocation(getTagValue(el, "Location"));
                a.setStatus(getTagValue(el, "Status"));
                a.setNotes(getTagValue(el, "Notes"));
                result.getAppointments().add(a);
            }
        }

        return result;
    }

    private static String getTagValue(Element element, String tagName) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            Node node = list.item(0);
            if (node != null && node.getFirstChild() != null) {
                return node.getFirstChild().getNodeValue().trim();
            }
        }
        return null;
    }
}
