package com.FP_Final.FP.service;

import com.FP_Final.FP.model.ActivityLog;
import com.FP_Final.FP.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ActivityLogService {

    @Autowired
    private ActivityLogRepository repo;

    public void log(String username, String action, String detail, String ip) {
        ActivityLog entry = new ActivityLog();
        entry.setUsername(username);
        entry.setAction(action);
        entry.setDetail(detail);
        entry.setIpAddress(ip);
        entry.setFecha(LocalDate.now());
        entry.setHora(LocalTime.now());
        repo.save(entry);
    }

    public List<ActivityLog> getAll() {
        return repo.findAllByOrderByFechaDescHoraDesc();
    }
}
