package br.edu.eseg.cooperativas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AutenticacaoController {

    @GetMapping("/login")
    public String exibirLogin() {
        return "login";
    }

    @GetMapping("/acesso-negado")
    public String exibirAcessoNegado() {
        return "acesso-negado";
    }
}