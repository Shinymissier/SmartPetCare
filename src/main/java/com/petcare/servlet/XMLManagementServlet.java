package com.petcare.servlet;

import com.petcare.dao.AppointmentDAO;
import com.petcare.dao.MedicalRecordDAO;
import com.petcare.dao.PetDAO;
import com.petcare.dao.VaccinationDAO;
import com.petcare.model.Appointment;
import com.petcare.model.MedicalRecord;
import com.petcare.model.Pet;
import com.petcare.model.User;
import com.petcare.model.Vaccination;
import com.petcare.xml.PetCareXMLGenerator;
import com.petcare.xml.PetCareXMLParser;
import com.petcare.xml.PetCareXMLValidator;
import com.petcare.xml.PetCareXPathService;
import com.petcare.xml.PetCareXSLTService;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/xml-management")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1 MB
    maxFileSize = 10 * 1024 * 1024,       // 10 MB
    maxRequestSize = 20 * 1024 * 1024     // 20 MB
)
public class XMLManagementServlet extends HttpServlet {

    private final PetDAO petDAO = new PetDAO();
    private final VaccinationDAO vaccinationDAO = new VaccinationDAO();
    private final MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) action = "view";

        try {
            if ("export-download".equalsIgnoreCase(action)) {
                handleExportDownload(req, resp, user);
                return;
            } else if ("xslt-report".equalsIgnoreCase(action)) {
                handleXSLTReport(req, resp, user);
                return;
            } else if ("view-xml".equalsIgnoreCase(action)) {
                handleViewXML(req, resp, user);
                return;
            } else if ("sample-xml".equalsIgnoreCase(action)) {
                handleLoadSample(req, resp);
                return;
            }
        } catch (Exception e) {
            req.setAttribute("errorMessage", "XML operation error: " + e.getMessage());
        }

        // Default view: load current user's generated XML as preview
        prepareDefaultView(req, user);
        req.getRequestDispatcher("/WEB-INF/views/xml/management.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) action = "validate";

        try {
            switch (action) {
                case "validate":
                    handleValidation(req, resp, user);
                    break;

                case "xpath":
                    handleXPathQuery(req, resp, user);
                    break;

                case "xslt-transform":
                    handleXSLTTransform(req, resp, user);
                    break;

                case "import-upload":
                    handleImportUpload(req, resp, user);
                    break;

                case "import-confirm":
                    handleImportConfirm(req, resp, user);
                    break;

                default:
                    resp.sendRedirect(req.getContextPath() + "/xml-management");
                    break;
            }
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error processing request: " + e.getMessage());
            prepareDefaultView(req, user);
            req.getRequestDispatcher("/WEB-INF/views/xml/management.jsp").forward(req, resp);
        }
    }

    private void prepareDefaultView(HttpServletRequest req, User user) {
        try {
            List<Pet> pets = petDAO.findAllByUserId(user.getId());
            List<Vaccination> vaccinations = vaccinationDAO.findAllByUserId(user.getId());
            List<MedicalRecord> medicalRecords = medicalRecordDAO.findAllByUserId(user.getId());
            List<Appointment> appointments = appointmentDAO.findAllByUserId(user.getId());

            String xml = PetCareXMLGenerator.generateCompleteXML(pets, vaccinations, medicalRecords, appointments, user.getFullName(), true);
            req.setAttribute("currentXml", xml);
            req.setAttribute("pets", pets);
        } catch (Exception e) {
            req.setAttribute("currentXml", "<!-- Error generating XML: " + e.getMessage() + " -->");
        }
    }

    private void handleExportDownload(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        String type = req.getParameter("type"); // "all", "pets", "vaccinations", "medical", "appointments"
        boolean validated = "true".equalsIgnoreCase(req.getParameter("validated"));

        List<Pet> pets = "vaccinations".equals(type) || "medical".equals(type) || "appointments".equals(type) ? null : petDAO.findAllByUserId(user.getId());
        List<Vaccination> vacs = "pets".equals(type) || "medical".equals(type) || "appointments".equals(type) ? null : vaccinationDAO.findAllByUserId(user.getId());
        List<MedicalRecord> meds = "pets".equals(type) || "vaccinations".equals(type) || "appointments".equals(type) ? null : medicalRecordDAO.findAllByUserId(user.getId());
        List<Appointment> appts = "pets".equals(type) || "vaccinations".equals(type) || "medical".equals(type) ? null : appointmentDAO.findAllByUserId(user.getId());

        String xml = PetCareXMLGenerator.generateCompleteXML(pets, vacs, meds, appts, user.getFullName(), true);

        if (validated) {
            // Verify with XSD before download
            InputStream xsdStream = getClass().getClassLoader().getResourceAsStream("schema/petcare.xsd");
            PetCareXMLValidator.ValidationResult vr = PetCareXMLValidator.validateWithXSD(xml, xsdStream);
            if (!vr.isValid()) {
                resp.sendRedirect(req.getContextPath() + "/xml-management?error=Export+failed+validation+check");
                return;
            }
        }

        String filename = "petcare_" + (type != null ? type : "export") + "_" + System.currentTimeMillis() + ".xml";
        resp.setContentType("application/xml");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        resp.getWriter().write(xml);
    }

    private void handleViewXML(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        List<Pet> pets = petDAO.findAllByUserId(user.getId());
        List<Vaccination> vacs = vaccinationDAO.findAllByUserId(user.getId());
        List<MedicalRecord> meds = medicalRecordDAO.findAllByUserId(user.getId());
        List<Appointment> appts = appointmentDAO.findAllByUserId(user.getId());

        String xml = PetCareXMLGenerator.generateCompleteXML(pets, vacs, meds, appts, user.getFullName(), true);
        resp.setContentType("text/xml");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(xml);
    }

    private void handleXSLTReport(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        List<Pet> pets = petDAO.findAllByUserId(user.getId());
        List<Vaccination> vacs = vaccinationDAO.findAllByUserId(user.getId());
        List<MedicalRecord> meds = medicalRecordDAO.findAllByUserId(user.getId());
        List<Appointment> appts = appointmentDAO.findAllByUserId(user.getId());

        String xml = PetCareXMLGenerator.generateCompleteXML(pets, vacs, meds, appts, user.getFullName(), false);
        InputStream xsltStream = getClass().getClassLoader().getResourceAsStream("schema/petcare.xsl");

        String html = PetCareXSLTService.transformToHTML(xml, xsltStream);
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(html);
    }

    private void handleValidation(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        String xmlContent = req.getParameter("xmlContent");
        String validatorType = req.getParameter("validatorType"); // "dtd", "xsd", "both"

        if (xmlContent == null || xmlContent.trim().isEmpty()) {
            prepareDefaultView(req, user);
            req.setAttribute("errorMessage", "XML content cannot be empty.");
            req.getRequestDispatcher("/WEB-INF/views/xml/management.jsp").forward(req, resp);
            return;
        }

        PetCareXMLValidator.ValidationResult dtdResult = null;
        PetCareXMLValidator.ValidationResult xsdResult = null;

        if ("dtd".equalsIgnoreCase(validatorType) || "both".equalsIgnoreCase(validatorType)) {
            InputStream dtdStream = getClass().getClassLoader().getResourceAsStream("schema/petcare.dtd");
            dtdResult = PetCareXMLValidator.validateWithDTD(xmlContent, dtdStream);
        }

        if ("xsd".equalsIgnoreCase(validatorType) || "both".equalsIgnoreCase(validatorType)) {
            InputStream xsdStream = getClass().getClassLoader().getResourceAsStream("schema/petcare.xsd");
            xsdResult = PetCareXMLValidator.validateWithXSD(xmlContent, xsdStream);
        }

        prepareDefaultView(req, user);
        req.setAttribute("xmlInput", xmlContent);
        req.setAttribute("dtdResult", dtdResult);
        req.setAttribute("xsdResult", xsdResult);
        req.setAttribute("activeTab", "validation");
        req.getRequestDispatcher("/WEB-INF/views/xml/management.jsp").forward(req, resp);
    }

    private void handleXPathQuery(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        String xmlContent = req.getParameter("xmlContent");
        String queryPreset = req.getParameter("queryPreset");
        String customXPath = req.getParameter("customXPath");
        String petNameParam = req.getParameter("petNameParam");

        if (xmlContent == null || xmlContent.trim().isEmpty()) {
            prepareDefaultView(req, user);
            req.setAttribute("errorMessage", "Please provide XML content to query.");
            req.getRequestDispatcher("/WEB-INF/views/xml/management.jsp").forward(req, resp);
            return;
        }

        PetCareXPathService.XPathQueryResult result;

        if ("findPetByName".equals(queryPreset)) {
            String name = (petNameParam != null && !petNameParam.isEmpty()) ? petNameParam : "Bruno";
            result = PetCareXPathService.findPetByName(xmlContent, name);
        } else if ("retrieveAllVaccinations".equals(queryPreset)) {
            result = PetCareXPathService.retrieveAllVaccinations(xmlContent);
        } else if ("findOverdueVaccinations".equals(queryPreset)) {
            result = PetCareXPathService.findOverdueVaccinations(xmlContent);
        } else if ("retrieveMedicalRecordsForPet".equals(queryPreset)) {
            String name = (petNameParam != null && !petNameParam.isEmpty()) ? petNameParam : "Bruno";
            result = PetCareXPathService.retrieveMedicalRecordsForPet(xmlContent, name);
        } else if ("findAppointmentsByDate".equals(queryPreset)) {
            result = PetCareXPathService.findAppointmentsByDatePattern(xmlContent, "2026");
        } else {
            // Custom expression
            String expr = (customXPath != null && !customXPath.trim().isEmpty()) ? customXPath.trim() : "//Pet";
            result = PetCareXPathService.evaluateXPath(xmlContent, expr);
        }

        prepareDefaultView(req, user);
        req.setAttribute("xmlInput", xmlContent);
        req.setAttribute("xpathResult", result);
        req.setAttribute("activeTab", "xpath");
        req.getRequestDispatcher("/WEB-INF/views/xml/management.jsp").forward(req, resp);
    }

    private void handleXSLTTransform(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        String xmlContent = req.getParameter("xmlContent");
        if (xmlContent == null || xmlContent.trim().isEmpty()) {
            prepareDefaultView(req, user);
            xmlContent = (String) req.getAttribute("currentXml");
        }

        InputStream xsltStream = getClass().getClassLoader().getResourceAsStream("schema/petcare.xsl");
        String transformedHtml = PetCareXSLTService.transformToHTML(xmlContent, xsltStream);

        prepareDefaultView(req, user);
        req.setAttribute("xmlInput", xmlContent);
        req.setAttribute("transformedHtml", transformedHtml);
        req.setAttribute("activeTab", "xslt");
        req.getRequestDispatcher("/WEB-INF/views/xml/management.jsp").forward(req, resp);
    }

    private void handleImportUpload(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        Part filePart = req.getPart("xmlFile");
        String xmlString = "";

        if (filePart != null && filePart.getSize() > 0) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(filePart.getInputStream(), StandardCharsets.UTF_8))) {
                xmlString = reader.lines().collect(Collectors.joining("\n"));
            }
        } else {
            xmlString = req.getParameter("xmlDirectInput");
        }

        if (xmlString == null || xmlString.trim().isEmpty()) {
            prepareDefaultView(req, user);
            req.setAttribute("errorMessage", "No XML file uploaded or content provided.");
            req.getRequestDispatcher("/WEB-INF/views/xml/management.jsp").forward(req, resp);
            return;
        }

        // 1. Validate structure using XSD schema
        InputStream xsdStream = getClass().getClassLoader().getResourceAsStream("schema/petcare.xsd");
        PetCareXMLValidator.ValidationResult vResult = PetCareXMLValidator.validateWithXSD(xmlString, xsdStream);

        // 2. Parse using DOM Parser
        PetCareXMLParser.ParseResult parseResult = null;
        try {
            parseResult = PetCareXMLParser.parseXML(xmlString);
        } catch (Exception e) {
            vResult.addError("DOM Parsing Exception: " + e.getMessage());
        }

        // Store in session for confirm step
        HttpSession session = req.getSession();
        session.setAttribute("pendingImportXML", xmlString);
        session.setAttribute("pendingParseResult", parseResult);

        prepareDefaultView(req, user);
        req.setAttribute("xmlInput", xmlString);
        req.setAttribute("importValidation", vResult);
        req.setAttribute("parseResult", parseResult);
        req.setAttribute("activeTab", "import");
        req.getRequestDispatcher("/WEB-INF/views/xml/management.jsp").forward(req, resp);
    }

    private void handleImportConfirm(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        HttpSession session = req.getSession(false);
        PetCareXMLParser.ParseResult parseResult = (session != null) ? (PetCareXMLParser.ParseResult) session.getAttribute("pendingParseResult") : null;

        if (parseResult == null) {
            resp.sendRedirect(req.getContextPath() + "/xml-management?error=No+pending+import+found+to+save");
            return;
        }

        int petsSaved = 0;
        int vacsSaved = 0;
        int medsSaved = 0;

        // Save valid records into Supabase / DB
        for (Pet p : parseResult.getPets()) {
            p.setUserId(user.getId());
            // Check if pet by this name already exists
            Pet existing = petDAO.findByNameAndUserId(p.getName(), user.getId());
            if (existing == null) {
                if (petDAO.insert(p)) petsSaved++;
            } else {
                p.setId(existing.getId());
                if (petDAO.update(p)) petsSaved++;
            }
        }

        for (Vaccination v : parseResult.getVaccinations()) {
            v.setUserId(user.getId());
            // Match with pet by name or use first pet
            if (v.getPetId() == null || v.getPetId().isEmpty()) {
                Pet pet = petDAO.findByNameAndUserId(v.getPetName(), user.getId());
                if (pet != null) {
                    v.setPetId(pet.getId());
                } else {
                    List<Pet> pets = petDAO.findAllByUserId(user.getId());
                    if (!pets.isEmpty()) v.setPetId(pets.get(0).getId());
                }
            }
            if (v.getPetId() != null) {
                if (vaccinationDAO.insert(v)) vacsSaved++;
            }
        }

        for (MedicalRecord m : parseResult.getMedicalRecords()) {
            m.setUserId(user.getId());
            if (m.getPetId() == null || m.getPetId().isEmpty()) {
                Pet pet = petDAO.findByNameAndUserId(m.getPetName(), user.getId());
                if (pet != null) {
                    m.setPetId(pet.getId());
                } else {
                    List<Pet> pets = petDAO.findAllByUserId(user.getId());
                    if (!pets.isEmpty()) m.setPetId(pets.get(0).getId());
                }
            }
            if (m.getPetId() != null) {
                if (medicalRecordDAO.insert(m)) medsSaved++;
            }
        }

        session.removeAttribute("pendingImportXML");
        session.removeAttribute("pendingParseResult");

        resp.sendRedirect(req.getContextPath() + "/xml-management?message=Successfully+imported+" +
                petsSaved + "+pets,+" + vacsSaved + "+vaccinations,+and+" + medsSaved + "+medical+records+into+database!");
    }

    private void handleLoadSample(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String type = req.getParameter("type"); // "valid" or "invalid"
        String resourcePath = "valid".equalsIgnoreCase(type) ? "samples/valid_petcare_sample.xml" : "samples/invalid_petcare_sample.xml";

        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (in != null) {
                String content = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                        .lines().collect(Collectors.joining("\n"));
                resp.setContentType("text/plain");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write(content);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("Sample XML file not found.");
            }
        }
    }
}
