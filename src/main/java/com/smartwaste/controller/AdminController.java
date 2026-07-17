package com.smartwaste.controller;

import com.smartwaste.entity.Complaint;
import com.smartwaste.entity.User;
import com.smartwaste.service.ComplaintService;
import com.smartwaste.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ComplaintService complaintService;

    @Autowired
    public AdminController(UserService userService, ComplaintService complaintService) {
        this.userService = userService;
        this.complaintService = complaintService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        long totalComplaints = complaintService.getAllComplaints().size();
        long pending = complaintService.getCountByStatus("PENDING");
        long assigned = complaintService.getCountByStatus("ASSIGNED") + complaintService.getCountByStatus("IN_PROGRESS");
        long completed = complaintService.getCountByStatus("COMPLETED");
        long workersCount = userService.getAllWorkers().size();

        model.addAttribute("totalComplaints", totalComplaints);
        model.addAttribute("pending", pending);
        model.addAttribute("assigned", assigned);
        model.addAttribute("completed", completed);
        model.addAttribute("workersCount", workersCount);

        List<Complaint> allComplaints = complaintService.getAllComplaints();
        model.addAttribute("recentComplaints", allComplaints.size() > 5 ? allComplaints.subList(0, 5) : allComplaints);

        return "admin/dashboard";
    }

    @GetMapping("/manage-complaints")
    public String manageComplaints(@RequestParam(value = "query", required = false) String query,
                                   @RequestParam(value = "status", required = false) String status,
                                   Model model) {
        List<Complaint> complaints = complaintService.searchAndFilter(query, status);
        List<User> workers = userService.getAllWorkers();

        model.addAttribute("complaints", complaints);
        model.addAttribute("workers", workers);
        model.addAttribute("query", query);
        model.addAttribute("status", status != null ? status : "ALL");

        return "admin/manage-complaints";
    }

    @PostMapping("/assign-worker")
    public String assignWorker(@RequestParam("complaintId") Long complaintId,
                               @RequestParam("workerId") Long workerId,
                               @RequestParam("priority") String priority) {
        try {
            complaintService.assignWorker(complaintId, workerId, priority);
            return "redirect:/admin/manage-complaints?assigned=true";
        } catch (Exception e) {
            return "redirect:/admin/manage-complaints?error=" + e.getMessage();
        }
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam("complaintId") Long complaintId,
                               @RequestParam("status") String status,
                               @RequestParam(value = "remarks", required = false) String remarks) {
        try {
            complaintService.updateStatus(complaintId, status, remarks);
            return "redirect:/admin/manage-complaints?updated=true";
        } catch (Exception e) {
            return "redirect:/admin/manage-complaints?error=" + e.getMessage();
        }
    }

    @GetMapping("/delete-complaint/{id}")
    public String deleteComplaint(@PathVariable("id") Long id) {
        try {
            complaintService.deleteComplaint(id);
            return "redirect:/admin/manage-complaints?deleted=true";
        } catch (Exception e) {
            return "redirect:/admin/manage-complaints?error=" + e.getMessage();
        }
    }

    @GetMapping("/manage-users")
    public String manageUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/manage-users";
    }

    @GetMapping("/complaints/{id}")
    public String viewComplaintDetails(@PathVariable("id") Long id, Model model) {
        Complaint complaint = complaintService.getComplaintById(id);
        List<User> workers = userService.getAllWorkers();
        model.addAttribute("complaint", complaint);
        model.addAttribute("workers", workers);
        return "admin/complaint-details";
    }
}
