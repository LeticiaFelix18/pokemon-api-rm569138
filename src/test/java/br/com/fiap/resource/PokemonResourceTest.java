package br.com.fiap.resource;

import br.com.fiap.bo.PokemonBO;
import br.com.fiap.to.PokemonTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDate;
import java.util.ArrayList;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PokemonResourceTest {
    private MockMvc mvc;
    private PokemonBO bo;
    private PokemonTO pokemon;
    private String json;

    @BeforeEach
    void preparar() {
        bo = mock(PokemonBO.class);
        PokemonResource resource = new PokemonResource();
        ReflectionTestUtils.setField(resource, "pokemonBO", bo);
        mvc = MockMvcBuilders.standaloneSetup(resource).addFilters(new CorsFilter()).build();
        pokemon = new PokemonTO(7L, "Bulbasaur", 0.7, 6.9, "Semente", LocalDate.now().minusDays(1));
        json = """
                {"nome":"Bulbasaur","altura":0.7,"peso":6.9,"categoria":"Semente","dataDaCaptura":"%s"}
                """.formatted(pokemon.getDataDaCaptura());
    }

    @Test
    void getListaEGetPorCodigo() throws Exception {
        when(bo.findAll()).thenReturn(new ArrayList<>(java.util.List.of(pokemon)));
        when(bo.findByCodigo(7L)).thenReturn(pokemon);
        mvc.perform(get("/pokemon")).andExpect(status().isOk()).andExpect(jsonPath("$[0].codigo").value(7));
        mvc.perform(get("/pokemon/7")).andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Bulbasaur"));
        mvc.perform(get("/pokemon/99")).andExpect(status().isNotFound());
    }

    @Test
    void listaVaziaTemStatus200() throws Exception {
        when(bo.findAll()).thenReturn(new ArrayList<>());
        mvc.perform(get("/pokemon")).andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test
    void postRetorna201ECodigoDoBanco() throws Exception {
        when(bo.save(any())).thenReturn(pokemon);
        mvc.perform(post("/pokemon").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.codigo").value(7));
    }

    @Test
    void putUsaCodigoDaUriERetorna201ComoNaAula() throws Exception {
        when(bo.update(any())).thenAnswer(invocation -> invocation.getArgument(0));
        mvc.perform(put("/pokemon/7").contentType(MediaType.APPLICATION_JSON)
                        .content(json.replace("{", "{\"codigo\":99,")))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.codigo").value(7));
    }

    @Test
    void deleteRetorna204SemCorpoOu404() throws Exception {
        when(bo.delete(7L)).thenReturn(true);
        mvc.perform(delete("/pokemon/7")).andExpect(status().isNoContent()).andExpect(content().string(""));
        mvc.perform(delete("/pokemon/99")).andExpect(status().isNotFound());
    }

    @Test
    void beanValidationRejeitaHojeFuturoENulos() throws Exception {
        for (String body : new String[]{
                json.replace(pokemon.getDataDaCaptura().toString(), LocalDate.now().toString()),
                json.replace(pokemon.getDataDaCaptura().toString(), LocalDate.now().plusDays(1).toString()),
                "{}", json.replace("Bulbasaur", " "), json.replace("0.7", "-1")}) {
            mvc.perform(post("/pokemon").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
            mvc.perform(put("/pokemon/7").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(bo);
    }

    @Test
    void rejeitaJsonMalformadoDataInvalidaECodigoNaoNumerico() throws Exception {
        mvc.perform(post("/pokemon").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/pokemon").contentType(MediaType.APPLICATION_JSON)
                        .content(json.replace(pokemon.getDataDaCaptura().toString(), "2026-02-30")))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/pokemon/abc")).andExpect(status().isBadRequest());
        verifyNoInteractions(bo);
    }

    @Test
    void falhaDePersistenciaNaoProduz201() throws Exception {
        mvc.perform(post("/pokemon").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
        mvc.perform(put("/pokemon/99").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void falhaDeBancoNaoExpoeDetalhes() throws Exception {
        when(bo.findAll()).thenThrow(new IllegalStateException("senha-secreta"));
        mvc.perform(get("/pokemon")).andExpect(status().isInternalServerError())
                .andExpect(content().string("Erro ao consultar pokémons."));
    }

    @Test
    void preflightCorsNaoAcessaBanco() throws Exception {
        mvc.perform(options("/pokemon").header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"))
                .andExpect(header().string("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS"));
        verifyNoInteractions(bo);
    }
}
