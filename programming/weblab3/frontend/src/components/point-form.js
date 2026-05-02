// src/components/point-form.js
class PointForm extends HTMLElement {
  constructor() {
    super();
    this.attachShadow({ mode: 'open' });
  }

  connectedCallback() {
    this.render();
    this._bind();
  }

  render() {
    this.shadowRoot.innerHTML = `
      <link rel="stylesheet" href="/src/styles/point-form.css">

      <div class="card">
        <h3>Форма точки</h3>

        <div class="row">
          <input id="x" placeholder="X" inputmode="decimal">
          <input id="y" placeholder="Y" inputmode="decimal">
        </div>

        <label class="r-label">
          Радиус (R):
          <input id="r" inputmode="decimal" value="1">
        </label>

        <button id="btn-check">Проверить</button>

        <div id="status" class="status"></div>
      </div>
    `;
  }

  _bind() {
    this.shadowRoot.getElementById('btn-check')
      .addEventListener('click', () => this.check());
  }

  async check() {
    const x = Number(this.shadowRoot.getElementById('x').value.replace(',', '.'));
    const y = Number(this.shadowRoot.getElementById('y').value.replace(',', '.'));
    const r = Number(this.shadowRoot.getElementById('r').value.replace(',', '.'));
    const status = this.shadowRoot.getElementById('status');

    if (![x,y,r].every(v => Number.isFinite(v))) {
      status.textContent = 'Введите числовые X, Y, R';
      return;
    }

    if (r <= 0) {
      status.textContent = 'R должен быть > 0';
      return;
    }

    status.textContent = 'Отправка...';
    const token = localStorage.getItem('jwt_token') || '';

    try {
      const res = await fetch('/geometry/api/geometry/check', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': token ? `Bearer ${token}` : ''
        },
        body: JSON.stringify({ x, y, r })
      });

      if (res.status === 401) {
        status.textContent = 'Не авторизованы';
        window.dispatchEvent(new CustomEvent('auth:unauthorized'));
        return;
      }

      if (!res.ok) {
        status.textContent = `Ошибка сервера (${res.status})`;
        return;
      }

      const data = await res.json();
      const hit = !!data?.hit;

      status.textContent = hit ? 'Попадание' : 'Мимо';

      const payload = { x, y, r, hit, time: Date.now() };
      window.dispatchEvent(new CustomEvent('geometry:result', { detail: payload }));
      window.dispatchEvent(new CustomEvent('point-checked', { detail: payload }));

    } catch {
      status.textContent = 'Сетевая ошибка';
    }
  }
}

customElements.define('point-form', PointForm);