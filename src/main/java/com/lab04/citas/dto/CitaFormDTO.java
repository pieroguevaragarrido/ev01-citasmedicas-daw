package com.lab04.citas.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class CitaFormDTO {

    @NotNull(message = "Debe seleccionar un paciente")
    private Long pacienteId;

    @NotNull(message = "Debe seleccionar un médico")
    private Long medicoId;

    @NotBlank(message = "La especialidad es obligatoria")
    private String especialidad;

    private String consultorio;

    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "La fecha no puede ser en el pasado")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio es obligatoria")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaInicio;

    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public String getConsultorio() { return consultorio; }
    public void setConsultorio(String consultorio) { this.consultorio = consultorio; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
}