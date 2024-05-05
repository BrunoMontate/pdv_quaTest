package net.originmobi.pdv.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ParcelaTest {

    @Test
    void testQuitado() {
        Parcela parcela = new Parcela();
        parcela.setQuitado(1);
        assertEquals(true, parcela.isQuitado());
        parcela.setQuitado(0);
        assertEquals(false, parcela.isQuitado());
    }
}
