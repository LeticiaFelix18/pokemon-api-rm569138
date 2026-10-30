package br.com.fiap.dao;

import br.com.fiap.to.PokemonTO;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PokemonDAO {
    public ArrayList<PokemonTO> findAll() {
        ArrayList<PokemonTO> pokemons = new ArrayList<>();
        String sql = "select * from ddd_pokemons order by codigo";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PokemonTO pokemon = new PokemonTO();
                pokemon.setCodigo(rs.getLong("codigo"));
                pokemon.setNome(rs.getString("nome"));
                pokemon.setAltura(rs.getDouble("altura"));
                pokemon.setPeso(rs.getDouble("peso"));
                pokemon.setCategoria(rs.getString("categoria"));
                pokemon.setDataDaCaptura(rs.getDate("data_da_captura").toLocalDate());
                pokemons.add(pokemon);
            }
            return pokemons;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao consultar pokémons.", e);
        }
    }

    public PokemonTO findByCodigo(Long codigo) {
        String sql = "select * from ddd_pokemons where codigo = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PokemonTO pokemon = new PokemonTO();
                    pokemon.setCodigo(rs.getLong("codigo"));
                    pokemon.setNome(rs.getString("nome"));
                    pokemon.setAltura(rs.getDouble("altura"));
                    pokemon.setPeso(rs.getDouble("peso"));
                    pokemon.setCategoria(rs.getString("categoria"));
                    pokemon.setDataDaCaptura(rs.getDate("data_da_captura").toLocalDate());
                    return pokemon;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao consultar pokémon.", e);
        }
    }

    public PokemonTO save(PokemonTO pokemon) {
        String sql = "insert into ddd_pokemons (nome, altura, peso, categoria, data_da_captura) values (?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, new String[]{"CODIGO"})) {
            ps.setString(1, pokemon.getNome());
            ps.setDouble(2, pokemon.getAltura());
            ps.setDouble(3, pokemon.getPeso());
            ps.setString(4, pokemon.getCategoria());
            ps.setDate(5, Date.valueOf(pokemon.getDataDaCaptura()));
            if (ps.executeUpdate() == 0) {
                return null;
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new IllegalStateException("Código gerado não retornado pelo Oracle.");
                }
                pokemon.setCodigo(rs.getLong(1));
            }
            return pokemon;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao salvar pokémon.", e);
        }
    }

    public PokemonTO update(PokemonTO pokemon) {
        String sql = "update ddd_pokemons set nome = ?, altura = ?, peso = ?, categoria = ?, data_da_captura = ? where codigo = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, pokemon.getNome());
            ps.setDouble(2, pokemon.getAltura());
            ps.setDouble(3, pokemon.getPeso());
            ps.setString(4, pokemon.getCategoria());
            ps.setDate(5, Date.valueOf(pokemon.getDataDaCaptura()));
            ps.setLong(6, pokemon.getCodigo());
            if (ps.executeUpdate() > 0) {
                return pokemon;
            }
            return null;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao atualizar pokémon.", e);
        }
    }

    public boolean delete(Long codigo) {
        String sql = "delete from ddd_pokemons where codigo = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, codigo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao excluir pokémon.", e);
        }
    }
}
