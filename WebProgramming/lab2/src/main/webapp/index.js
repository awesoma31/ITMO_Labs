"use strict";

const VALID_XS = new Set([-2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2]);
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
 * @param event {SubmitEvent}
 */
function submit(event) {
    event.preventDefault();

    /** @type {HTMLDivElement} */
    const errorDiv = document.getElementById("error");

    try {
        const data = new FormData(event.target);
        const values = Object.fromEntries(data);
        validateFormInput(values);
    } catch (e) {
        errorDiv.hidden = false;
        errorDiv.innerText = e.message;
        return;
    }

    this.submit(event);
}
/** @param values FormValues*/
function validateFormInput(values) {
    if (values.x === undefined) {
        throw new Error("x is required");
    }
    if (!VALID_XS.has(Number(values.x))) {
        throw new Error(`x must be one of [${[...VALID_XS].join(", ")}]`);
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

function initCanvas() {
    const canvas = document.getElementById("coordinatePlane");
    const ctx = canvas.getContext("2d");

    canvas.addEventListener("click", function (e) {
        //todo:
        drawShape(ctx, canvas);
    });
}

function drawShape(ctx, canvas) {
    const R = 100;

    ctx.clearRect(0, 0, canvas.width, canvas.height);

    ctx.translate(canvas.width / 2, canvas.height / 2);
    ctx.scale(1, -1);

    ctx.fillStyle = 'rgb(51 153 255)'
    ctx.beginPath();

    //top right triangle
    ctx.moveTo(0, 0);
    ctx.lineTo(0, R/2);
    ctx.lineTo(R, 0);

    // Bottom right rectangle
    ctx.lineTo(R, -R/2);
    ctx.lineTo(0, -R/2);

    // Bottom left arc
    ctx.arc(0, 0, R / 2, -Math.PI / 2, -Math.PI, true);


    ctx.closePath();
    ctx.fill();

    // Axis
    ctx.strokeStyle = "white";
    ctx.beginPath();
    ctx.moveTo(-canvas.width / 2, 0);
    ctx.lineTo(canvas.width / 2, 0);
    ctx.moveTo(0, -canvas.height / 2);
    ctx.lineTo(0, canvas.height / 2);
    ctx.stroke();

    ctx.setTransform(1, 0, 0, 1, 0, 0);

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
}
