package com.petcare.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class PetCareXMLValidator {

    public static class ValidationResult {
        private boolean valid = true;
        private String validationType;
        private List<String> errors = new ArrayList<>();
        private List<String> warnings = new ArrayList<>();

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }

        public String getValidationType() { return validationType; }
        public void setValidationType(String validationType) { this.validationType = validationType; }

        public List<String> getErrors() { return errors; }
        public List<String> getWarnings() { return warnings; }

        public void addError(String error) {
            this.errors.add(error);
            this.valid = false;
        }

        public void addWarning(String warning) {
            this.warnings.add(warning);
        }
    }

    /**
     * Validate XML content using DTD (Document Type Definition)
     */
    public static ValidationResult validateWithDTD(String xmlContent, InputStream dtdStream) {
        ValidationResult result = new ValidationResult();
        result.setValidationType("DTD (Document Type Definition)");

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException e) {
                    result.addWarning("Line " + e.getLineNumber() + ", Col " + e.getColumnNumber() + ": " + e.getMessage());
                }

                @Override
                public void error(SAXParseException e) {
                    result.addError("Line " + e.getLineNumber() + ", Col " + e.getColumnNumber() + ": " + e.getMessage());
                }

                @Override
                public void fatalError(SAXParseException e) {
                    result.addError("Fatal Line " + e.getLineNumber() + ", Col " + e.getColumnNumber() + ": " + e.getMessage());
                }
            });

            // Set entity resolver so that SYSTEM "petcare.dtd" resolves to our bundled DTD stream
            if (dtdStream != null) {
                builder.setEntityResolver((publicId, systemId) -> {
                    if (systemId != null && systemId.contains("petcare.dtd")) {
                        return new InputSource(dtdStream);
                    }
                    return null;
                });
            }

            builder.parse(new InputSource(new StringReader(xmlContent)));

        } catch (Exception e) {
            result.addError("DTD Validation Exception: " + e.getMessage());
        }

        return result;
    }

    /**
     * Validate XML content using XSD (XML Schema Definition)
     */
    public static ValidationResult validateWithXSD(String xmlContent, InputStream xsdStream) {
        ValidationResult result = new ValidationResult();
        result.setValidationType("XSD (XML Schema Definition)");

        if (xsdStream == null) {
            result.addError("XSD Schema file not found.");
            return result;
        }

        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(xsdStream));
            Validator validator = schema.newValidator();

            validator.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException e) {
                    result.addWarning("Line " + e.getLineNumber() + ", Col " + e.getColumnNumber() + ": " + e.getMessage());
                }

                @Override
                public void error(SAXParseException e) {
                    result.addError("Line " + e.getLineNumber() + ", Col " + e.getColumnNumber() + ": " + e.getMessage());
                }

                @Override
                public void fatalError(SAXParseException e) {
                    result.addError("Fatal Line " + e.getLineNumber() + ", Col " + e.getColumnNumber() + ": " + e.getMessage());
                }
            });

            String cleanXml = xmlContent.replaceAll("(?s)<!DOCTYPE[^>]*>", "");
            validator.validate(new StreamSource(new StringReader(cleanXml)));

        } catch (Exception e) {
            result.addError("Schema Validation Error: " + e.getMessage());
        }

        return result;
    }
}
