package com.lab04.citas.service;

import com.lab04.citas.dto.CitaFormDTO;
import com.lab04.citas.entity.Cita;

public interface CitaService {
    Cita registrarCita(CitaFormDTO dto); // RF-CIT-01 + RF-CIT-02 + RF-CIT-04 + RF-CIT-05
    Cita buscarPorId(Long id);
    Iterable<Cita> listarTodas();
}