package com.smartwaste.service;

import com.smartwaste.dto.ComplaintDto;
import com.smartwaste.entity.Complaint;
import com.smartwaste.entity.User;
import com.smartwaste.repository.ComplaintRepository;
import com.smartwaste.repository.UserRepository;
import com.smartwaste.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    public ComplaintServiceImpl(ComplaintRepository complaintRepository, UserRepository userRepository) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Complaint raiseComplaint(ComplaintDto complaintDto, String citizenEmail) {
        User citizen = userRepository.findByEmail(citizenEmail)
                .orElseThrow(() -> new IllegalArgumentException("Citizen not found with email: " + citizenEmail));

        String generatedId = "COMP-" + (100000 + new Random().nextInt(900000));

        String savedFilename = null;
        if (complaintDto.getImageFile() != null && !complaintDto.getImageFile().isEmpty()) {
            try {
                savedFilename = FileUploadUtil.saveFile(uploadPath, complaintDto.getImageFile());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload garbage image", e);
            }
        }

        Complaint complaint = Complaint.builder()
                .complaintId(generatedId)
                .title(complaintDto.getTitle())
                .category(complaintDto.getCategory())
                .description(complaintDto.getDescription())
                .area(complaintDto.getArea())
                .landmark(complaintDto.getLandmark())
                .phone(complaintDto.getPhone())
                .latitude(complaintDto.getLatitude())
                .longitude(complaintDto.getLongitude())
                .imagePath(savedFilename)
                .status("PENDING")
                .citizen(citizen)
                .build();

        return complaintRepository.save(complaint);
    }

    @Override
    public List<Complaint> getCitizenComplaints(String citizenEmail) {
        User citizen = userRepository.findByEmail(citizenEmail)
                .orElseThrow(() -> new IllegalArgumentException("Citizen not found"));
        return complaintRepository.findByCitizenIdOrderByDateDesc(citizen.getId());
    }

    @Override
    public List<Complaint> getWorkerComplaints(String workerEmail) {
        User worker = userRepository.findByEmail(workerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Worker not found"));
        return complaintRepository.findByWorkerIdOrderByDateDesc(worker.getId());
    }

    @Override
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAllByOrderByDateDesc();
    }

    @Override
    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found with ID: " + id));
    }

    @Override
    public Complaint assignWorker(Long complaintId, Long workerId, String priority) {
        Complaint complaint = getComplaintById(complaintId);
        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("Worker not found with ID: " + workerId));

        if (!"ROLE_WORKER".equals(worker.getRole())) {
            throw new IllegalArgumentException("Assigned user is not a worker");
        }

        complaint.setWorker(worker);
        complaint.setPriority(priority);
        complaint.setStatus("ASSIGNED");
        return complaintRepository.save(complaint);
    }

    @Override
    public Complaint updateStatus(Long complaintId, String status, String remarks) {
        Complaint complaint = getComplaintById(complaintId);
        complaint.setStatus(status);
        if (remarks != null && !remarks.trim().isEmpty()) {
            complaint.setRemarks(remarks);
        }
        return complaintRepository.save(complaint);
    }

    @Override
    public Complaint completeComplaint(Long complaintId, MultipartFile afterImage, String remarks) {
        Complaint complaint = getComplaintById(complaintId);

        String savedFilename = null;
        if (afterImage != null && !afterImage.isEmpty()) {
            try {
                savedFilename = FileUploadUtil.saveFile(uploadPath, afterImage);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload after-cleaning image", e);
            }
        }

        complaint.setAfterCleaningImage(savedFilename);
        complaint.setStatus("COMPLETED");
        if (remarks != null && !remarks.trim().isEmpty()) {
            complaint.setRemarks(remarks);
        }
        return complaintRepository.save(complaint);
    }

    @Override
    public long getCountByStatus(String status) {
        return complaintRepository.countByStatus(status);
    }

    @Override
    public long getCountByCitizen(String email) {
        User citizen = userRepository.findByEmail(email).orElse(null);
        if (citizen == null) return 0;
        return complaintRepository.countByCitizenId(citizen.getId());
    }

    @Override
    public List<Complaint> searchAndFilter(String query, String status) {
        if (query == null || query.trim().isEmpty()) {
            if (status == null || status.trim().isEmpty() || "ALL".equalsIgnoreCase(status)) {
                return complaintRepository.findAllByOrderByDateDesc();
            } else {
                return complaintRepository.findByStatus(status.toUpperCase());
            }
        } else {
            String trimmedQuery = query.trim();
            if (status == null || status.trim().isEmpty() || "ALL".equalsIgnoreCase(status)) {
                return complaintRepository.searchComplaints(trimmedQuery);
            } else {
                return complaintRepository.searchComplaintsWithStatus(trimmedQuery, status.toUpperCase());
            }
        }
    }

    @Override
    public void deleteComplaint(Long id) {
        if (complaintRepository.existsById(id)) {
            complaintRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Complaint not found with ID: " + id);
        }
    }
}
