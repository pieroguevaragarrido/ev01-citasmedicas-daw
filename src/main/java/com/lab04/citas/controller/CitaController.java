package com.lab04.citas.controller;

import com.lab04.citas.dto.CitaFormDTO;
import com.lab04.citas.entity.Cita;
import com.lab04.citas.entity.CitaEstado;
import com.lab04.citas.exception.DisponibilidadException;
import com.lab04.citas.exception.TransicionEstadoInvalidaException;
import com.lab04.citas.repository.MedicoRepository;
import com.lab04.citas.repository.PacienteRepository;
import com.lab04.citas.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Arrays;

@Controller
@RequestMapping("/citas")
public class CitaController {

    private final CitaService citaService;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    public CitaController(CitaService citaService,
                          PacienteRepository pacienteRepository,
                          MedicoRepository medicoRepository) {
        this.citaService = citaService;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
    }

    // RF-CIT-17: listado acepta filtros opcionales combinables
    @GetMapping
    public String listar(@RequestParam(required = false) Long pacienteId,
                         @RequestParam(required = false) Long medicoId,
                         @RequestParam(required = false) String especialidad,
                         @RequestParam(required = false) LocalDate fecha,
                         @RequestParam(required = false) CitaEstado estado,
                         Model model) {

        if (especialidad != null && especialidad.isBlank()) {
            especialidad = null;
        }

        model.addAttribute("citas",
                citaService.buscarConFiltros(pacienteId, medicoId, especialidad, fecha, estado));
        model.addAttribute("pacientes", pacienteRepository.findAll());
        model.addAttribute("medicos", medicoRepository.findAll());
        model.addAttribute("estados", CitaEstado.values());
        model.addAttribute("estadosCambio", Arrays.stream(CitaEstado.values())
                .filter(e -> e != CitaEstado.CANCELADA)
                .toArray());
        model.addAttribute("fPacienteId", pacienteId);
        model.addAttribute("fMedicoId", medicoId);
        model.addAttribute("fEspecialidad", especialidad);
        model.addAttribute("fFecha", fecha);
        model.addAttribute("fEstado", estado);
        return "citas/lista";
    }

    @GetMapping("/nueva")
    public String formularioNuevo(Model model) {
        model.addAttribute("citaForm", new CitaFormDTO());
        model.addAttribute("pacientes", pacienteRepository.findAll());
        model.addAttribute("medicos", medicoRepository.findAll());
        return "citas/formulario";
    }

    @PostMapping
    public String registrar(@Valid @ModelAttribute("citaForm") CitaFormDTO citaForm,
                            BindingResult result,
                            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pacientes", pacienteRepository.findAll());
            model.addAttribute("medicos", medicoRepository.findAll());
            return "citas/formulario";
        }
        try {
            citaService.registrarCita(citaForm);
        } catch (DisponibilidadException ex) {
            model.addAttribute("errorDisponibilidad", ex.getMessage());
            model.addAttribute("pacientes", pacienteRepository.findAll());
            model.addAttribute("medicos", medicoRepository.findAll());
            return "citas/formulario";
        }
        return "redirect:/citas";
    }

    // ---------- RF-CIT-09: Modificar cita ----------
    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model) {
        Cita cita = citaService.buscarPorId(id);
        CitaFormDTO dto = new CitaFormDTO();
        dto.setPacienteId(cita.getPaciente().getId());
        dto.setMedicoId(cita.getMedico().getId());
        dto.setEspecialidad(cita.getEspecialidad());
        dto.setConsultorio(cita.getConsultorio());
        dto.setFecha(cita.getFecha());
        dto.setHoraInicio(cita.getHoraInicio());
        model.addAttribute("citaForm", dto);
        model.addAttribute("citaId", id);
        model.addAttribute("pacientes", pacienteRepository.findAll());
        model.addAttribute("medicos", medicoRepository.findAll());
        return "citas/formulario";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("citaForm") CitaFormDTO citaForm,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("citaId", id);
            model.addAttribute("pacientes", pacienteRepository.findAll());
            model.addAttribute("medicos", medicoRepository.findAll());
            return "citas/formulario";
        }
        try {
            citaService.modificarCita(id, citaForm);
        } catch (DisponibilidadException ex) {
            model.addAttribute("errorDisponibilidad", ex.getMessage());
            model.addAttribute("citaId", id);
            model.addAttribute("pacientes", pacienteRepository.findAll());
            model.addAttribute("medicos", medicoRepository.findAll());
            return "citas/formulario";
        }
        return "redirect:/citas";
    }

    // ---------- RF-CIT-13: Cambiar estado ----------
    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam CitaEstado nuevoEstado,
                                RedirectAttributes redirectAttributes) {
        try {
            citaService.cambiarEstado(id, nuevoEstado);
        } catch (TransicionEstadoInvalidaException ex) {
            redirectAttributes.addFlashAttribute("errorEstado", ex.getMessage());
        }
        return "redirect:/citas";
    }

    // ---------- RF-CIT-11: Cancelar cita ----------
    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id,
                           @RequestParam String motivo,
                           @RequestParam(required = false, defaultValue = "sistema") String usuario,
                           RedirectAttributes redirectAttributes) {
        try {
            citaService.cancelarCita(id, motivo, usuario);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorCancelacion", ex.getMessage());
        }
        return "redirect:/citas";
    }
}