package com.petcare.servlet;

import com.google.gson.Gson;
import com.petcare.dao.AppointmentDAO;
import com.petcare.dao.DashboardDAO;
import com.petcare.dao.PetDAO;
import com.petcare.dao.VaccinationDAO;
import com.petcare.model.Appointment;
import com.petcare.model.DashboardStats;
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

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private final DashboardDAO dashboardDAO = new DashboardDAO();
    private final PetDAO petDAO = new PetDAO();
    private final VaccinationDAO vaccinationDAO = new VaccinationDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String userId = user.getId();

        // Check if AJAX request for chart data
        String format = req.getParameter("format");
        if ("json".equalsIgnoreCase(format)) {
            DashboardStats stats = dashboardDAO.getDashboardStats(userId);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(gson.toJson(stats));
            return;
        }

        // Fetch dynamic stats from database
        DashboardStats stats = dashboardDAO.getDashboardStats(userId);
        List<Pet> pets = petDAO.findAllByUserId(userId);
        List<Vaccination> upcomingVaccinations = vaccinationDAO.getUpcomingVaccinations(userId);
        List<Vaccination> overdueVaccinations = vaccinationDAO.getOverdueVaccinations(userId);
        List<Appointment> upcomingAppointments = appointmentDAO.getUpcomingAppointments(userId);

        req.setAttribute("stats", stats);
        req.setAttribute("pets", pets);
        req.setAttribute("upcomingVaccinations", upcomingVaccinations);
        req.setAttribute("overdueVaccinations", overdueVaccinations);
        req.setAttribute("upcomingAppointments", upcomingAppointments);
        req.setAttribute("speciesJson", gson.toJson(stats.getSpeciesDistribution()));
        req.setAttribute("vaccinationJson", gson.toJson(stats.getVaccinationStatusCounts()));

        req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
    }
}
