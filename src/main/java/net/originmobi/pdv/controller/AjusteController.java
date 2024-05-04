package net.originmobi.pdv.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.ui.Model;
import net.originmobi.pdv.filter.AjusteFilter;
import net.originmobi.pdv.model.Ajuste;
import net.originmobi.pdv.model.Produto;
import net.originmobi.pdv.service.AjusteProdutoService;
import net.originmobi.pdv.service.AjusteService;
import net.originmobi.pdv.service.ProdutoService;

@RestController
@RequestMapping("/ajustes")
public class AjusteController {

    private static final String AJUSTE_FORM = "ajuste/form";
    private static final String AJUSTE_LIST = "ajuste/list";
    private static final String PARAM_CODAJUSTE = "codajuste";
    private static final String PARAM_CODPROD = "codprod";
    private static final String PARAM_QTD_ALTERAR = "qtd_alterar";

    private final AjusteService ajustes;
    private final ProdutoService produtos;
    private final AjusteProdutoService ajusteProdutos;

    @Autowired
    public AjusteController(AjusteService ajustes, ProdutoService produtos, AjusteProdutoService ajusteProdutos) {
        this.ajustes = ajustes;
        this.produtos = produtos;
        this.ajusteProdutos = ajusteProdutos;
    }

    @GetMapping
    public ModelAndView lista(@ModelAttribute("filterAjuste") AjusteFilter filter, Pageable pageable, Model model) {
        ModelAndView mv = new ModelAndView(AJUSTE_LIST);
        Page<Ajuste> lista = ajustes.lista(pageable, filter);
        mv.addObject("ajustes", lista);
        
        model.addAttribute("qtdpaginas", lista.getTotalPages());
        model.addAttribute("pagAtual", lista.getPageable().getPageNumber());
        model.addAttribute("proxPagina", lista.getPageable().next().getPageNumber());
        model.addAttribute("pagAnterior", lista.getPageable().previousOrFirst().getPageNumber());
        model.addAttribute("hasNext", lista.hasNext());
        model.addAttribute("hasPrevious", lista.hasPrevious());
        
        return mv;
    }

    @PostMapping
    public String cadastra(UriComponentsBuilder builder) {
        UriComponents uri = builder.path("/ajustes/").build();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uri.toUri());

        Long codigo = ajustes.novo();

        return headers + codigo.toString();
    }

    @GetMapping("{codigo}")
    public ModelAndView form(@PathVariable("codigo") Ajuste ajuste) {
        ModelAndView mv = new ModelAndView(AJUSTE_FORM);
        mv.addObject("ajuste", ajuste);
        mv.addObject("produtosAjuste", ajusteProdutos.listaProdutosAjuste(ajuste.getCodigo()));
        return mv;
    }
    
    @PostMapping("/addproduto")
    public String addProduto(@RequestParam Map<String, String> request) {
        Long codAjuste = Long.decode(request.get(PARAM_CODAJUSTE));
        Long codprod = Long.decode(request.get(PARAM_CODPROD));
        int qtdAlterar = Integer.parseInt(request.get(PARAM_QTD_ALTERAR));
        
        return ajusteProdutos.addProduto(codAjuste, codprod, qtdAlterar);
    }
    
    @PostMapping("/processar")
    public String processar(@RequestParam Map<String, String> request) {
        Long codAjuste = Long.decode(request.get(PARAM_CODAJUSTE));
        String obs = request.get("obs");
        
        return ajustes.processar(codAjuste, obs);
    }
    
    @DeleteMapping("/cancelar/{codigo}")
    public String remover(@PathVariable("codigo") Ajuste ajuste, UriComponentsBuilder builder) {
        UriComponents component = builder.path("/ajustes").build();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(component.toUri());
        
        ajustes.remover(ajuste);
        
        return component.toString();
    }
    
    @DeleteMapping("/remove/item")
    public String removeItem(@RequestParam Map<String, String> request) {
        Long codAjuste = Long.decode(request.get(PARAM_CODAJUSTE));
        Long coditem = Long.decode(request.get("coditem"));
        
        return ajusteProdutos.removeProduto(codAjuste, coditem);
    }

    @ModelAttribute("produtos")
    public List<Produto> produtos() {
        return produtos.listar();
    }
}
