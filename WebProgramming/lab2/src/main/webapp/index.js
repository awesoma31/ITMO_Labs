"use strict";

const VALID_RS = new Set([1, 1.5, 2, 2.5, 3]);

document.addEventListener("DOMContentLoaded", () => {
    /** @type {HTMLFormElement} */
    const form = document.getElementById("data-form");
    form.addEventListener("submit", submit);
    initCanvas();
});

function checkX() {
    const checkboxes = document.getElementsByName("x");
    let checked = false;

    for (let i = 0; i < checkboxes.length; i++) {
        if (checkboxes[i].checked) {
            if (checked) {
                return false;
            }
            checked = true;
        }
    }
}

/**
 * @typedef {Object} FormValues
 * @property {string|undefined} x
 * @property {string} y
 * @property {string} r
 */

/**
 * Sends the data to the server.
 * @param e {SubmitEvent}
 */
function submit(e) {
    e.preventDefault();

    /** @type {HTMLDivElement} */
    const errorDiv = document.getElementById("error");

    try {
        const data = new FormData(e.target);
        const values = Object.fromEntries(data);
        validateFormInput(values);
    } catch (e) {
        errorDiv.hidden = false;
        errorDiv.innerText = e.message;
        return;
    }

    this.submit(e);
}
/** @param values FormValues*/
function validateFormInput(values) {
    if (values.x === undefined) {
        throw new Error("x is required");
    }
    if (values.x < -2 || values.x > 2) {
        throw new Error(`x must be in [-2, 2]`);
    }

    if (values.y === undefined) {
        throw new Error("y is required");
    }
    if (Number(values.y) < -5 || Number(values.y) > 5) {
        throw new Error("y must be in [-5, 5]");
    }

    if (values.r === undefined) {
        throw new Error("r is required");
    }
    if (!VALID_RS.has(Number(values.r))) {
        throw new Error(`r must be one of [${[...VALID_RS].join(", ")}]`);
    }
}

function selectGraphCheckbox(x) {
    const checkboxes = document.querySelectorAll('input[name="x"]');

    checkboxes.forEach(cb => {
        cb.checked = false;
    });

    const graphCheckbox = document.getElementById('graphCheckbox');

    graphCheckbox.checked = true;

    const graphValueSpan = document.getElementById('graphValue');
    graphValueSpan.textContent = x.toString();
}

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

function drawShape(ctx, canvas, points) {
    const R = 100;

    ctx.clearRect(0, 0, canvas.width, canvas.height);

    ctx.translate(canvas.width / 2, canvas.height / 2);
    ctx.scale(1, -1);

    ctx.fillStyle = 'rgb(51 153 255)';
    ctx.beginPath();

    // Top right triangle
    ctx.moveTo(0, 0);
    ctx.lineTo(0, R / 2);
    ctx.lineTo(R, 0);

    // Bottom right rectangle
    ctx.lineTo(R, -R / 2);
    ctx.lineTo(0, -R / 2);

    // Bottom left arc
    ctx.arc(0, 0, R / 2, -Math.PI / 2, -Math.PI, true);

    ctx.closePath();
    ctx.fill();

    // Draw axis
    ctx.strokeStyle = "white";
    ctx.beginPath();
    ctx.moveTo(-canvas.width / 2, 0);
    ctx.lineTo(canvas.width / 2, 0);
    ctx.moveTo(0, -canvas.height / 2);
    ctx.lineTo(0, canvas.height / 2);
    ctx.stroke();

    // Reset transformations
    ctx.setTransform(1, 0, 0, 1, 0, 0);

    // Draw labels for R, R/2, etc.
    ctx.fillStyle = "white";
    ctx.font = "12px monospace";
    ctx.fillText("R", canvas.width / 2 + 6, canvas.height / 2 - R);
    ctx.fillText("R/2", canvas.width / 2 + 6, canvas.height / 2 - R / 2);
    ctx.fillText("R", canvas.width / 2 + R - 6, canvas.height / 2 + 12);
    ctx.fillText("R/2", canvas.width / 2 + R / 2 - 6, canvas.height / 2 + 12);
    ctx.fillText("-R/2", canvas.width / 2 - R / 2 - 12, canvas.height / 2 + 12);
    ctx.fillText("-R", canvas.width / 2 - R - 12, canvas.height / 2 + 15);
    ctx.fillText("-R/2", canvas.width / 2 + 6, canvas.height / 2 + R / 2 + 6);
    ctx.fillText("-R", canvas.width / 2 + 6, canvas.height / 2 + R + 6);

    drawPoints(ctx, canvas, points, R);
}

function drawPoints(ctx, canvas, points, R) {
    ctx.fillStyle = "purple";

    // Move origin to the center and flip the y-axis
    ctx.translate(canvas.width / 2, canvas.height / 2);
    ctx.scale(1, -1);

    points.forEach(point => {
        const { x, y, r } = point;

        const scaledX = (x / r) * R;
        const scaledY = (y / r) * R;

        ctx.beginPath();
        ctx.arc(scaledX, scaledY, 3, 0, Math.PI * 2);
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

function roundHalf(num) {
    return Math.round(num * 2) / 2;
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
            const errorDiv = document.getElementById("error");
            errorDiv.hidden = false;
            errorDiv.innerText = "Failed to load points data.";
        });
}
