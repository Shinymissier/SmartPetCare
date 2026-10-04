package com.petcare;

import com.petcare.model.Pet;
import com.petcare.model.Vaccination;
import com.petcare.util.PasswordUtil;
import com.petcare.xml.PetCareXMLGenerator;
import com.petcare.xml.PetCareXMLParser;
import com.petcare.xml.PetCareXMLValidator;
import com.petcare.xml.PetCareXPathService;
import com.petcare.xml.PetCareXSLTService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class PetCareXMLTest {

    private String loadResource(String path) throws Exception {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(path)) {
            assertNotNull(in, "Resource not found: " + path);
            return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
        }
    }

    @Test
    @DisplayName("Verify Password Hashing and Checking")
    void testPasswordHashing() {
        String plain = "admin123";
        String hash = PasswordUtil.hashPassword(plain);
        assertNotNull(hash);
        assertTrue(PasswordUtil.checkPassword(plain, hash));
        assertFalse(PasswordUtil.checkPassword("wrongpassword", hash));
    }

    @Test
    @DisplayName("Verify Valid Sample Passes Both DTD and XSD Validation")
    void testValidSampleValidation() throws Exception {
        String xml = loadResource("samples/valid_petcare_sample.xml");

        // DTD
        InputStream dtdStream = getClass().getClassLoader().getResourceAsStream("schema/petcare.dtd");
        PetCareXMLValidator.ValidationResult dtdResult = PetCareXMLValidator.validateWithDTD(xml, dtdStream);
        assertTrue(dtdResult.isValid(), "DTD errors: " + dtdResult.getErrors());

        // XSD
        InputStream xsdStream = getClass().getClassLoader().getResourceAsStream("schema/petcare.xsd");
        PetCareXMLValidator.ValidationResult xsdResult = PetCareXMLValidator.validateWithXSD(xml, xsdStream);
        assertTrue(xsdResult.isValid(), "XSD errors: " + xsdResult.getErrors());
    }

    @Test
    @DisplayName("Verify Invalid Sample Fails Validation with Descriptive Errors")
    void testInvalidSampleValidation() throws Exception {
        String xml = loadResource("samples/invalid_petcare_sample.xml");

        InputStream xsdStream = getClass().getClassLoader().getResourceAsStream("schema/petcare.xsd");
        PetCareXMLValidator.ValidationResult xsdResult = PetCareXMLValidator.validateWithXSD(xml, xsdStream);
        assertFalse(xsdResult.isValid(), "Invalid sample should fail XSD validation");
        assertFalse(xsdResult.getErrors().isEmpty(), "Should produce error descriptions");
    }

    @Test
    @DisplayName("Verify DOM Parser Extracts Pet and Vaccination Objects")
    void testDOMParser() throws Exception {
        String xml = loadResource("samples/valid_petcare_sample.xml");
        PetCareXMLParser.ParseResult result = PetCareXMLParser.parseXML(xml);

        assertNotNull(result);
        assertEquals(2, result.getPets().size(), "Should extract 2 pets");
        assertEquals("Bruno", result.getPets().get(0).getName());
        assertEquals("Dog", result.getPets().get(0).getSpecies());
        assertEquals(new BigDecimal("28.50"), result.getPets().get(0).getWeight());

        assertEquals(3, result.getVaccinations().size(), "Should extract 3 vaccinations");
        assertEquals("Rabies", result.getVaccinations().get(0).getVaccineName());
        assertEquals("Completed", result.getVaccinations().get(0).getStatus());
    }

    @Test
    @DisplayName("Verify XPath Queries Retrieve Targeted Nodes")
    void testXPathQueries() throws Exception {
        String xml = loadResource("samples/valid_petcare_sample.xml");

        // 1. Find pet by name
        PetCareXPathService.XPathQueryResult petResult = PetCareXPathService.findPetByName(xml, "Bruno");
        assertEquals(1, petResult.getMatchCount());

        // 2. Retrieve all vaccinations
        PetCareXPathService.XPathQueryResult vacResult = PetCareXPathService.retrieveAllVaccinations(xml);
        assertEquals(3, vacResult.getMatchCount());

        // 3. Find overdue vaccinations
        PetCareXPathService.XPathQueryResult overdueResult = PetCareXPathService.findOverdueVaccinations(xml);
        assertEquals(1, overdueResult.getMatchCount());

        // 4. Retrieve medical records for Bruno
        PetCareXPathService.XPathQueryResult medResult = PetCareXPathService.retrieveMedicalRecordsForPet(xml, "Bruno");
        assertEquals(1, medResult.getMatchCount());
    }

    @Test
    @DisplayName("Verify XSLT Transformation into HTML Report")
    void testXSLTTransformation() throws Exception {
        String xml = loadResource("samples/valid_petcare_sample.xml");
        InputStream xsltStream = getClass().getClassLoader().getResourceAsStream("schema/petcare.xsl");

        String html = PetCareXSLTService.transformToHTML(xml, xsltStream);
        assertNotNull(html);
        assertTrue(html.contains("<html"), "Output should be HTML");
        assertTrue(html.contains("Bruno"), "Output should contain Pet Name");
        assertTrue(html.contains("Rabies"), "Output should contain Vaccine Name");
        assertTrue(html.contains("Vaccination History"), "Output should contain Vaccination Section");
    }

    @Test
    @DisplayName("Verify XML Generator Produces Valid XML from Java Model Objects")
    void testXMLGenerator() throws Exception {
        List<Pet> pets = new ArrayList<>();
        Pet p = new Pet();
        p.setId("p-100");
        p.setName("Milo");
        p.setSpecies("Dog");
        p.setBreed("Beagle");
        p.setDateOfBirth(Date.valueOf("2024-01-10"));
        p.setGender("Male");
        p.setWeight(new BigDecimal("12.50"));
        pets.add(p);

        List<Vaccination> vacs = new ArrayList<>();
        Vaccination v = new Vaccination();
        v.setId("v-100");
        v.setPetId("p-100");
        v.setPetName("Milo");
        v.setVaccineName("Parvovirus");
        v.setVaccinationDate(Date.valueOf(LocalDate.now()));
        v.setNextDueDate(Date.valueOf(LocalDate.now().plusYears(1)));
        v.setStatus("Completed");
        vacs.add(v);

        String generated = PetCareXMLGenerator.generateCompleteXML(pets, vacs, null, null, "Dr. Test", false);
        assertNotNull(generated);
        assertTrue(generated.contains("<Name>Milo</Name>"));
        assertTrue(generated.contains("<VaccineName>Parvovirus</VaccineName>"));

        // Validate generated XML against XSD
        InputStream xsdStream = getClass().getClassLoader().getResourceAsStream("schema/petcare.xsd");
        PetCareXMLValidator.ValidationResult vr = PetCareXMLValidator.validateWithXSD(generated, xsdStream);
        assertTrue(vr.isValid(), "Generated XML must be valid according to XSD: " + vr.getErrors());
    }
}
