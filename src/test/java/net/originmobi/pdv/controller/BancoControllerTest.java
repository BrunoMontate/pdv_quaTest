package net.originmobi.pdv.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;
import net.originmobi.pdv.enumerado.caixa.CaixaTipo;
import net.originmobi.pdv.filter.BancoFilter;
import net.originmobi.pdv.service.CaixaService;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Adiciona a extensão Mockito para JUnit 5
class BancoControllerTest {

    @Mock
    private CaixaService caixaService;

    @InjectMocks
    private BancoController bancoController;

    @Test
    void testListar() {
        // Mock data
        BancoFilter filter = new BancoFilter();
        when(caixaService.listaBancosAbertosTipoFilterBanco(CaixaTipo.BANCO, filter))
            .thenReturn(Collections.emptyList());

        // Call the method
        ModelAndView modelAndView = bancoController.listar(filter);

        // Verify interactions and assertions
        verify(caixaService, times(1)).listaBancosAbertosTipoFilterBanco(CaixaTipo.BANCO, filter);
        assertEquals("banco/list", modelAndView.getViewName());
        assertNotNull(modelAndView.getModel().get("bancos"));
    }
}
