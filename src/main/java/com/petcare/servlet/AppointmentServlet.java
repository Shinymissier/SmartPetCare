package com.petcare.servlet;

import com.petcare.dao.AppointmentDAO;
import com.petcare.dao.PetDAO;
import com.petcare.model.Appointment;
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
import java.util.List;

@WebServlet("/appointments")
public class AppointmentServlet extends HttpServlet {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final PetDAO petDAO = new PetDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<Pet> pets = petDAO.findAllByUserId(user.getId());
        List<Appointment> appointments = appointmentDAO.findAllByUserId(user.getId());

        req.setAttribute("pets", pets);
        req.setAttribute("appointments", appointments);
        req.getRequestDispatcher("/WEB-INF/views/appointments/list.jsp").forward(req, resp);
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
            appointmentDAO.delete(id, user.getId());
            resp.sendRedirect(req.getContextPath() + "/appointments?message=Appointment+deleted");
            return;
        }

        if ("status".equalsIgnoreCase(action)) {
            String id = req.getParameter("id");
            String status = req.getParameter("status");
            Appointment a = appointmentDAO.findByIdAndUserId(id, user.getId());
            if (a != null) {
                a.setStatus(status);
                appointmentDAO.update(a);
            }
            resp.sendRedirect(req.getContextPath() + "/appointments?message=Status+updated");
            return;
        }

        String id = req.getParameter("id");
        String petId = req.getParameter("petId");
        String title = req.getParameter("title");
        String appointmentType = req.getParameter("appointmentType");
        String apptDate = req.getParameter("appointmentDate");
        String providerName = req.getParameter("providerName");
        String location = req.getParameter("location");
        String status = req.getParameter("status");
        String notes = req.getParameter("notes");

        if (petId == null || title == null || apptDate == null) {
            resp.sendRedirect(req.getContextPath() + "/appointments?error=Required+fields+missing");
            return;
        }

        Appointment a = new Appointment();
        a.setUserId(user.getId());
        a.setPetId(petId.trim());
        a.setTitle(title.trim());
        a.setAppointmentType(appointmentType != null ? appointmentType.trim() : "Veterinary");
        a.setAppointmentDate(DateUtil.parseTimestamp(apptDate));
        a.setProviderName(providerName != null ? providerName.trim() : null);
        a.setLocation(location != null ? location.trim() : null);
        a.setStatus(status != null ? status.trim() : "Scheduled");
        a.setNotes(notes != null ? notes.trim() : null);

        if (id == null || id.trim().isEmpty()) {
            appointmentDAO.insert(a);
            resp.sendRedirect(req.getContextPath() + "/appointments?message=Appointment+scheduled+successfully");
        } else {
            a.setId(id.trim());
            appointmentDAO.update(a);
            resp.sendRedirect(req.getContextPath() + "/appointments?message=Appointment+updated+successfully");
        }
    }
}
