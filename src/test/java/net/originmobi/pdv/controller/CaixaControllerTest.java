package net.originmobi.pdv.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import net.originmobi.pdv.service.CaixaService;
import net.originmobi.pdv.service.CaixaLancamentoService;
import net.originmobi.pdv.service.UsuarioService;
import net.originmobi.pdv.filter.CaixaFilter;
import net.originmobi.pdv.model.Caixa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;
import java.util.Arrays;
import java.util.List;


class CaixaControllerTest {

    private CaixaService caixaService;
    private CaixaLancamentoService caixaLancamentoService;
    private UsuarioService usuarioService;
    private CaixaController caixaController;

    @BeforeEach
    void setUp() {
        caixaService = mock(CaixaService.class);
        caixaLancamentoService = mock(CaixaLancamentoService.class);
        usuarioService = mock(UsuarioService.class);
        caixaController = new CaixaController(caixaService, caixaLancamentoService, usuarioService);
    }
    // Verifica existe um objeto caixa no modelo
    @Test
    void testForm() {
        ModelAndView modelAndView = caixaController.form();

        assertEquals("caixa/form", modelAndView.getViewName());
        assertNotNull(modelAndView.getModel().get("caixa"));
    }

    @Test
    void testLista() {
        CaixaFilter filter = new CaixaFilter();
        List<Caixa> caixas = Arrays.asList(new Caixa(), new Caixa());
        when(caixaService.listarCaixas(filter)).thenReturn(caixas);
        ModelAndView modelAndView = caixaController.lista(filter);
        assertEquals("caixa/list", modelAndView.getViewName());
        assertEquals(caixas, modelAndView.getModel().get("caixas"));
    }
    // Deve retornar a view correta e se contém a lista de caixas no modelo.
    
}
