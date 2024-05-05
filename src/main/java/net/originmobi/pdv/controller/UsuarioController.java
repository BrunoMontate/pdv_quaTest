package net.originmobi.pdv.controller;

import java.util.List;
import java.util.Map;

import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.originmobi.pdv.model.GrupoUsuario;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.service.GrupoUsuarioService;
import net.originmobi.pdv.service.PessoaService;
import net.originmobi.pdv.service.UsuarioService;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

	private static final String USUARIO_LIST = "usuario/list";

	private static final String USUARIO_FORM = "usuario/form";
	private static final String CODIGO_GRUPO = "codigoGru";

	private UsuarioService usuarios;
	private PessoaService pessoas;
	private GrupoUsuarioService gruposUsuario;

	public UsuarioController(UsuarioService usuarios,PessoaService pessoas, GrupoUsuarioService gruposUsuario){
		this.usuarios = usuarios;
		this.pessoas = pessoas;
		this.gruposUsuario = gruposUsuario;
	}

	@GetMapping("/form")
	public ModelAndView form() {
		ModelAndView mv = new ModelAndView(USUARIO_FORM);
		mv.addObject("usuario", new Usuario());
		return mv;
	}

	@GetMapping
	public ModelAndView lista() {
		ModelAndView mv = new ModelAndView(USUARIO_LIST);
		mv.addObject("usuarios", usuarios.lista());
		return mv;
	}

	@PostMapping
	public String cadastrar(@Validated Usuario usuario, Errors errors, RedirectAttributes attributes) {
		if (errors.hasErrors())
			return USUARIO_FORM;

		String mensagem = "";

		try {
			mensagem = usuarios.cadastrar(usuario);
			attributes.addFlashAttribute("mensagem", mensagem);
		} catch (Exception e) {
			e.getStackTrace();
		}

		return "redirect:/usuario/form";
	}

	@GetMapping("{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Usuario usuario) {
		ModelAndView mv = new ModelAndView(USUARIO_FORM);
		mv.addObject(usuario);
		mv.addObject("grupos", gruposUsuario.buscaGrupos(usuario));
		return mv;
	}

	@PostMapping("/addgrupo")
	public String addGrupo(@RequestParam Map<String, String> request) {

		if (request.get(CODIGO_GRUPO).length() == 0)
			return "grupo vazio";

		Long codUsu = Long.decode(request.get("codigoUsu"));
		Long codGru = Long.decode(request.get(CODIGO_GRUPO));

		return usuarios.addGrupo(codUsu, codGru);
	}

	@PutMapping("/removegrupo")
	public String removeGrupo(@RequestParam Map<String, String> request) {
		Long codUsu = Long.decode(request.get("codigoUsu"));
		Long codGru = Long.decode(request.get(CODIGO_GRUPO));

		return usuarios.removeGrupo(codUsu, codGru);
	}

	@GetMapping("/teste")
	public String teste() {
		return "tudo ok";
	}

	@ModelAttribute("pessoas")
	public List<Pessoa> pessoas() {
		return pessoas.lista();
	}

	@ModelAttribute("todosGrupos")
	public List<GrupoUsuario> todosGrupos() {
		return gruposUsuario.lista();
	}

}
