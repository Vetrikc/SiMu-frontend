// Player, library search and confirmation dialogs. No backend URLs here: every request goes
// through this application's own controllers.
document.addEventListener('DOMContentLoaded', () => {
    const player = document.getElementById('player');
    const title = document.getElementById('player-title');
    const subtitle = document.getElementById('player-subtitle');

    document.querySelectorAll('.play-btn').forEach((button) => {
        button.addEventListener('click', () => {
            if (!player || !button.dataset.src) {
                return;
            }
            player.src = button.dataset.src;
            player.play().catch(() => {
                // autoplay may be blocked until the user interacts - the controls stay usable
            });
            if (title) {
                title.textContent = button.dataset.title || 'Трек';
            }
            if (subtitle) {
                subtitle.textContent = button.dataset.artist || '';
            }
        });
    });

    const search = document.getElementById('search');
    if (search) {
        const cards = Array.from(document.querySelectorAll('[data-track]'));
        const emptyState = document.getElementById('no-results');

        search.addEventListener('input', () => {
            const query = search.value.trim().toLowerCase();
            let visible = 0;
            cards.forEach((card) => {
                const haystack = (card.dataset.search || '').toLowerCase();
                const matches = !query || haystack.includes(query);
                card.classList.toggle('hidden', !matches);
                if (matches) {
                    visible += 1;
                }
            });
            if (emptyState) {
                emptyState.classList.toggle('hidden', visible > 0);
            }
        });
    }

    document.querySelectorAll('form[data-confirm]').forEach((form) => {
        form.addEventListener('submit', (event) => {
            if (!window.confirm(form.dataset.confirm)) {
                event.preventDefault();
            }
        });
    });
});
