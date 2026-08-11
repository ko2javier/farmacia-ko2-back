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
    private ActivityLogRepository activityLogRepository;

    // Registra una acción en el historial de actividad con fecha, hora e IP del cliente
    public void log(String username, String action, String detail, String ip) {
        ActivityLog registro = new ActivityLog();
        registro.setUsername(username);
        registro.setAction(action);
        registro.setDetail(detail);
        registro.setIpAddress(ip);
        registro.setFecha(LocalDate.now());
        registro.setHora(LocalTime.now());
        activityLogRepository.save(registro);
    }

    // Devuelve todos los registros ordenados del más reciente al más antiguo
    public List<ActivityLog> getAll() {
        return activityLogRepository.findAllByOrderByFechaDescHoraDesc();
    }
}
