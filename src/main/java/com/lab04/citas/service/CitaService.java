package com.lab04.citas.service;

import com.lab04.citas.dto.CitaFormDTO;
import com.lab04.citas.entity.Cita;
import com.lab04.citas.entity.CitaEstado;
public interface CitaService {
    Cita registrarCita(CitaFormDTO dto); // RF-CIT-01 + RF-CIT-02 + RF-CIT-04 + RF-CIT-05
    Cita buscarPorId(Long id);
    Iterable<Cita> listarTodas();

    Cita modificarCita(Long id, CitaFormDTO dto);                      // RF-CIT-09
    void cancelarCita(Long id, String motivo, String usuario);         // RF-CIT-11
    void cambiarEstado(Long id, CitaEstado nuevoEstado);                // RF-CIT-13
}