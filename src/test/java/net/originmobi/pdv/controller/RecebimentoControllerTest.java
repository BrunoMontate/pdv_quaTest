package net.originmobi.pdv.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.originmobi.pdv.service.RecebimentoService;
import net.originmobi.pdv.service.TituloService;

class RecebimentoControllerTest {

    @Mock
    private RecebimentoService recebimentoService;

    @Mock
    private TituloService tituloService;

    @InjectMocks
    private RecebimentoController recebimentoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //Testar o metodo Receber. O pagamento deve ser recebido com sucesso.
    @Test
    void testReceber() {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("receber", "1");
        requestParams.put("titulo", "1");
        requestParams.put("vlrecebido", "100.00");
        requestParams.put("desconto", "0.00");
        requestParams.put("acrescimo", "0.00");

        when(recebimentoService.receber(anyLong(), anyDouble(), anyDouble(), anyDouble(), anyLong()))
                .thenReturn("Pagamento recebido com sucesso");

        String mensagem = recebimentoController.receber(requestParams);
        assertNotNull(mensagem);
        assertEquals("Pagamento recebido com sucesso", mensagem);
    }
}
