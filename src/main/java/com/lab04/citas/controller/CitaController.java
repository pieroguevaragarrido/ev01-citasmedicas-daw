package com.lab04.citas.controller;

import com.lab04.citas.dto.CitaFormDTO;
import com.lab04.citas.exception.DisponibilidadException;
import com.lab04.citas.repository.MedicoRepository;
import com.lab04.citas.repository.PacienteRepository;
import com.lab04.citas.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("citas", citaService.listarTodas());
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
}