package br.com.fiap.to;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PokemonTOTest {
    @Test
    void aceitaPokemonValido() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            PokemonTO pokemon = new PokemonTO(null, "Bulbasaur", 0.7, 6.9, "Semente", LocalDate.now().minusDays(1));
            assertTrue(factory.getValidator().validate(pokemon).isEmpty());
        }
    }

    @Test
    void rejeitaCamposInvalidos() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            PokemonTO pokemon = new PokemonTO(null, " ", 0.0, -1.0, "", LocalDate.now());
            assertEquals(5, factory.getValidator().validate(pokemon).size());
        }
    }

    @Test
    void rejeitaDataFuturaENula() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            PokemonTO pokemon = new PokemonTO(null, "Bulbasaur", 0.7, 6.9, "Semente", LocalDate.now().plusDays(1));
            assertFalse(factory.getValidator().validate(pokemon).isEmpty());
            pokemon.setDataDaCaptura(null);
            assertFalse(factory.getValidator().validate(pokemon).isEmpty());
        }
    }
}
