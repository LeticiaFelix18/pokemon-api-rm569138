package br.com.fiap.bo;

import br.com.fiap.dao.PokemonDAO;
import br.com.fiap.to.PokemonTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PokemonBOTest {
    private PokemonBO bo;
    private PokemonDAO dao;

    @BeforeEach
    void preparar() {
        bo = new PokemonBO();
        dao = mock(PokemonDAO.class);
        ReflectionTestUtils.setField(bo, "pokemonDAO", dao);
    }

    @Test
    void bloqueiaHojeFuturoENuloAntesDoDAO() {
        PokemonTO pokemon = new PokemonTO();
        for (LocalDate data : new LocalDate[]{LocalDate.now(), LocalDate.now().plusDays(1), null}) {
            pokemon.setDataDaCaptura(data);
            assertNull(bo.save(pokemon));
            assertNull(bo.update(pokemon));
        }
        verifyNoInteractions(dao);
    }

    @Test
    void encaminhaCapturaPassadaParaPersistencia() {
        PokemonTO pokemon = new PokemonTO(1L, "Bulbasaur", 0.7, 6.9, "Semente", LocalDate.now().minusDays(1));
        when(dao.save(pokemon)).thenReturn(pokemon);
        when(dao.update(pokemon)).thenReturn(pokemon);
        assertSame(pokemon, bo.save(pokemon));
        assertSame(pokemon, bo.update(pokemon));
    }

    @Test
    void consultaEExclusaoPassamPeloDAO() {
        ArrayList<PokemonTO> lista = new ArrayList<>();
        when(dao.findAll()).thenReturn(lista);
        when(dao.delete(3L)).thenReturn(true);
        assertSame(lista, bo.findAll());
        assertNull(bo.findByCodigo(3L));
        assertTrue(bo.delete(3L));
        verify(dao).findByCodigo(3L);
    }
}
