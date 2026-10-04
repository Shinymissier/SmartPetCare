package com.petcare.servlet;

import com.petcare.dao.PetDAO;
import com.petcare.dao.VaccinationDAO;
import com.petcare.model.Pet;
import com.petcare.model.User;
import com.petcare.model.Vaccination;
import com.petcare.util.DateUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/vaccinations")
public class VaccinationServlet extends HttpServlet {

    private final VaccinationDAO vaccinationDAO = new VaccinationDAO();
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
                String preselectedPetId = req.getParameter("petId");
                req.setAttribute("preselectedPetId", preselectedPetId);
                req.getRequestDispatcher("/WEB-INF/views/vaccinations/form.jsp").forward(req, resp);
                break;

            case "edit":
                String editId = req.getParameter("id");
                Vaccination v = vaccinationDAO.findByIdAndUserId(editId, user.getId());
                if (v == null) {
                    resp.sendRedirect(req.getContextPath() + "/vaccinations?error=Vaccination+record+not+found");
                    return;
                }
                req.setAttribute("mode", "edit");
                req.setAttribute("vaccination", v);
                req.getRequestDispatcher("/WEB-INF/views/vaccinations/form.jsp").forward(req, resp);
                break;

            case "list":
            default:
                String filter = req.getParameter("filter"); // 'all', 'upcoming', 'overdue', 'pet'
                String filterPetId = req.getParameter("petId");
                List<Vaccination> list;

                if ("upcoming".equalsIgnoreCase(filter)) {
                    list = vaccinationDAO.getUpcomingVaccinations(user.getId());
                } else if ("overdue".equalsIgnoreCase(filter)) {
                    list = vaccinationDAO.getOverdueVaccinations(user.getId());
                } else if (filterPetId != null && !filterPetId.trim().isEmpty()) {
                    list = vaccinationDAO.findByPetIdAndUserId(filterPetId, user.getId());
                } else {
                    list = vaccinationDAO.findAllByUserId(user.getId());
                }

                req.setAttribute("vaccinations", list);
                req.setAttribute("activeFilter", filter != null ? filter : "all");
                req.setAttribute("selectedPetId", filterPetId);
                req.getRequestDispatcher("/WEB-INF/views/vaccinations/list.jsp").forward(req, resp);
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
            boolean ok = vaccinationDAO.delete(id, user.getId());
            if (ok) {
                resp.sendRedirect(req.getContextPath() + "/vaccinations?message=Vaccination+record+deleted");
            } else {
                resp.sendRedirect(req.getContextPath() + "/vaccinations?error=Failed+to+delete+record");
            }
            return;
        }

        String id = req.getParameter("id");
        String petId = req.getParameter("petId");
        String vaccineName = req.getParameter("vaccineName");
        String vacDate = req.getParameter("vaccinationDate");
        String dueDate = req.getParameter("nextDueDate");
        String vetName = req.getParameter("vetName");
        String clinicName = req.getParameter("clinicName");
        String notes = req.getParameter("notes");
        String manualStatus = req.getParameter("status");

        if (petId == null || petId.trim().isEmpty() ||
            vaccineName == null || vaccineName.trim().isEmpty() ||
            vacDate == null || vacDate.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/vaccinations?action=new&error=Pet,+Vaccine+name,+and+Date+are+required");
            return;
        }

        Vaccination v = new Vaccination();
        v.setUserId(user.getId());
        v.setPetId(petId.trim());
        v.setVaccineName(vaccineName.trim());
        v.setVaccinationDate(DateUtil.parseDate(vacDate));
        v.setNextDueDate(DateUtil.parseDate(dueDate));
        v.setVetName(vetName != null ? vetName.trim() : null);
        v.setClinicName(clinicName != null ? clinicName.trim() : null);
        v.setNotes(notes != null ? notes.trim() : null);

        // Auto calculate status or respect override
        if (manualStatus != null && !manualStatus.trim().isEmpty()) {
            v.setStatus(manualStatus.trim());
        } else {
            v.recalculateStatus();
        }

        if (id == null || id.trim().isEmpty()) {
            boolean ok = vaccinationDAO.insert(v);
            if (ok) {
                resp.sendRedirect(req.getContextPath() + "/vaccinations?message=Vaccination+added+successfully");
            } else {
                resp.sendRedirect(req.getContextPath() + "/vaccinations?error=Failed+to+save+vaccination");
            }
        } else {
            v.setId(id.trim());
            boolean ok = vaccinationDAO.update(v);
            if (ok) {
                resp.sendRedirect(req.getContextPath() + "/vaccinations?message=Vaccination+updated+successfully");
            } else {
                resp.sendRedirect(req.getContextPath() + "/vaccinations?error=Failed+to+update+vaccination");
            }
        }
    }
}
