package com.petcare.xml;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

public class PetCareXSLTService {

    /**
     * Transform XML content using the provided XSLT stylesheet stream to HTML report
     */
    public static String transformToHTML(String xmlContent, InputStream xsltStream) throws Exception {
        if (xsltStream == null) {
            throw new IllegalArgumentException("XSLT Stylesheet input stream is null");
        }

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(new StreamSource(xsltStream));

        String cleanXml = xmlContent.replaceAll("(?s)<!DOCTYPE[^>]*>", "");
        StringWriter writer = new StringWriter();
        transformer.transform(new StreamSource(new StringReader(cleanXml)), new StreamResult(writer));

        return writer.toString();
    }
}
