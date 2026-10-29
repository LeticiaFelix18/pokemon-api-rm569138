package br.com.fiap.dao;

import br.com.fiap.to.PokemonTO;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PokemonDAOTest {
    @Test
    void insertRecuperaCodigoGeradoEFechaRecursos() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet keys = mock(ResultSet.class);
        when(connection.prepareStatement(anyString(), any(String[].class))).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);
        when(ps.getGeneratedKeys()).thenReturn(keys);
        when(keys.next()).thenReturn(true);
        when(keys.getLong(1)).thenReturn(42L);
        PokemonTO pokemon = new PokemonTO(null, "Bulbasaur", 0.7, 6.9, "Semente", LocalDate.of(2026, 10, 1));
        try (MockedStatic<ConnectionFactory> factory = mockStatic(ConnectionFactory.class)) {
            factory.when(ConnectionFactory::getConnection).thenReturn(connection);
            assertSame(pokemon, new PokemonDAO().save(pokemon));
        }
        assertEquals(42L, pokemon.getCodigo());
        verify(ps).setString(1, "Bulbasaur");
        verify(ps).setDate(5, java.sql.Date.valueOf("2026-10-01"));
        verify(keys).close();
        verify(ps).close();
        verify(connection).close();
    }

    @Test
    void buscaMapeiaRegistroEUsaParametro() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getLong("codigo")).thenReturn(7L);
        when(rs.getString("nome")).thenReturn("Bulbasaur");
        when(rs.getDouble("altura")).thenReturn(0.7);
        when(rs.getDouble("peso")).thenReturn(6.9);
        when(rs.getString("categoria")).thenReturn("Semente");
        when(rs.getDate("data_da_captura")).thenReturn(java.sql.Date.valueOf("2026-10-01"));
        try (MockedStatic<ConnectionFactory> factory = mockStatic(ConnectionFactory.class)) {
            factory.when(ConnectionFactory::getConnection).thenReturn(connection);
            PokemonTO resultado = new PokemonDAO().findByCodigo(7L);
            assertEquals(7L, resultado.getCodigo());
            assertEquals("Bulbasaur", resultado.getNome());
            assertEquals(0.7, resultado.getAltura());
            assertEquals(6.9, resultado.getPeso());
            assertEquals("Semente", resultado.getCategoria());
            assertEquals(LocalDate.of(2026, 10, 1), resultado.getDataDaCaptura());
        }
        verify(ps).setLong(1, 7L);
        verify(connection).close();
    }

    @Test
    void ausenciaNaoEhSucessoEmBuscaUpdateOuDelete() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        PokemonTO pokemon = new PokemonTO(9L, "Bulbasaur", 0.7, 6.9, "Semente", LocalDate.of(2026, 10, 1));
        try (MockedStatic<ConnectionFactory> factory = mockStatic(ConnectionFactory.class)) {
            factory.when(ConnectionFactory::getConnection).thenReturn(connection);
            PokemonDAO dao = new PokemonDAO();
            assertNull(dao.findByCodigo(9L));
            assertNull(dao.update(pokemon));
            assertFalse(dao.delete(9L));
            assertTrue(dao.findAll().isEmpty());
        }
        verify(ps).setLong(6, 9L);
        verify(connection, times(4)).close();
    }

    @Test
    void falhaSQLNaoViraListaVazia() throws Exception {
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("detalhes privados"));
        try (MockedStatic<ConnectionFactory> factory = mockStatic(ConnectionFactory.class)) {
            factory.when(ConnectionFactory::getConnection).thenReturn(connection);
            assertThrows(IllegalStateException.class, () -> new PokemonDAO().findAll());
        }
        verify(connection).close();
    }
}
