class CheckResult extends HTMLElement {
    constructor() {
        super()
        this.attachShadow({ mode: 'open' })
        this.results = []
    }

    connectedCallback() {
        this.render()

        window.addEventListener('point-checked', (e) => {
            this.results.push(e.detail)
            this.renderTable()
        })

        window.addEventListener('logout', () => {
            this.results = []
            this.renderTable()
        })
    }

    render() {
        this.shadowRoot.innerHTML = `
            <link rel="stylesheet" href="/src/styles/check-result.css">

            <h3>История проверок</h3>
            <div id="tableContainer"></div>
        `

        this.renderTable()
    }

    renderTable() {
        const container = this.shadowRoot.getElementById('tableContainer')

        if (this.results.length === 0) {
            container.innerHTML = `<p class="empty">Нет данных</p>`
            return
        }

        container.innerHTML = `
            <table>
                <thead>
                    <tr>
                        <th>X</th>
                        <th>Y</th>
                        <th>R</th>
                        <th>Результат</th>
                    </tr>
                </thead>
                <tbody>
                    ${this.results.map(r => `
                        <tr>
                            <td>${Number(r.x).toFixed(1)}</td>
                            <td>${Number(r.y).toFixed(1)}</td>
                            <td>${Number(r.r).toFixed(1)}</td>
                            <td>${r.hit ? 'Попадание' : 'Мимо '}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `
    }
}

customElements.define('check-result', CheckResult)