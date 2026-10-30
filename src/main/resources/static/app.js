// Usa a API da própria aplicação, tanto no IntelliJ quanto no Render.
const API_URL = '/pokemon';
const cards = document.querySelector('#cards');
const search = document.querySelector('#search');
const category = document.querySelector('#category');
const form = document.querySelector('#pokemon-form');
const dialog = document.querySelector('#pokemon-dialog');
const deleteDialog = document.querySelector('#delete-dialog');
let pokemons = [];
let pokemonToDelete = null;
let saving = false;
let deleting = false;
let toastTimer;

function element(tag, className, text) {
    const node = document.createElement(tag);
    if (className) node.className = className;
    if (text !== undefined) node.textContent = text;
    return node;
}

function normalize(text) {
    return String(text).normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase().trim();
}

function formatDate(date) {
    return date.split('-').reverse().join('/');
}

function yesterday() {
    const date = new Date();
    date.setDate(date.getDate() - 1);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
}

function showToast(message) {
    const toast = document.querySelector('#toast');
    toast.textContent = message;
    toast.hidden = false;
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => { toast.hidden = true; }, 4500);
}

async function apiRequest(path = '', options = {}) {
    let response;
    try {
        response = await fetch(API_URL + path, { ...options, signal: AbortSignal.timeout(90000) });
    } catch (error) {
        throw new Error(options.method
            ? 'A conexão foi interrompida. Atualize a coleção para conferir se a operação foi concluída antes de tentar novamente.'
            : 'Não foi possível conectar à API. Confira sua conexão e clique em Atualizar.');
    }
    if (!response.ok) {
        if (response.status === 400) throw new Error('Confira os campos: nome e categoria são obrigatórios, altura e peso devem ser positivos e a captura deve estar no passado.');
        if (response.status === 404) throw new Error('Esse Pokémon não foi encontrado. Atualize a coleção.');
        throw new Error('A API não conseguiu acessar os dados. Tente novamente em alguns instantes.');
    }
    if (response.status === 204) {
        return null;
    }
    return response.json();
}

