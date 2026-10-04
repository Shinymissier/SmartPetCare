package com.petcare.servlet;

import com.petcare.dao.MedicalRecordDAO;
import com.petcare.dao.PetDAO;
import com.petcare.model.MedicalRecord;
import com.petcare.model.Pet;
import com.petcare.model.User;
import com.petcare.util.DateUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/medical-records")
public class MedicalRecordServlet extends HttpServlet {

    private final MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();
    private final PetDAO petDAO = new PetDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) action = "list";

        List<Pet> pets = petDAO.findAllByUserId(user.getId());
        req.setAttribute("pets", pets);

        switch (action) {
            case "new":
                req.setAttribute("mode", "create");
                String petIdParam = req.getParameter("petId");
                req.setAttribute("preselectedPetId", petIdParam);
                req.getRequestDispatcher("/WEB-INF/views/medical/form.jsp").forward(req, resp);
                break;

            case "edit":
                String editId = req.getParameter("id");
                MedicalRecord m = medicalRecordDAO.findByIdAndUserId(editId, user.getId());
                if (m == null) {
                    resp.sendRedirect(req.getContextPath() + "/medical-records?error=Record+not+found");
                    return;
                }
                req.setAttribute("mode", "edit");
                req.setAttribute("record", m);
                req.getRequestDispatcher("/WEB-INF/views/medical/form.jsp").forward(req, resp);
                break;

            case "list":
            default:
                String filterPetId = req.getParameter("petId");
                List<MedicalRecord> list;
                if (filterPetId != null && !filterPetId.trim().isEmpty()) {
                    list = medicalRecordDAO.findByPetIdAndUserId(filterPetId, user.getId());
                } else {
                    list = medicalRecordDAO.findAllByUserId(user.getId());
                }
                req.setAttribute("records", list);
                req.setAttribute("selectedPetId", filterPetId);
                req.getRequestDispatcher("/WEB-INF/views/medical/list.jsp").forward(req, resp);
                break;
        }
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

        if ("delete".equalsIgnoreCase(action)) {
            String id = req.getParameter("id");
            boolean ok = medicalRecordDAO.delete(id, user.getId());
            if (ok) {
                resp.sendRedirect(req.getContextPath() + "/medical-records?message=Medical+record+deleted");
            } else {
                resp.sendRedirect(req.getContextPath() + "/medical-records?error=Failed+to+delete+record");
            }
            return;
        }

        String id = req.getParameter("id");
        String petId = req.getParameter("petId");
        String visitDate = req.getParameter("visitDate");
        String diagnosis = req.getParameter("diagnosis");
        String treatment = req.getParameter("treatment");
        String medication = req.getParameter("medication");
        String vetName = req.getParameter("vetName");
        String clinicName = req.getParameter("clinicName");
        String costStr = req.getParameter("cost");
        String followUp = req.getParameter("followUpDate");
        String notes = req.getParameter("notes");

        if (petId == null || petId.trim().isEmpty() ||
            visitDate == null || visitDate.trim().isEmpty() ||
            diagnosis == null || diagnosis.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/medical-records?action=new&error=Pet,+Visit+Date,+and+Diagnosis+are+required");
            return;
        }

        MedicalRecord m = new MedicalRecord();
        m.setUserId(user.getId());
        m.setPetId(petId.trim());
        m.setVisitDate(DateUtil.parseDate(visitDate));
        m.setDiagnosis(diagnosis.trim());
        m.setTreatment(treatment != null ? treatment.trim() : null);
        m.setMedication(medication != null ? medication.trim() : null);
        m.setVetName(vetName != null ? vetName.trim() : null);
        m.setClinicName(clinicName != null ? clinicName.trim() : null);
        
        try {
            m.setCost(costStr != null && !costStr.trim().isEmpty() ? new BigDecimal(costStr.trim()) : BigDecimal.ZERO);
        } catch (Exception e) {
            m.setCost(BigDecimal.ZERO);
        }

        m.setFollowUpDate(DateUtil.parseDate(followUp));
        m.setNotes(notes != null ? notes.trim() : null);

        if (id == null || id.trim().isEmpty()) {
            boolean ok = medicalRecordDAO.insert(m);
            if (ok) {
                resp.sendRedirect(req.getContextPath() + "/medical-records?message=Medical+record+added+successfully");
            } else {
                resp.sendRedirect(req.getContextPath() + "/medical-records?error=Failed+to+save+medical+record");
            }
        } else {
            m.setId(id.trim());
            boolean ok = medicalRecordDAO.update(m);
            if (ok) {
                resp.sendRedirect(req.getContextPath() + "/medical-records?message=Medical+record+updated+successfully");
            } else {
                resp.sendRedirect(req.getContextPath() + "/medical-records?error=Failed+to+update+medical+record");
            }
        }
    }
}
