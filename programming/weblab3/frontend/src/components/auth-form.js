// src/components/auth-form.js
const AUTH_LOGIN_URL = '/auth/api/auth/login';
const AUTH_REGISTER_URL = '/auth/api/auth/register';

class AuthForm extends HTMLElement {
  constructor() {
    super();
    this.attachShadow({ mode: 'open' });
  }

  connectedCallback() {
    this.render();
    this._bind();
  }

  render(status = '') {
    this.shadowRoot.innerHTML = `
      <link rel="stylesheet" href="/src/styles/auth-form.css">

      <div class="card">
        <h3>Авторизация</h3>

        <input id="login" placeholder="Логин" autocomplete="username" />
        <input id="password" placeholder="Пароль" type="password" autocomplete="current-password" />

        <div class="buttons">
          <button id="btn-login">Войти</button>
          <button id="btn-register" class="alt">Регистрация</button>
        </div>

        <div id="status" class="status">${status}</div>
        <div id="error" class="error"></div>
      </div>
    `;
  }

  _bind() {
    this.shadowRoot.getElementById('btn-login')
      .addEventListener('click', () => this.login());

    this.shadowRoot.getElementById('btn-register')
      .addEventListener('click', () => this.register());
  }

  async login() {
    const login = this.shadowRoot.getElementById('login').value.trim();
    const password = this.shadowRoot.getElementById('password').value || '';
    const status = this.shadowRoot.getElementById('status');
    const error = this.shadowRoot.getElementById('error');

    error.textContent = '';
    status.textContent = 'Вход...';

    if (!login || !password) {
      error.textContent = 'Введите логин и пароль';
      status.textContent = '';
      return;
    }

    try {
      const res = await fetch(AUTH_LOGIN_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ login, password })
      });

      if (!res.ok) {
        error.textContent = `Ошибка входа (${res.status})`;
        status.textContent = '';
        return;
      }

      const data = await res.json();
      const token = data?.token ?? (typeof data === 'string' ? data : null);

      if (!token) {
        error.textContent = 'Сервер не вернул токен';
        status.textContent = '';
        return;
      }

      localStorage.setItem('jwt_token', token);
      localStorage.setItem('login', login);

      status.textContent = 'Успешно!';
      window.dispatchEvent(new CustomEvent('auth:login', { detail: { login, token } }));

    } catch {
      error.textContent = 'Сетевая ошибка при входе';
      status.textContent = '';
    }
  }

  async register() {
    const login = this.shadowRoot.getElementById('login').value.trim();
    const password = this.shadowRoot.getElementById('password').value || '';
    const status = this.shadowRoot.getElementById('status');
    const error = this.shadowRoot.getElementById('error');

    error.textContent = '';
    status.textContent = 'Регистрация...';

    if (!login || !password) {
      error.textContent = 'Введите логин и пароль';
      status.textContent = '';
      return;
    }

    try {
      const res = await fetch(AUTH_REGISTER_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ login, password })
      });

      if (res.ok) {
        status.textContent = 'Регистрация успешна. Выполните вход.';
      } else if (res.status === 409) {
        error.textContent = 'Пользователь уже существует';
        status.textContent = '';
      } else {
        error.textContent = `Ошибка регистрации (${res.status})`;
        status.textContent = '';
      }
    } catch {
      error.textContent = 'Сетевая ошибка при регистрации';
      status.textContent = '';
    }
  }
}

customElements.define('auth-form', AuthForm);