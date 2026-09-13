package com.lab04.citas.repository;

import com.lab04.citas.entity.Cita;
import com.lab04.citas.entity.CitaEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    // Usado por RF-CIT-04 y RF-CIT-05: busca citas del médico que se solapan
    // en fecha/hora y que NO estén en los estados excluidos (cancelada, no asistió).
    // RF-CIT-04 / RF-CIT-05: conflictos de horario del MÉDICO.
    // No filtra por especialidad a propósito: un médico no puede estar en
    // dos citas al mismo tiempo sin importar qué especialidad se escriba
    // en el formulario para cada cita.
    @Query("SELECT c FROM Cita c WHERE c.medico.id = :medicoId " +
            "AND c.fecha = :fecha " +
            "AND c.estado NOT IN :estadosExcluidos " +
            "AND (:horaInicio < c.horaFin AND :horaFin > c.horaInicio)")
    List<Cita> buscarConflictos(@Param("medicoId") Long medicoId,
                                @Param("fecha") LocalDate fecha,
                                @Param("horaInicio") LocalTime horaInicio,
                                @Param("horaFin") LocalTime horaFin,
                                @Param("estadosExcluidos") List<CitaEstado> estadosExcluidos);

    // Nuevo: conflictos de horario del PACIENTE. Mismo patrón que buscarConflictos,
    // pero filtrando por paciente.id en vez de medico.id, y sin importar el médico
    // ni la especialidad: un paciente no puede tener 2 citas que se crucen en horario.
    @Query("SELECT c FROM Cita c WHERE c.paciente.id = :pacienteId " +
            "AND c.fecha = :fecha " +
            "AND c.estado NOT IN :estadosExcluidos " +
            "AND (:horaInicio < c.horaFin AND :horaFin > c.horaInicio)")
    List<Cita> buscarConflictosPaciente(@Param("pacienteId") Long pacienteId,
                                        @Param("fecha") LocalDate fecha,
                                        @Param("horaInicio") LocalTime horaInicio,
                                        @Param("horaFin") LocalTime horaFin,
                                        @Param("estadosExcluidos") List<CitaEstado> estadosExcluidos);
}