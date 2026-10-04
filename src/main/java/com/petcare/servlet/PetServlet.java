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

@WebServlet("/pets")
public class PetServlet extends HttpServlet {

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
        if (action == null) action = "list";

        switch (action) {
            case "new":
                req.setAttribute("mode", "create");
                req.getRequestDispatcher("/WEB-INF/views/pets/form.jsp").forward(req, resp);
                break;

            case "edit":
                String editId = req.getParameter("id");
                Pet petToEdit = petDAO.findByIdAndUserId(editId, user.getId());
                if (petToEdit == null) {
                    resp.sendRedirect(req.getContextPath() + "/pets?error=Pet+not+found");
                    return;
                }
                req.setAttribute("mode", "edit");
                req.setAttribute("pet", petToEdit);
                req.getRequestDispatcher("/WEB-INF/views/pets/form.jsp").forward(req, resp);
                break;

            case "view":
                String viewId = req.getParameter("id");
                Pet pet = petDAO.findByIdAndUserId(viewId, user.getId());
                if (pet == null) {
                    resp.sendRedirect(req.getContextPath() + "/pets?error=Pet+not+found");
                    return;
                }
                List<Vaccination> vaccinations = vaccinationDAO.findByPetIdAndUserId(viewId, user.getId());
                List<MedicalRecord> medicalRecords = medicalRecordDAO.findByPetIdAndUserId(viewId, user.getId());
                
                req.setAttribute("pet", pet);
                req.setAttribute("vaccinations", vaccinations);
                req.setAttribute("medicalRecords", medicalRecords);
                req.getRequestDispatcher("/WEB-INF/views/pets/view.jsp").forward(req, resp);
                break;

            case "list":
            default:
                List<Pet> pets = petDAO.findAllByUserId(user.getId());
                req.setAttribute("pets", pets);
                req.getRequestDispatcher("/WEB-INF/views/pets/list.jsp").forward(req, resp);
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
            String petId = req.getParameter("id");
            boolean ok = petDAO.delete(petId, user.getId());
            if (ok) {
                resp.sendRedirect(req.getContextPath() + "/pets?message=Pet+removed+successfully");
            } else {
                resp.sendRedirect(req.getContextPath() + "/pets?error=Failed+to+remove+pet");
            }
            return;
        }

        // Create or Edit
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String species = req.getParameter("species");
        String breed = req.getParameter("breed");
        String dob = req.getParameter("dateOfBirth");
        String gender = req.getParameter("gender");
        String weightStr = req.getParameter("weight");
        String color = req.getParameter("color");
        String allergies = req.getParameter("allergies");
        String healthConditions = req.getParameter("healthConditions");
        String notes = req.getParameter("notes");
        String photoUrl = req.getParameter("photoUrl");

        if (name == null || name.trim().isEmpty() || species == null || species.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Pet name and species are required.");
            req.setAttribute("mode", id == null || id.isEmpty() ? "create" : "edit");
            req.getRequestDispatcher("/WEB-INF/views/pets/form.jsp").forward(req, resp);
            return;
        }

        Pet pet = new Pet();
        pet.setUserId(user.getId());
        pet.setName(name.trim());
        pet.setSpecies(species.trim());
        pet.setBreed(breed != null ? breed.trim() : null);
        pet.setDateOfBirth(DateUtil.parseDate(dob));
        pet.setGender(gender != null ? gender.trim() : "Unknown");
        
        try {
            if (weightStr != null && !weightStr.trim().isEmpty()) {
                pet.setWeight(new BigDecimal(weightStr.trim()));
            } else {
                pet.setWeight(BigDecimal.ZERO);
            }
        } catch (Exception e) {
            pet.setWeight(BigDecimal.ZERO);
        }

        pet.setColor(color != null ? color.trim() : null);
        pet.setAllergies(allergies != null ? allergies.trim() : null);
        pet.setHealthConditions(healthConditions != null ? healthConditions.trim() : null);
        pet.setNotes(notes != null ? notes.trim() : null);

        // Photo URL default avatars if empty
        if (photoUrl == null || photoUrl.trim().isEmpty()) {
            if ("Dog".equalsIgnoreCase(species)) {
                photoUrl = "https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=400&q=80";
            } else if ("Cat".equalsIgnoreCase(species)) {
                photoUrl = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=400&q=80";
            } else {
                photoUrl = "https://images.unsplash.com/photo-1535268647677-300dbf3d78d1?auto=format&fit=crop&w=400&q=80";
            }
        }
        pet.setPhotoUrl(photoUrl);

        if (id == null || id.trim().isEmpty()) {
            // Insert
            boolean ok = petDAO.insert(pet);
            if (ok) {
                resp.sendRedirect(req.getContextPath() + "/pets?message=Pet+registered+successfully");
            } else {
                resp.sendRedirect(req.getContextPath() + "/pets?error=Failed+to+register+pet");
            }
        } else {
            // Update
            pet.setId(id.trim());
            boolean ok = petDAO.update(pet);
            if (ok) {
                resp.sendRedirect(req.getContextPath() + "/pets?message=Pet+updated+successfully");
            } else {
                resp.sendRedirect(req.getContextPath() + "/pets?error=Failed+to+update+pet");
            }
        }
    }
}
