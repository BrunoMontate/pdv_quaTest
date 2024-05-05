package net.originmobi.pdv.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.originmobi.pdv.enumerado.TelefoneTipo;
import net.originmobi.pdv.filter.FornecedorFilter;
import net.originmobi.pdv.model.Cidade;
import net.originmobi.pdv.model.Endereco;
import net.originmobi.pdv.model.Fornecedor;
import net.originmobi.pdv.model.Telefone;
import net.originmobi.pdv.service.CidadeService;
import net.originmobi.pdv.service.EnderecoService;
import net.originmobi.pdv.service.FornecedorService;
import net.originmobi.pdv.service.TelefoneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
@RequestMapping("/fornecedor")
public class FornecedorController {

	private static final String FORNECEDOR_FORM = "fornecedor/form";
	private static final String FORNECEDOR_LIST = "fornecedor/list";
	private static final Logger logger = LoggerFactory.getLogger(FornecedorController.class);
	private static final String REDIRECT_FORNECEDOR_FORM = "redirect:/fornecedor/form";


	private final FornecedorService fornecedores;
	private final CidadeService cidades;
	private final EnderecoService enderecos;
	private final TelefoneService telefones;

	@Autowired
    public FornecedorController(FornecedorService fornecedores, CidadeService cidades, EnderecoService enderecos, TelefoneService telefones) {
        this.fornecedores = fornecedores;
        this.cidades = cidades;
        this.enderecos = enderecos;
        this.telefones = telefones;
    }

	@GetMapping("/form")
	public ModelAndView form() {
		ModelAndView mv = new ModelAndView(FORNECEDOR_FORM);
		mv.addObject("fornecedor", new Fornecedor());
		mv.addObject("endereco", new Endereco());
		mv.addObject("telefone", new Telefone());
		return mv;
	}

	@GetMapping
	public ModelAndView busca(@ModelAttribute FornecedorFilter filter) {
		ModelAndView mv = new ModelAndView(FORNECEDOR_LIST);
		List<Fornecedor> todosFornecedores = fornecedores.busca(filter);
		mv.addObject("todosFornecedores", todosFornecedores);
		return mv;
	}

	@GetMapping("{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Fornecedor fornecedor) {
		ModelAndView mv = new ModelAndView(FORNECEDOR_FORM);
		mv.addObject(fornecedor);
		
		Endereco endereco = enderecos.enderecoCodigo(fornecedor.getEndereco().getCodigo());
		mv.addObject(endereco);
		
		Telefone telefone = telefones.telefoneCodigo(fornecedor.getTelefone().get(0).getCodigo());
		mv.addObject(telefone);
		return mv;
	}

	@PostMapping
	@Transactional
	public String codastrar(@Validated Fornecedor fornecedor, Errors errors, Endereco endereco, Telefone telefone,
			RedirectAttributes attributes) {

		if (errors.hasErrors())
			return FORNECEDOR_FORM;

		try {
			enderecos.cadastrar(endereco);
		} catch (Exception e) {
			logger.error("Erro ao cadastrar endereço ",e);
			return REDIRECT_FORNECEDOR_FORM;
		}

		try {
			telefones.cadastrar(telefone);
		} catch (Exception e) {
			logger.error("Erro ao cadastrar telefone ",e);
			return REDIRECT_FORNECEDOR_FORM;
		}

		fornecedor.setEndereco(endereco);
		fornecedor.setTelefone(Arrays.asList(telefone));

		try {
			String mensagem = fornecedores.cadastrar(fornecedor);
			attributes.addFlashAttribute("mensagem", mensagem);

			return REDIRECT_FORNECEDOR_FORM;
		} catch (Exception e) {
			logger.error("Erro ao cadastrar fornecedor ",e);
		}

		return REDIRECT_FORNECEDOR_FORM;
	}

	@ModelAttribute("cidades")
	public List<Cidade> cidades() {
		return cidades.lista();
	}

	@ModelAttribute("telefoneTipo")
	public List<TelefoneTipo> telefoneTipo() {
		return Arrays.asList(TelefoneTipo.values());
	}

}
