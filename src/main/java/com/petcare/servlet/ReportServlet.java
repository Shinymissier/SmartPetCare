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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/reports")
public class ReportServlet extends HttpServlet {

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

        String reportType = req.getParameter("type");
        if (reportType == null) reportType = "health";

        String petId = req.getParameter("petId");
        List<Pet> pets = petDAO.findAllByUserId(user.getId());

        List<Vaccination> vaccinations;
        List<MedicalRecord> medicalRecords;
        List<Appointment> appointments;

        if (petId != null && !petId.trim().isEmpty()) {
            vaccinations = vaccinationDAO.findByPetIdAndUserId(petId, user.getId());
            medicalRecords = medicalRecordDAO.findByPetIdAndUserId(petId, user.getId());
            req.setAttribute("selectedPet", petDAO.findByIdAndUserId(petId, user.getId()));
        } else {
            vaccinations = vaccinationDAO.findAllByUserId(user.getId());
            medicalRecords = medicalRecordDAO.findAllByUserId(user.getId());
        }

        appointments = appointmentDAO.findAllByUserId(user.getId());

        req.setAttribute("pets", pets);
        req.setAttribute("reportType", reportType);
        req.setAttribute("selectedPetId", petId);
        req.setAttribute("vaccinations", vaccinations);
        req.setAttribute("medicalRecords", medicalRecords);
        req.setAttribute("appointments", appointments);

        req.getRequestDispatcher("/WEB-INF/views/reports/index.jsp").forward(req, resp);
    }
}
