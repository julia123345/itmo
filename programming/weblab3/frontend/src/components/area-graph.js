class AreaGraph extends HTMLElement {
  constructor() {
    super();
    this.attachShadow({ mode: 'open' });

    this.points = [];
    this.r = 1;
    this.size = 420;
    this.scale = 40;
  }

  connectedCallback() {
    this.shadowRoot.innerHTML = `
      <link rel="stylesheet" href="/src/styles/area-graph.css">

      <div class="card">
        <h3>График области (клик для добавления)</h3>

        <div class="controls">
          <label>
            Радиус (R):
            <input id="r" type="number" step="0.5" value="${this.r}">
          </label>
        </div>

        <canvas id="c" width="${this.size}" height="${this.size}"></canvas>
      </div>
    `;

    this.canvas = this.shadowRoot.getElementById('c');
    this.ctx = this.canvas.getContext('2d');

    this.shadowRoot.getElementById('r').addEventListener('input', async (e) => {
      const v = parseFloat(e.target.value);
      if (Number.isFinite(v) && v > 0) {
        this.r = v;
        await this.recheckPoints();
        this.draw();
      }
    });

    this.canvas.addEventListener('click', (e) => this.handleClick(e));

    window.addEventListener('point-checked', (e) => {
      const d = e.detail;
      this.points.unshift(d);
      if (this.points.length > 200) this.points.pop();
      this.draw();
    });

    window.addEventListener('auth:logout', () => {
      this.points = [];
      this.draw();
    });

    this.draw();
  }

  centerX(){ return this.size/2 }
  centerY(){ return this.size/2 }

  toCanvasX(x){ return this.centerX() + x * this.scale }
  toCanvasY(y){ return this.centerY() - y * this.scale }

  fromCanvas(cx, cy){
    const rect = this.canvas.getBoundingClientRect();
    const x = (cx - rect.left - this.centerX()) / this.scale;
    const y = (this.centerY() - (cy - rect.top)) / this.scale;
    return { x, y };
  }

  drawAxes(){
    const ctx = this.ctx;
    ctx.save();
    ctx.strokeStyle = 'rgba(233,238,251,0.25)';
    ctx.lineWidth = 1;

    ctx.beginPath();
    ctx.moveTo(0, this.centerY());
    ctx.lineTo(this.size, this.centerY());
    ctx.moveTo(this.centerX(), 0);
    ctx.lineTo(this.centerX(), this.size);
    ctx.stroke();

    ctx.restore();
  }

  drawArea(){
    const ctx = this.ctx;

    const rScaled = this.r * this.scale;
    const halfRScaled = (this.r / 2) * this.scale;

    ctx.save();
    ctx.fillStyle = 'rgba(92,124,255,0.45)';

    // I квадрант — сектор R/2
    ctx.beginPath();
    ctx.moveTo(this.centerX(), this.centerY());
    ctx.arc(this.centerX(), this.centerY(), halfRScaled, 0, -Math.PI / 2, true);
    ctx.closePath();
    ctx.fill();

    // III квадрант — прямоугольник
    ctx.beginPath();
    ctx.rect(this.centerX() - rScaled, this.centerY(), rScaled, rScaled / 2);
    ctx.fill();

    // II квадрант — треугольник
    ctx.beginPath();
    ctx.moveTo(this.centerX(), this.centerY());
    ctx.lineTo(this.centerX() - rScaled, this.centerY());
    ctx.lineTo(this.centerX(), this.centerY() - (rScaled/2));
    ctx.closePath();
    ctx.fill();

    ctx.restore();
  }

  drawPoints(){
    const ctx = this.ctx;
    for (const p of this.points) {
      ctx.beginPath();
      ctx.fillStyle = p.hit ? '#6dffb2' : '#ff9aa7';
      ctx.arc(this.toCanvasX(p.x), this.toCanvasY(p.y), 4, 0, Math.PI*2);
      ctx.fill();
    }
  }

  draw(){
    const ctx = this.ctx;
    ctx.clearRect(0,0,this.size,this.size);
    this.drawArea();
    this.drawAxes();
    this.drawPoints();
  }

  async handleClick(e){
    const { x, y } = this.fromCanvas(e.clientX, e.clientY);
    const token = localStorage.getItem('jwt_token') || '';

    try {
      const res = await fetch('/geometry/api/geometry/check', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': token ? `Bearer ${token}` : ''
        },
        body: JSON.stringify({ x, y, r: this.r })
      });

      if (!res.ok) return;

      const data = await res.json();
      const payload = { x, y, r: this.r, hit: !!data.hit };

      this.points.unshift(payload);
      if (this.points.length > 200) this.points.pop();

      this.draw();
      window.dispatchEvent(new CustomEvent('point-checked', { detail: payload }));

    } catch (err) {
      console.error('Click check error', err);
    }
  }

  async recheckPoints(){
    const token = localStorage.getItem('jwt_token') || '';

    for (let p of this.points) {
      try {
        const res = await fetch('/geometry/api/geometry/check', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': token ? `Bearer ${token}` : ''
          },
          body: JSON.stringify({ x: p.x, y: p.y, r: this.r })
        });

        if (!res.ok) continue;
        const data = await res.json();
        p.hit = !!data.hit;

      } catch {}
    }
  }
}

customElements.define('area-graph', AreaGraph);