// As imagens são consultadas pelo nome. Não são gravadas na tabela Oracle.
async function pokemonImage(name) {
    const slug = normalize(name).replace(/♀/g, '-f').replace(/♂/g, '-m').replace(/[.'’]/g, '').replace(/\s+/g, '-');
    try {
        const response = await fetch(`https://pokeapi.co/api/v2/pokemon/${encodeURIComponent(slug)}`, { signal: AbortSignal.timeout(12000) });
        if (!response.ok) {
            return null;
        }
        const data = await response.json();
        return data.sprites.other['official-artwork'].front_default || data.sprites.front_default;
    } catch (error) {
        return null;
    }
}

function createCard(pokemon) {
    const card = element('article', 'card');
    const picture = element('div', 'card-image');
    const placeholder = element('span', 'image-placeholder', 'CARREGANDO IMAGEM…');
    picture.append(placeholder);
    const actions = element('div', 'card-actions');
    const edit = element('button', 'icon-button', '✎');
    edit.type = 'button';
    edit.setAttribute('aria-label', `Editar ${pokemon.nome}, código ${pokemon.codigo}`);
    edit.title = 'Editar Pokémon';
    edit.addEventListener('click', () => openForm(pokemon));
    const remove = element('button', 'icon-button delete', '×');
    remove.type = 'button';
    remove.setAttribute('aria-label', `Excluir ${pokemon.nome}, código ${pokemon.codigo}`);
    remove.title = 'Excluir Pokémon';
    remove.addEventListener('click', () => openDelete(pokemon));
    actions.append(edit, remove);
    picture.append(actions);
    const code = element('p', 'record-code', `REGISTRO ${String(pokemon.codigo).padStart(3, '0')}`);
    const title = element('h3', '', pokemon.nome);
    const badge = element('span', 'card-category', pokemon.categoria);
    const measures = element('div', 'card-measures');
    for (const [label, value] of [['ALTURA', `${pokemon.altura.toLocaleString('pt-BR')} m`], ['PESO', `${pokemon.peso.toLocaleString('pt-BR')} kg`]]) {
        const measure = element('div');
        measure.append(element('span', '', label), document.createTextNode(value));
        measures.append(measure);
    }
    const date = element('p', 'capture-date', `Capturado em ${formatDate(pokemon.dataDaCaptura)}`);
    card.append(picture, code, title, badge, measures, date);
    pokemonImage(pokemon.nome).then(url => {
        if (!url) { placeholder.textContent = 'IMAGEM NÃO ENCONTRADA'; return; }
        const image = element('img');
        image.alt = pokemon.nome;
        image.loading = 'lazy';
        image.addEventListener('load', () => { placeholder.hidden = true; });
        image.addEventListener('error', () => { image.remove(); placeholder.hidden = false; placeholder.textContent = 'IMAGEM INDISPONÍVEL'; });
        image.src = url;
        picture.insertBefore(image, actions);
    });
    return card;
}

function renderCollection() {
    const query = normalize(search.value);
    const filtered = pokemons.filter(pokemon =>
        (normalize(pokemon.nome).includes(query) || String(pokemon.codigo).includes(query))
        && (!category.value || pokemon.categoria === category.value));
    cards.replaceChildren(...filtered.map(createCard));
    document.querySelector('#collection-count').textContent = pokemons.length;
    document.querySelector('#result-count').textContent = `${filtered.length} ${filtered.length === 1 ? 'captura' : 'capturas'}`;
    const empty = document.querySelector('#empty-state');
    empty.hidden = filtered.length > 0;
    document.querySelector('#empty-title').textContent = pokemons.length ? 'Nenhum Pokémon por aqui.' : 'Sua aventura começa aqui.';
    document.querySelector('#empty-description').textContent = pokemons.length ? 'Tente outro nome, código ou categoria.' : 'Adicione seu primeiro Pokémon à coleção.';
    document.querySelector('#empty-add').hidden = pokemons.length > 0;
}

async function loadCollection() {
    const reload = document.querySelector('#reload');
    const notice = document.querySelector('#notice');
    notice.hidden = true;
    document.querySelector('#loading').hidden = false;
    document.querySelector('#empty-state').hidden = true;
    cards.setAttribute('aria-busy', 'true');
    reload.disabled = true;
    try {
        pokemons = await apiRequest();
        const currentCategory = category.value;
        category.replaceChildren(new Option('Todas as categorias', ''));
        const categories = [];
        for (const pokemon of pokemons) {
            if (!categories.includes(pokemon.categoria)) {
                categories.push(pokemon.categoria);
            }
        }
        categories.sort();
        for (const value of categories) {
            category.append(new Option(value, value));
        }
        if ([...category.options].some(option => option.value === currentCategory)) category.value = currentCategory;
        renderCollection();
    } catch (error) {
        notice.textContent = error.message;
        notice.hidden = false;
    } finally {
        document.querySelector('#loading').hidden = true;
        cards.setAttribute('aria-busy', 'false');
        reload.disabled = false;
    }
}

function openForm(pokemon = null) {
    form.reset();
    document.querySelector('#form-error').hidden = true;
    document.querySelector('#pokemon-code').value = '';
    document.querySelector('#form-title').textContent = pokemon ? 'Editar captura' : 'Nova captura';
    document.querySelector('#save-button').textContent = pokemon ? 'Salvar alterações' : 'Salvar captura';
    document.querySelector('#pokemon-date').max = yesterday();
    if (pokemon) {
        document.querySelector('#pokemon-code').value = pokemon.codigo;
        document.querySelector('#pokemon-name').value = pokemon.nome;
        document.querySelector('#pokemon-height').value = pokemon.altura;
        document.querySelector('#pokemon-weight').value = pokemon.peso;
        document.querySelector('#pokemon-category').value = pokemon.categoria;
        document.querySelector('#pokemon-date').value = pokemon.dataDaCaptura;
    }
    dialog.showModal();
}

function closeForm() { if (!saving) dialog.close(); }

form.addEventListener('submit', async event => {
    event.preventDefault();
    if (saving) return;
    const code = document.querySelector('#pokemon-code').value;
    const pokemon = {
        nome: document.querySelector('#pokemon-name').value.trim(),
        altura: Number(document.querySelector('#pokemon-height').value),
        peso: Number(document.querySelector('#pokemon-weight').value),
        categoria: document.querySelector('#pokemon-category').value.trim(),
        dataDaCaptura: document.querySelector('#pokemon-date').value
    };
    const errorMessage = document.querySelector('#form-error');
    errorMessage.hidden = true;
    if (!pokemon.nome || !pokemon.categoria || pokemon.dataDaCaptura > yesterday()) {
        errorMessage.textContent = 'Preencha nome e categoria e escolha uma data anterior a hoje.';
        errorMessage.hidden = false;
        return;
    }
    saving = true;
    const saveButton = document.querySelector('#save-button');
    saveButton.disabled = true;
    saveButton.textContent = 'Salvando…';
    try {
        let path = '';
        let method = 'POST';
        if (code) {
            path = `/${code}`;
            method = 'PUT';
        }
        await apiRequest(path, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(pokemon)
        });
        dialog.close();
        showToast(code ? 'Captura atualizada!' : 'Pokémon adicionado à coleção!');
        search.value = '';
        category.value = '';
        await loadCollection();
    } catch (error) {
        errorMessage.textContent = error.message;
        errorMessage.hidden = false;
    } finally {
        saving = false;
        saveButton.disabled = false;
        saveButton.textContent = code ? 'Salvar alterações' : 'Salvar captura';
    }
});

function openDelete(pokemon) {
    pokemonToDelete = pokemon;
    document.querySelector('#delete-description').textContent = `Deseja excluir ${pokemon.nome} (registro ${pokemon.codigo}) da sua coleção?`;
    document.querySelector('#delete-error').hidden = true;
    deleteDialog.showModal();
}

document.querySelector('#confirm-delete').addEventListener('click', async () => {
    if (!pokemonToDelete || deleting) return;
    deleting = true;
    const button = document.querySelector('#confirm-delete');
    button.disabled = true;
    button.textContent = 'Excluindo…';
    try {
        await apiRequest(`/${pokemonToDelete.codigo}`, { method: 'DELETE' });
        deleteDialog.close();
        showToast('Pokémon removido da coleção.');
        await loadCollection();
    } catch (error) {
        document.querySelector('#delete-error').textContent = error.message;
        document.querySelector('#delete-error').hidden = false;
    } finally {
        deleting = false;
        button.disabled = false;
        button.textContent = 'Excluir Pokémon';
    }
});

document.querySelector('#add-pokemon').addEventListener('click', () => openForm());
document.querySelector('#empty-add').addEventListener('click', () => openForm());
document.querySelector('#close-dialog').addEventListener('click', closeForm);
document.querySelector('#cancel-form').addEventListener('click', closeForm);
document.querySelector('#cancel-delete').addEventListener('click', () => { if (!deleting) deleteDialog.close(); });
dialog.addEventListener('cancel', event => { if (saving) event.preventDefault(); });
deleteDialog.addEventListener('cancel', event => { if (deleting) event.preventDefault(); });
search.addEventListener('input', renderCollection);
category.addEventListener('change', renderCollection);
document.querySelector('#reload').addEventListener('click', loadCollection);
loadCollection();
