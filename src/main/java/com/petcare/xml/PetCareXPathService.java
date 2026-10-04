package com.petcare.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class PetCareXPathService {

    public static class XPathQueryResult {
        private String expression;
        private int matchCount;
        private List<String> textResults = new ArrayList<>();
        private String rawXmlMatches;

        public String getExpression() { return expression; }
        public void setExpression(String expression) { this.expression = expression; }

        public int getMatchCount() { return matchCount; }
        public void setMatchCount(int matchCount) { this.matchCount = matchCount; }

        public List<String> getTextResults() { return textResults; }
        public void setTextResults(List<String> textResults) { this.textResults = textResults; }

        public String getRawXmlMatches() { return rawXmlMatches; }
        public void setRawXmlMatches(String rawXmlMatches) { this.rawXmlMatches = rawXmlMatches; }
    }

    /**
     * Evaluate an arbitrary or preset XPath expression over an XML document string
     */
    public static XPathQueryResult evaluateXPath(String xmlContent, String expression) throws Exception {
        XPathQueryResult result = new XPathQueryResult();
        result.setExpression(expression);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setFeature("http://xml.org/sax/features/validation", false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setEntityResolver((publicId, systemId) -> {
            InputStream in = PetCareXPathService.class.getClassLoader().getResourceAsStream("schema/petcare.dtd");
            return in != null ? new InputSource(in) : new InputSource(new StringReader(""));
        });
        Document doc = builder.parse(new InputSource(new StringReader(xmlContent)));

        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();

        NodeList nodes = (NodeList) xpath.compile(expression).evaluate(doc, XPathConstants.NODESET);
        result.setMatchCount(nodes.getLength());

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StringBuilder xmlBuffer = new StringBuilder();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String nodeContent = node.getTextContent() != null ? node.getTextContent().trim() : "";
            if (!nodeContent.isEmpty()) {
                result.getTextResults().add(nodeContent);
            }

            try {
                StringWriter sw = new StringWriter();
                transformer.transform(new DOMSource(node), new StreamResult(sw));
                xmlBuffer.append(sw.toString()).append("\n");
            } catch (Exception ignored) {}
        }

        result.setRawXmlMatches(xmlBuffer.toString().trim());
        return result;
    }

    // --- Predefined Queries ---

    /**
     * 1. Find a pet by name: //Pet[Name='Bruno']
     */
    public static XPathQueryResult findPetByName(String xmlContent, String petName) throws Exception {
        String expr = "//Pet[Name='" + petName.replace("'", "''") + "']";
        return evaluateXPath(xmlContent, expr);
    }

    /**
     * 2. Retrieve all vaccination records: //Vaccination
     */
    public static XPathQueryResult retrieveAllVaccinations(String xmlContent) throws Exception {
        String expr = "//Vaccination";
        return evaluateXPath(xmlContent, expr);
    }

    /**
     * 3. Find overdue vaccinations: //Vaccination[Status='Overdue']
     */
    public static XPathQueryResult findOverdueVaccinations(String xmlContent) throws Exception {
        String expr = "//Vaccination[Status='Overdue']";
        return evaluateXPath(xmlContent, expr);
    }

    /**
     * 4. Retrieve all medical records for a specific pet: //MedicalRecord[PetName='Bruno']
     */
    public static XPathQueryResult retrieveMedicalRecordsForPet(String xmlContent, String petName) throws Exception {
        String expr = "//MedicalRecord[PetName='" + petName.replace("'", "''") + "']";
        return evaluateXPath(xmlContent, expr);
    }

    /**
     * 5. Find appointments for a particular date or type: //Appointment[contains(AppointmentDate, '2026')]
     */
    public static XPathQueryResult findAppointmentsByDatePattern(String xmlContent, String datePattern) throws Exception {
        String expr = "//Appointment[contains(AppointmentDate, '" + datePattern.replace("'", "''") + "')]";
        return evaluateXPath(xmlContent, expr);
    }
}
