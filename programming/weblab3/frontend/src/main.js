import './components/auth-form.js'
import './components/point-form.js'
import './components/check-result.js'
import './components/area-graph.js'

// Если нет токена — отправим на страницу логина
if (!localStorage.getItem('jwt_token')) {
  // оставляем возможность открыть login.html напрямую
  if (location.pathname !== '/login.html') {
    location.href = '/login.html'
  }
}

const topControls = document.getElementById('top-controls')

function renderHeader() {
  const login = localStorage.getItem('login') || ''
  if (!topControls) return

  if (localStorage.getItem('jwt_token')) {
    topControls.innerHTML = `
      <span style="color:var(--muted);margin-right:12px">Пользователь: ${login}</span>
      <button id="btn-logout">Выйти</button>
    `
    document.getElementById('btn-logout').onclick = () => {
      localStorage.removeItem('jwt_token')
      localStorage.removeItem('login')
      location.href = '/login.html'
    }
  } else {
    topControls.innerHTML = `<a href="/login.html" style="color:var(--muted)">Войти</a>`
  }
}

renderHeader()

// Подписки: при успешном логине (из auth-form) возвращаем пользователя на /
window.addEventListener('auth:login', () => {
  renderHeader()
  location.href = '/'
})

// Если geometry сообщил, обновим header/прочее (точки рисуются компонентом)
window.addEventListener('auth:logout', () => {
  localStorage.removeItem('jwt_token')
  localStorage.removeItem('login')
  renderHeader()
})