package com.smartwaste.controller;

import com.smartwaste.entity.Complaint;
import com.smartwaste.entity.User;
import com.smartwaste.service.ComplaintService;
import com.smartwaste.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/worker")
public class WorkerController {

    private final UserService userService;
    private final ComplaintService complaintService;

    @Autowired
    public WorkerController(UserService userService, ComplaintService complaintService) {
        this.userService = userService;
        this.complaintService = complaintService;
    }

    @GetMapping("/dashboard")
    public String workerDashboard(Principal principal, Model model) {
        String email = principal.getName();
        User worker = userService.findByEmail(email);
        List<Complaint> complaints = complaintService.getWorkerComplaints(email);

        long totalCount = complaints.size();
        long pendingCount = complaints.stream().filter(c -> "ASSIGNED".equals(c.getStatus())).count();
        long progressCount = complaints.stream().filter(c -> "IN_PROGRESS".equals(c.getStatus())).count();
        long completedCount = complaints.stream().filter(c -> "COMPLETED".equals(c.getStatus())).count();

        model.addAttribute("worker", worker);
        model.addAttribute("complaints", complaints);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("progressCount", progressCount);
        model.addAttribute("completedCount", completedCount);

        return "worker/dashboard";
    }

    @GetMapping("/complaints/{id}")
    public String complaintDetails(@PathVariable("id") Long id, Principal principal, Model model) {
        Complaint complaint = complaintService.getComplaintById(id);
        
        // Safety check to ensure worker is authorized to view this complaint
        if (complaint.getWorker() == null || !complaint.getWorker().getEmail().equals(principal.getName())) {
            return "redirect:/worker/dashboard";
        }

        model.addAttribute("complaint", complaint);
        return "worker/assigned-complaints"; // This page handles detailed view and completion
    }

    @PostMapping("/start-work")
    public String startWork(@RequestParam("complaintId") Long complaintId, Principal principal) {
        Complaint complaint = complaintService.getComplaintById(complaintId);
        if (complaint.getWorker() == null || !complaint.getWorker().getEmail().equals(principal.getName())) {
            return "redirect:/worker/dashboard";
        }
        
        complaintService.updateStatus(complaintId, "IN_PROGRESS", "Worker has arrived at location and started cleaning.");
        return "redirect:/worker/dashboard?started=true";
    }

    @PostMapping("/complete")
    public String completeTask(@RequestParam("complaintId") Long complaintId,
                               @RequestParam("afterImage") MultipartFile afterImage,
                               @RequestParam("remarks") String remarks,
                               Principal principal) {
        Complaint complaint = complaintService.getComplaintById(complaintId);
        if (complaint.getWorker() == null || !complaint.getWorker().getEmail().equals(principal.getName())) {
            return "redirect:/worker/dashboard";
        }

        if (afterImage.isEmpty()) {
            return "redirect:/worker/complaints/" + complaintId + "?error=image_required";
        }

        try {
            complaintService.completeComplaint(complaintId, afterImage, remarks);
            return "redirect:/worker/dashboard?completed=true";
        } catch (Exception e) {
            return "redirect:/worker/complaints/" + complaintId + "?error=" + e.getMessage();
        }
    }
}
