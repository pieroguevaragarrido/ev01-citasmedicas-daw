package com.lab04.citas.service;

import com.lab04.citas.dto.CitaFormDTO;
import com.lab04.citas.entity.Cita;
import com.lab04.citas.entity.CitaEstado;

import java.time.LocalDate;
import java.util.List;

public interface CitaService {

    Cita registrarCita(CitaFormDTO dto); // RF-CIT-01 + RF-CIT-02 + RF-CIT-04 + RF-CIT-05
    Cita buscarPorId(Long id);
    Iterable<Cita> listarTodas();

    void cambiarEstado(Long id, CitaEstado nuevoEstado);            // RF-CIT-13
    void cancelarCita(Long id, String motivo, String usuario);      // RF-CIT-11
    Cita modificarCita(Long id, CitaFormDTO dto);                   // RF-CIT-09
    List<Cita> buscarConFiltros(Long pacienteId, Long medicoId,
                                String especialidad, LocalDate fecha,
                                CitaEstado estado);                 // RF-CIT-17
}