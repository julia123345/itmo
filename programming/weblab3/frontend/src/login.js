import './components/auth-form.js'

// Если уже залогинен — перенаправляем на основную
if (localStorage.getItem('jwt_token')) {
  location.href = '/'
}

// Подписка: когда auth-form диспатчит auth:login — редирект
window.addEventListener('auth:login', () => {
  location.href = '/'
})