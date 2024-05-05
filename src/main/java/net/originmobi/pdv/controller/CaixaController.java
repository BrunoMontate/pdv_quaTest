package net.originmobi.pdv.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import net.originmobi.pdv.enumerado.caixa.CaixaTipo;
import net.originmobi.pdv.enumerado.caixa.EstiloLancamento;
import net.originmobi.pdv.enumerado.caixa.TipoLancamento;
import net.originmobi.pdv.filter.CaixaFilter;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.service.CaixaLancamentoService;
import net.originmobi.pdv.service.CaixaService;
import net.originmobi.pdv.service.UsuarioService;
import net.originmobi.pdv.singleton.Aplicacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/caixa")
public class CaixaController {

	private static final String CAIXA_GERENCIAR = "caixa/gerenciar";

	private static final String CAIXA_LIST = "caixa/list";

	private static final String CAIXA_FORM = "caixa/form";

	private static final String CAIXA_PARAM = "caixa";

	private static final Logger logger = LoggerFactory.getLogger(CaixaController.class);

	private final CaixaService caixas;
	private final CaixaLancamentoService lancamentos;
	private final UsuarioService usuarios;


	public CaixaController(CaixaService caixas, CaixaLancamentoService lancamentos, UsuarioService usuarios) {
        this.caixas = caixas;
        this.lancamentos = lancamentos;
        this.usuarios = usuarios;
    }

	@GetMapping("/form")
	public ModelAndView form() {
		ModelAndView mv = new ModelAndView(CAIXA_FORM);
		mv.addObject(new Caixa());
		return mv;
	}

	@GetMapping
	public ModelAndView lista(@ModelAttribute("filterCaixa") CaixaFilter filter) {
		ModelAndView mv = new ModelAndView(CAIXA_LIST);
		mv.addObject("caixas", caixas.listarCaixas(filter));
		return mv;
	}

	@PostMapping
	public String cadastro(@RequestParam Map<String, String> request, UriComponentsBuilder b) {
		String descricao = request.get("descricao");
		String tipo = request.get("tipo");
		String vlAbertura = request.get("valorAbertura");
		String agencia = request.get("agencia");
		String conta = request.get("conta");
		
		Double valorAbertura = vlAbertura.isEmpty() ? 0.0 : Double.valueOf(vlAbertura.replace(".","").replace(",", "."));
		CaixaTipo caixaTipo = CaixaTipo.valueOf(tipo);
		
		Caixa caixa = new Caixa();
		caixa.setDescricao(descricao);
		caixa.setTipo(caixaTipo);
		caixa.setValor_abertura(valorAbertura);
		caixa.setAgencia(agencia);
		caixa.setConta(conta);
		
		UriComponents uri = b.path("/caixa/gerenciar/").build();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(uri.toUri());
		
		Long codCaixa = caixas.cadastro(caixa);

		return headers.toString() + codCaixa;
	}

	@SuppressWarnings("deprecation")
	@GetMapping("/gerenciar/{codigo}")
	public ModelAndView gerenciar(@PathVariable("codigo") Caixa caixa) {
		ModelAndView mv = new ModelAndView(CAIXA_GERENCIAR);
		mv.addObject(CAIXA_PARAM, caixa);
		mv.addObject("lancamento", new CaixaLancamento());
		mv.addObject("lancamentos", lancamentos.lancamentosDoCaixa(caixa));
		return mv;
	}

	@PostMapping("/lancamento/suprimento")
	public String fazSuprimento(@RequestParam Map<String, String> request) {
		Double valor = Double.valueOf(request.get("valor").replace(",", "."));
		String observacao = request.get("obs");

		Optional<Caixa> caixaOptional = caixas.busca(Long.decode(request.get(CAIXA_PARAM)));
		if (caixaOptional.isPresent()) {
			Caixa caixa = caixaOptional.get();
			Aplicacao aplicacao = Aplicacao.getInstancia();
			Usuario usuario = usuarios.buscaUsuario(aplicacao.getUsuarioAtual());

			CaixaLancamento lancamento = new CaixaLancamento(observacao, valor, TipoLancamento.SUPRIMENTO,
					EstiloLancamento.ENTRADA, caixa, usuario);

			try {
				return lancamentos.lancamento(lancamento);
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
		} else {
			return "";
		}
	}

	@PostMapping("/lancamento/sangria")
	public String fazSangria(@RequestParam Map<String, String> request) {
		Double valor = Double.valueOf(request.get("valor").replace(",", "."));
		String observacao = request.get("obs");
		Optional<Caixa> caixaOptional = caixas.busca(Long.decode(request.get(CAIXA_PARAM)));

		if (caixaOptional.isPresent()) {
			Caixa caixa = caixaOptional.get();
			Aplicacao aplicacao = Aplicacao.getInstancia();
			Usuario usuario = usuarios.buscaUsuario(aplicacao.getUsuarioAtual());
	
			CaixaLancamento lancamento = new CaixaLancamento(observacao, valor, TipoLancamento.SANGRIA,
					EstiloLancamento.SAIDA, caixa, usuario);
	
			try {
				return lancamentos.lancamento(lancamento);
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
		} else {
			return "";
		}
	}

	@PostMapping("/fechar")
	public String fecha(@RequestParam Map<String, String> request) {
		Long caixa = Long.decode(request.get(CAIXA_PARAM));
		String senha = request.get("senha");
		
		String mensagem = "";
		try {
			mensagem = caixas.fechaCaixa(caixa, senha);
		} catch (Exception e) {
			logger.error("",e);
		}
		
		return mensagem;
	}
	
	@ModelAttribute("usuarioAtual")
	public String usuarioAtual() {
		Aplicacao aplicacao = Aplicacao.getInstancia();
		return aplicacao.getUsuarioAtual();
	}

	@ModelAttribute("caixatipo")
	public List<CaixaTipo> caixatipo() {
		return Arrays.asList(CaixaTipo.values());
	}

	@ModelAttribute("destinos")
	public List<Caixa> destinos() {
		return caixas.caixasAbertos();
	}
}
