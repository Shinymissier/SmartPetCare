package com.petcare.servlet;

import com.petcare.dao.PetDAO;
import com.petcare.dao.ReminderDAO;
import com.petcare.model.Pet;
import com.petcare.model.Reminder;
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

@WebServlet("/reminders")
public class ReminderServlet extends HttpServlet {

    private final ReminderDAO reminderDAO = new ReminderDAO();
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
        List<Reminder> reminders = reminderDAO.findAllByUserId(user.getId());

        req.setAttribute("pets", pets);
        req.setAttribute("reminders", reminders);
        req.getRequestDispatcher("/WEB-INF/views/reminders/list.jsp").forward(req, resp);
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

        if ("toggle".equalsIgnoreCase(action)) {
            String id = req.getParameter("id");
            reminderDAO.toggleComplete(id, user.getId());
            resp.sendRedirect(req.getContextPath() + "/reminders");
            return;
        }

        if ("delete".equalsIgnoreCase(action)) {
            String id = req.getParameter("id");
            reminderDAO.delete(id, user.getId());
            resp.sendRedirect(req.getContextPath() + "/reminders?message=Reminder+deleted");
            return;
        }

        String title = req.getParameter("title");
        String reminderDate = req.getParameter("reminderDate");
        String reminderType = req.getParameter("reminderType");
        String petId = req.getParameter("petId");

        if (title == null || reminderDate == null) {
            resp.sendRedirect(req.getContextPath() + "/reminders?error=Title+and+Date+are+required");
            return;
        }

        Reminder r = new Reminder();
        r.setUserId(user.getId());
        r.setTitle(title.trim());
        r.setReminderDate(DateUtil.parseDate(reminderDate));
        r.setReminderType(reminderType != null ? reminderType.trim() : "General");
        if (petId != null && !petId.trim().isEmpty()) {
            r.setPetId(petId.trim());
        }

        reminderDAO.insert(r);
        resp.sendRedirect(req.getContextPath() + "/reminders?message=Reminder+added");
    }
}
