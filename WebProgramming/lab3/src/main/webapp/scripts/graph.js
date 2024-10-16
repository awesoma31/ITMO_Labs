const cnvScale = 30;


function drawDot(x, y, result) {
    const canvas = document.getElementById("graphCanvas");
    const ctx = canvas.getContext("2d");

    if (result === true) {
        ctx.fillStyle = "purple"
    } else {
        ctx.fillStyle = "red"
    }

    ctx.beginPath();
    ctx.arc(x*cnvScale, y*cnvScale, 4, 0, Math.PI * 2);
    ctx.fill();
    // console.log("point drawn with scale: x:" + x*cnvScale + ", y: " + y*cnvScale + ", res:" + result )
    // console.log("point drawn: x:" + x + ", y: " + y + ", res:" + result )
}

function redrawFigure(r) {
    const canvas = document.getElementById("graphCanvas");
    const ctx = canvas.getContext("2d");

    canvas.addEventListener("click", function () {
        const rect = canvas.getBoundingClientRect();
        const xDom = event.clientX - rect.left - canvas.width / 2;
        const yDom = canvas.height / 2 - (event.clientY - rect.top);

        try {

            const x = Math.round(xDom * (r*100 / (canvas.width / 4))) / 100;
            const y = Math.round(yDom * (r*100 / (canvas.height / 4))) / 100;
            console.log("x: " + x + ", y: " + y + ", r: " + r);

            sendPoint(x, y, r);
        } catch (e) {
            /** @type {HTMLDivElement} */
            const errorDiv = document.getElementById("error");
            // errorDiv.hidden = false;
            console.log(e);
            // errorDiv.innerText = e.message;
        }
    });

    ctx.clearRect(0, 0, canvas.width, canvas.height);

    ctx.translate(canvas.width / 2, canvas.height / 2);
    ctx.scale(1, -1);

    drawPolygon(ctx, r)
    drawAxis(ctx, canvas);
    drawAxisLabels(ctx, canvas, r);
}

function sendPoint(x, y, r) {
    const hiddenX = document.getElementById("data-form:hiddenX");
    const hiddenY = document.getElementById("data-form:hiddenY");
    const hiddenR = document.getElementById("data-form:hiddenR");

    hiddenX.value = x;
    hiddenY.value = y;
    hiddenR.value = r;

    document.getElementById("data-form:submitButton").click();
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

function drawAxis(ctx, canvas) {
    ctx.strokeStyle = "white";
    ctx.beginPath();
    ctx.moveTo(-canvas.width / 2, 0);
    ctx.lineTo(canvas.width / 2, 0);
    ctx.moveTo(0, -canvas.height / 2);
    ctx.lineTo(0, canvas.height / 2);
    ctx.stroke();
}

function drawAxisLabels(ctx, canvas, r) {
    const a = r*cnvScale;
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
