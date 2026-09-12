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
    @Query("SELECT c FROM Cita c WHERE c.medico.id = :medicoId " +
            "AND c.fecha = :fecha " +
            "AND c.estado NOT IN :estadosExcluidos " +
            "AND (:horaInicio < c.horaFin AND :horaFin > c.horaInicio)")
    List<Cita> buscarConflictos(@Param("medicoId") Long medicoId,
                                @Param("fecha") LocalDate fecha,
                                @Param("horaInicio") LocalTime horaInicio,
                                @Param("horaFin") LocalTime horaFin,
                                @Param("estadosExcluidos") List<CitaEstado> estadosExcluidos);
}