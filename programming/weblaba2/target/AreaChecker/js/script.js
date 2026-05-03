
let currentR = 0;

$(document).ready(function () {
    window.memoDraw = createMemoizedDraw(drawAreaImpl);

    if (typeof initialR !== 'undefined' && initialR > 0) {
        selectR(initialR);
    }

    window.drawArea = () => window.memoDraw.draw(currentR);

    // Клик по canvas
    $('#areaCanvas').click(function (e) {
        if (!currentR || currentR <= 0) {
            alert("Please select R value before clicking on the canvas!");
            return;
        }

        const rect = this.getBoundingClientRect();
        const centerX = 250;
        const centerY = 250;
        const scale = 40;

        const x = parseFloat(((e.clientX - rect.left - centerX) / scale).toFixed(2));
        const y = parseFloat(((centerY - (e.clientY - rect.top)) / scale).toFixed(2));

        // Отправка формы
        $('#mainForm')
            .find('input[name="x"], input[name="y"]').remove().end()
            .append($('<input>', { type: 'hidden', name: 'x', value: x }))
            .append($('<input>', { type: 'hidden', name: 'y', value: y }))
            .submit();
    });

    if (window.memoDraw) {
        window.memoDraw.draw(currentR);
    }
});

function selectR(value) {
    if (typeof value !== 'number' || value < 1 || value > 5) {
        console.warn('Invalid R value:', value);
        return;
    }
    currentR = value;

    // Обновляем форму для R
    $('#rValue').val(value);
    $('.r-option').removeClass('selected');
    $(`.r-option[data-value="${value}"]`).addClass('selected');
    $('#currentR').text(value);

    // Перерисовываем график с новым R (точки пересчитаются автоматически)
    if (window.memoDraw) {
        window.memoDraw.draw(value);
    }
}

// Декоратор функции
function createMemoizedDraw(drawFn) {
    let lastR = null;
    return {
        draw(r) {
            if (r !== lastR) {
                lastR = r;
                drawFn(r);
            }
        }
    };
}

// Динамический график
function drawAreaImpl(r) {
    const canvas = document.getElementById('areaCanvas');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    const centerX = 250;
    const centerY = 250;
    const scale = 40;

    // Очистка
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // Оси
    ctx.strokeStyle = '#000';
    ctx.lineWidth = 1;
    ctx.beginPath();
    ctx.moveTo(centerX, 0);
    ctx.lineTo(centerX, canvas.height);
    ctx.moveTo(0, centerY);
    ctx.lineTo(canvas.width, centerY);
    ctx.stroke();

    // Подписи осей
    ctx.fillStyle = '#000';
    ctx.font = '14px Arial';
    ctx.textAlign = 'left';
    ctx.textBaseline = 'top';
    ctx.fillText('X', canvas.width - 15, centerY + 5);
    ctx.textAlign = 'right';
    ctx.fillText('Y', centerX - 5, 10);

    if (r > 0) {
        // Динамические подписи
        ctx.font = '12px Arial';
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        const labels = [
            [r, centerX + r * scale, centerY + 15],
            [r / 2, centerX + (r / 2) * scale, centerY + 15],
            [-r, centerX - r * scale, centerY + 15],
            [-r / 2, centerX - (r / 2) * scale, centerY + 15],
            [r, centerX - 15, centerY - r * scale],
            [r / 2, centerX - 15, centerY - (r / 2) * scale],
            [-r, centerX - 15, centerY + r * scale],
            [-r / 2, centerX - 15, centerY + (r / 2) * scale],
        ];
        labels.forEach(([val, x, y]) => {
            ctx.fillText(val.toFixed(1), x, y);
        });

        // Заштрихованная область
        ctx.fillStyle = 'rgba(0,100,255,0.3)';
        // I четверть: [0, R] × [0, R]
        ctx.fillRect(centerX, centerY - r * scale, r * scale, r * scale);
        // III четверть: треугольник
        ctx.beginPath();
        ctx.moveTo(centerX, centerY);
        ctx.lineTo(centerX - r * scale, centerY);
        ctx.lineTo(centerX, centerY + (r / 2) * scale);
        ctx.closePath();
        ctx.fill();
        // IV четверть: сектор радиусом R/2
        ctx.beginPath();
        ctx.moveTo(centerX, centerY);
        ctx.arc(centerX, centerY, (r / 2) * scale, 0, Math.PI / 2);
        ctx.fill();
    }

    function getAllPointsFromTable() {
        const points = [];
        $('#resultsTable tbody tr').each(function () {
            const tds = $(this).find('td');
            if (tds.length >= 4) {
                const x = parseFloat(tds.eq(0).text());
                const y = parseFloat(tds.eq(1).text());
                const originalR = parseFloat(tds.eq(2).text()); // Оригинальный R из таблицы
                const originalHit = tds.eq(3).text() === 'HIT'; // Оригинальный hit из таблицы

                if (!isNaN(x) && !isNaN(y) && !isNaN(originalR)) {
                    const recalculatedHit = checkHit(x, y, r);
                    points.push({
                        x,
                        y,
                        originalR, // Сохраняем оригинальный R для отображения
                        originalHit, // Сохраняем оригинальный hit
                        recalculatedHit // Новый рассчитанный hit для текущего R
                    });
                }
            }
        });
        return points;
    }

    function drawPoint(x, y, isHit, isCurrentR) {
        ctx.beginPath();
        ctx.arc(centerX + x * scale, centerY - y * scale, 4, 0, 2 * Math.PI);
        ctx.fillStyle = isCurrentR
            ? (isHit ? 'green' : 'red')
            : (isHit ? 'rgba(0,128,0,0.4)' : 'rgba(255,0,0,0.4)');
        ctx.fill();
        ctx.strokeStyle = '#000';
        ctx.stroke();
    }

    const allPoints = getAllPointsFromTable();
    allPoints.forEach(point => {

        drawPoint(point.x, point.y, point.recalculatedHit, point.originalR === r);
    });
}

function checkHit(x, y, r) {
    // Проверяем прямоугольник в первой четверти
    if (x >= 0 && y >= 0 && x <= r && y <= r) {
        return true;
    }
    // Проверяем четверть круга в четвертой четверти
    if (x >= 0 && y <= 0 && (x * x + y * y) <= (r / 2) * (r / 2)) {
        return true;
    }
    // Проверяем треугольник в третьей четверти
    if (x <= 0 && y <= 0 && x >= -r && y >= -r / 2 && (-y * 2) <= (r + 2 * x)) {
        return true;
    }
    return false;
}


