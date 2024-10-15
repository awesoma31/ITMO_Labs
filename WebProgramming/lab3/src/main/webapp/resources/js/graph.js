document.addEventListener("DOMContentLoaded", () => {
    /** @type {HTMLFormElement} */
    // const form = document.getElementById("data-form");
    // form.addEventListener("submit", submit);
    initCanvas();
});

function initCanvas() {
    const canvas = document.getElementById("coordinatePlane");
    const ctx = canvas.getContext("2d");

    fetchPoints(ctx, canvas);

    canvas.addEventListener("click", function () {
        const rect = canvas.getBoundingClientRect();
        const xDom = event.clientX - rect.left - canvas.width / 2;
        const yDom = canvas.height / 2 - (event.clientY - rect.top);

        try {
            const r = getR();
            const x = Math.round(xDom * (r / (canvas.width / 4))) / 100;
            const y = Math.round(yDom * (r / (canvas.height / 4))) / 100;
            console.log("x: " + x + ", y: " + y + ", r: " + r/100);

            selectGraphCheckbox(x);

            sendPoint(x, y, r);
        } catch (e) {
            /** @type {HTMLDivElement} */
            const errorDiv = document.getElementById("error");
            errorDiv.hidden = false;
            errorDiv.innerText = e.message;
        }
    });

}

function drawAxis(ctx, canvas) {
    ctx.strokeStyle = "white";
    ctx.beginPath();
    ctx.moveTo(-canvas.width / 2, 0);
    ctx.lineTo(canvas.width / 2, 0);
    ctx.moveTo(0, -canvas.height / 2);
    ctx.lineTo(0, canvas.height / 2);
    ctx.stroke();
}

function drawAxisLabels(ctx, canvas) {
    const radioVal = getR() / 100;
    const a = radioVal*cnvScale;
    ctx.fillStyle = "white";
    ctx.font = "12px monospace";
    ctx.fillText("R", canvas.width / 2 + 6, canvas.height / 2 - a);
    ctx.fillText("R/2", canvas.width / 2 + 6, canvas.height / 2 - a / 2);
    ctx.fillText("R", canvas.width / 2 + a - 6, canvas.height / 2 + 12);
    ctx.fillText("R/2", canvas.width / 2 + a / 2 - 6, canvas.height / 2 + 12);
    ctx.fillText("-R/2", canvas.width / 2 - a / 2 - 12, canvas.height / 2 + 12);
    ctx.fillText("-R", canvas.width / 2 - a - 12, canvas.height / 2 + 15);
    ctx.fillText("-R/2", canvas.width / 2 + 6, canvas.height / 2 + a / 2 + 6);
    ctx.fillText("-R", canvas.width / 2 + 6, canvas.height / 2 + a + 6);
}

function drawPolygon(ctx, radioVal) {
    ctx.fillStyle = 'rgb(51 153 255)';
    ctx.beginPath();

    // Top right triangle
    ctx.moveTo(0, 0);
    const a = radioVal*cnvScale;
    ctx.lineTo(0, a / 2);
    ctx.lineTo(a, 0);

    // Bottom right rectangle
    ctx.lineTo(a, -a / 2);
    ctx.lineTo(0, -a / 2);

    // Bottom left arc
    ctx.arc(0, 0, a / 2, -Math.PI / 2, -Math.PI, true);

    ctx.closePath();
    ctx.fill();
}

function redrawCanvas(number) {
    const canvas = document.getElementById("coordinatePlane");
    const ctx = canvas.getContext("2d");
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    fetchPoints(ctx, canvas);
}

function drawShape(ctx, canvas, points) {
    const radioVal = getR() / 100;

    ctx.clearRect(0, 0, canvas.width, canvas.height);

    ctx.translate(canvas.width / 2, canvas.height / 2);
    ctx.scale(1, -1);

    drawPolygon(ctx, radioVal);

    drawAxis(ctx, canvas);
    ctx.setTransform(1, 0, 0, 1, 0, 0);
    drawAxisLabels(ctx, canvas);
    drawPoints(ctx, canvas, points, radioVal);
}

function drawPoints(ctx, canvas, points, R) {
    ctx.fillStyle = "purple";

    ctx.translate(canvas.width / 2, canvas.height / 2);
    ctx.scale(1, -1);

    points.forEach(point => {
        const { x, y, r } = point;

        const scaledX = (x / r) * R;
        const scaledY = (y / r) * R;

        ctx.beginPath();
        ctx.arc(x*cnvScale, y*cnvScale, 3, 0, Math.PI * 2);
        ctx.fill();
    });

    ctx.setTransform(1, 0, 0, 1, 0, 0);
}



function getR() {
    const rs = Array.from(
        document.getElementsByName("r")).filter(e => e.checked
    );
    if (rs.length !== 1) {
        throw new Error("Exactly one r must be chosen");
    }
    return 100 * Number(rs[0].value);
}

function sendPoint(x, y, r) {
    // /** @type {HTMLFormElement} */
    const form = document.getElementById("data-form");

    const checkboxes = document.querySelectorAll('input[name="x"]');

    checkboxes.forEach(cb => {
        cb.checked = false;
    });

    const graphCheckbox = document.getElementById('graphCheckbox');

    graphCheckbox.checked = true;
    graphCheckbox.value = x;

    form["y"].value = y;
    form["r"].value = r;

    form.submit();
}

function fetchPoints(ctx, canvas) {
    fetch('getPoints')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            return response.json();
        })
        .then(pts => {
            drawShape(ctx, canvas, pts);
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });
}