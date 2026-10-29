package br.com.fiap.resource;

import br.com.fiap.bo.PokemonBO;
import br.com.fiap.to.PokemonTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/pokemon")
public class PokemonResource {
    private PokemonBO pokemonBO = new PokemonBO();

    @GetMapping
    public ResponseEntity<?> findAll() {
        try {
            List<PokemonTO> pokemons = pokemonBO.findAll();
            if (pokemons != null) {
                return ResponseEntity.status(HttpStatus.OK).body(pokemons);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao consultar pokémons.");
        }
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<?> findByCodigo(@PathVariable Long codigo) {
        try {
            PokemonTO pokemon = pokemonBO.findByCodigo(codigo);
            if (pokemon != null) {
                return ResponseEntity.status(HttpStatus.OK).body(pokemon);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pokémon não encontrado!");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao consultar pokémon.");
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid PokemonTO pokemon) {
        try {
            // O código é gerado no banco, não pelo cliente.
            pokemon.setCodigo(null);
            PokemonTO resultado = pokemonBO.save(pokemon);
            if (resultado != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao salvar pokémon.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao salvar pokémon.");
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<?> update(@PathVariable Long codigo, @RequestBody @Valid PokemonTO pokemon) {
        try {
            pokemon.setCodigo(codigo);
            PokemonTO resultado = pokemonBO.update(pokemon);
            if (resultado != null) {
                // A43: a atualização bem-sucedida retorna 201 Created.
                return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao atualizar pokémon.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao atualizar pokémon.");
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> delete(@PathVariable Long codigo) {
        try {
            if (pokemonBO.delete(codigo)) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pokémon não encontrado!");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao excluir pokémon.");
        }
    }
}
