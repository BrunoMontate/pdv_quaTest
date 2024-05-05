package net.originmobi.pdv.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.util.Date;

import org.junit.jupiter.api.Test;

import net.originmobi.pdv.enumerado.caixa.CaixaTipo;

class CaixaTest {

    @Test
    void testIsCofre() {
        Caixa caixaCofre = new Caixa();
        caixaCofre.setTipo(CaixaTipo.COFRE);
        
        assertEquals(true, caixaCofre.isCofre());
    }

    @Test
    void testIsBanco() {
        Caixa caixaBanco = new Caixa();
        caixaBanco.setTipo(CaixaTipo.BANCO);
        
        assertEquals(true, caixaBanco.isBanco());
    }

    @Test
    void testIsAberto() {
        Caixa caixaAberto = new Caixa();
        caixaAberto.setData_fechamento(null);
        
        assertEquals(true, caixaAberto.isAberto());
        
        Caixa caixaFechado = new Caixa();
        caixaFechado.setData_fechamento(new Timestamp(new Date().getTime()));
        
        assertEquals(false, caixaFechado.isAberto());
    }
}
