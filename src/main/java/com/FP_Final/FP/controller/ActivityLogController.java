package com.FP_Final.FP.controller;

import com.FP_Final.FP.model.ActivityLog;
import com.FP_Final.FP.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/activity-log")
public class ActivityLogController {

    @Autowired
    private ActivityLogService activityLogService;

    // GET /activity-log/all — devuelve el historial completo de acciones (solo SUPERADMIN)
    @GetMapping("/all")
    public List<ActivityLog> obtenerTodos() {
        return activityLogService.getAll();
    }
}
