package com.lab04.citas.service.impl;

import com.lab04.citas.dto.CitaFormDTO;
import com.lab04.citas.entity.Cita;
import com.lab04.citas.entity.CitaEstado;
import com.lab04.citas.entity.Medico;
import com.lab04.citas.entity.Paciente;
import com.lab04.citas.exception.DisponibilidadException;
import com.lab04.citas.repository.CitaRepository;
import com.lab04.citas.repository.MedicoRepository;
import com.lab04.citas.repository.PacienteRepository;
import com.lab04.citas.service.CitaService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    // Duración fija de la cita en minutos (simplificación)
    private static final int DURACION_MINUTOS = 30;

    public CitaServiceImpl(CitaRepository citaRepository,
                           PacienteRepository pacienteRepository,
                           MedicoRepository medicoRepository) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
    }

    @Override
    public Cita registrarCita(CitaFormDTO dto) {
        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));
        Medico medico = medicoRepository.findById(dto.getMedicoId())
                .orElseThrow(() -> new IllegalArgumentException("Médico no encontrado"));

        LocalTime horaFin = dto.getHoraInicio().plusMinutes(DURACION_MINUTOS);

        // RF-CIT-04 y RF-CIT-05: validar disponibilidad / impedir doble reserva
        List<Cita> conflictos = citaRepository.buscarConflictos(
                medico.getId(), dto.getFecha(), dto.getHoraInicio(), horaFin,
                List.of(CitaEstado.CANCELADA, CitaEstado.NO_ASISTIO));
        if (!conflictos.isEmpty()) {
            throw new DisponibilidadException(
                    "El médico ya tiene una cita programada en ese horario. Elige otro horario."
            );
        }

        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setMedico(medico);
        cita.setEspecialidad(dto.getEspecialidad());
        cita.setConsultorio(dto.getConsultorio());
        cita.setFecha(dto.getFecha());
        cita.setHoraInicio(dto.getHoraInicio());
        cita.setHoraFin(horaFin);
        cita.setEstado(CitaEstado.PROGRAMADA);
        cita.setFechaCreacion(LocalDateTime.now());

        // RF-CIT-02: generar código único (correlativo simple)
        long siguiente = citaRepository.count() + 1;
        cita.setCodigoCita(String.format("CIT-%06d", siguiente));

        return citaRepository.save(cita);
    }

    @Override
    public Cita buscarPorId(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));
    }

    @Override
    public Iterable<Cita> listarTodas() {
        return citaRepository.findAll();
    }
}