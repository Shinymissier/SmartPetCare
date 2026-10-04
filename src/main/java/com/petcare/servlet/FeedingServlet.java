package com.petcare.servlet;

import com.petcare.dao.FeedingDAO;
import com.petcare.dao.PetDAO;
import com.petcare.model.FeedingLog;
import com.petcare.model.FeedingSchedule;
import com.petcare.model.Pet;
import com.petcare.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/feeding")
public class FeedingServlet extends HttpServlet {

    private final FeedingDAO feedingDAO = new FeedingDAO();
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
        List<FeedingSchedule> schedules = feedingDAO.findAllByUserId(user.getId());
        List<FeedingLog> recentLogs = feedingDAO.getRecentLogs(user.getId(), 10);

        req.setAttribute("pets", pets);
        req.setAttribute("schedules", schedules);
        req.setAttribute("recentLogs", recentLogs);

        req.getRequestDispatcher("/WEB-INF/views/feeding/list.jsp").forward(req, resp);
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
            feedingDAO.delete(id, user.getId());
            resp.sendRedirect(req.getContextPath() + "/feeding?message=Feeding+schedule+deleted");
            return;
        }

        if ("log".equalsIgnoreCase(action)) {
            // Quick mark as Fed
            String petId = req.getParameter("petId");
            String scheduleId = req.getParameter("scheduleId");
            String status = req.getParameter("status"); // Fed, Skipped
            String notes = req.getParameter("notes");

            FeedingLog log = new FeedingLog();
            log.setPetId(petId);
            log.setScheduleId(scheduleId);
            log.setUserId(user.getId());
            log.setStatus(status != null ? status : "Fed");
            log.setNotes(notes);

            feedingDAO.logFeeding(log);
            resp.sendRedirect(req.getContextPath() + "/feeding?message=Feeding+logged+successfully");
            return;
        }

        // Add or update schedule
        String id = req.getParameter("id");
        String petId = req.getParameter("petId");
        String foodType = req.getParameter("foodType");
        String quantity = req.getParameter("quantity");
        String feedingTime = req.getParameter("feedingTime");
        String frequency = req.getParameter("frequency");
        String status = req.getParameter("status");
        String notes = req.getParameter("notes");

        if (petId == null || foodType == null || quantity == null || feedingTime == null) {
            resp.sendRedirect(req.getContextPath() + "/feeding?error=Missing+required+fields");
            return;
        }

        FeedingSchedule s = new FeedingSchedule();
        s.setUserId(user.getId());
        s.setPetId(petId.trim());
        s.setFoodType(foodType.trim());
        s.setQuantity(quantity.trim());
        s.setFeedingTime(feedingTime.trim());
        s.setFrequency(frequency != null ? frequency.trim() : "Daily");
        s.setStatus(status != null ? status.trim() : "Active");
        s.setNotes(notes != null ? notes.trim() : null);

        if (id == null || id.trim().isEmpty()) {
            feedingDAO.insert(s);
            resp.sendRedirect(req.getContextPath() + "/feeding?message=Feeding+schedule+added");
        } else {
            s.setId(id.trim());
            feedingDAO.update(s);
            resp.sendRedirect(req.getContextPath() + "/feeding?message=Feeding+schedule+updated");
        }
    }
}
