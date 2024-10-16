const cnvScale = 30;


function drawDot(x, y, result) {
    const canvas = document.getElementById("graphCanvas");
    const ctx = canvas.getContext("2d");

    if (result === "true") {
        ctx.fillStyle = "purple";
    }
    if (result !== "true"){
        ctx.fillStyle = "red";
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

    ctx.clearRect(0, 0, canvas.width, canvas.height);

    ctx.translate(canvas.width / 2, canvas.height / 2);
    ctx.scale(1, -1);

    drawPolygon(ctx, r)
    drawAxis(ctx, canvas);
    drawAxisLabels(ctx, canvas);
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