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

    // RF-CIT-04 / RF-CIT-05: conflictos de horario del MÉDICO.
    // No filtra por especialidad a propósito.
    @Query("SELECT c FROM Cita c WHERE c.medico.id = :medicoId " +
            "AND c.fecha = :fecha " +
            "AND c.estado NOT IN :estadosExcluidos " +
            "AND (:horaInicio < c.horaFin AND :horaFin > c.horaInicio)")
    List<Cita> buscarConflictos(@Param("medicoId") Long medicoId,
                                @Param("fecha") LocalDate fecha,
                                @Param("horaInicio") LocalTime horaInicio,
                                @Param("horaFin") LocalTime horaFin,
                                @Param("estadosExcluidos") List<CitaEstado> estadosExcluidos);

    // Conflictos de horario del PACIENTE: mismo patrón, filtrando por paciente.id.
    @Query("SELECT c FROM Cita c WHERE c.paciente.id = :pacienteId " +
            "AND c.fecha = :fecha " +
            "AND c.estado NOT IN :estadosExcluidos " +
            "AND (:horaInicio < c.horaFin AND :horaFin > c.horaInicio)")
    List<Cita> buscarConflictosPaciente(@Param("pacienteId") Long pacienteId,
                                        @Param("fecha") LocalDate fecha,
                                        @Param("horaInicio") LocalTime horaInicio,
                                        @Param("horaFin") LocalTime horaFin,
                                        @Param("estadosExcluidos") List<CitaEstado> estadosExcluidos);

    // RF-CIT-17: búsqueda con filtros combinables, todos opcionales.
    @Query("SELECT c FROM Cita c WHERE " +
            "(:pacienteId IS NULL OR c.paciente.id = :pacienteId) AND " +
            "(:medicoId IS NULL OR c.medico.id = :medicoId) AND " +
            "(:especialidad IS NULL OR c.especialidad = :especialidad) AND " +
            "(:fecha IS NULL OR c.fecha = :fecha) AND " +
            "(:estado IS NULL OR c.estado = :estado) " +
            "ORDER BY c.fecha DESC, c.horaInicio DESC")
    List<Cita> buscarConFiltros(@Param("pacienteId") Long pacienteId,
                                @Param("medicoId") Long medicoId,
                                @Param("especialidad") String especialidad,
                                @Param("fecha") LocalDate fecha,
                                @Param("estado") CitaEstado estado);
}