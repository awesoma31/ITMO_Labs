"use strict";

class InvalidValueException extends Error {
    constructor(message) {
        super(message);
        this.name = "InvalidValueException";
    }
}

const state = {
    x: 0,
    y: 0,
    r: 1.0,
};

let x;

/** @type HTMLFormElement */
const form = document.getElementById("data-form");

const buttons = document.querySelectorAll('.x')
buttons.forEach(button => {
    button.addEventListener('click', function() {
        x = this.getAttribute('value');
        document.getElementById('chosenX').textContent = x;
    });
});

form.addEventListener("submit", onSubmit);

/**
 * @typedef {Object} FormValues
 * @property {string|undefined} x
 * @property {string} y
 * @property {string} r
 */

/** @param values FormValues
 * @throws InvalidValueException If any of the values are not valid
 */
function validateFormInput(values) {
    state.x = values.x;
    state.y = values.y;
    state.r = values.r;
    if (values.x === undefined) {
        console.log("x undefined")
        throw new InvalidValueException("please select x");
    }

    if (isNaN(values.y)) {
        console.log("y isNaN")
        throw new InvalidValueException("invalid y value");
    }

    const y = parseInt(state.y);
    if (y < -5 || y > 5) {
        throw new InvalidValueException("y is out of bounds (allowed range -5..5)")
    }

    if (isNaN(values.r)) {
        console.log("invalid r invalid")
        throw new InvalidValueException("invalid r value")
    }
}

/** @type HTMLTableElement */
const table = document.getElementById("result-table");

/** @type HTMLDivElement */
const errorDiv = document.getElementById("error");

/** @param {SubmitEvent} ev */
async function onSubmit(ev) {
    ev.preventDefault();

    const formData = new FormData(this);
    /** @type FormValues */
    const values = Object.fromEntries(formData);
    values.x = state.x;
    values.y = state.y;
    values.r = state.r;

    values.x = x;

    formData.set("x", x)
    console.log(formData)

    try {
        validateFormInput(values);
        errorDiv.hidden = true;
    } catch (err) {
        console.log(err)
        this.reset();
        errorDiv.hidden = false;
        errorDiv.textContent = err.message;
        return;
    }

    const params = new URLSearchParams(formData);
    const url = "/fcgi-bin/app.jar?" + params.toString();
    // const url = "/calculate?" + params.toString();
    console.log(url)

    const response = await fetch(url);

    const newRow = table.insertRow(-1);

    const rowX = newRow.insertCell(0);
    const rowY = newRow.insertCell(1);
    const rowR = newRow.insertCell(2);
    const rowTime = newRow.insertCell(3);
    const rowExecTime = newRow.insertCell(4);
    const rowResult = newRow.insertCell(5);

    const results = {
        x: state.x,
        y: state.y,
        r: state.r,
        execTime: "",
        time: "",
        result: false,
    };

    rowX.textContent = state.x;
    rowY.textContent = state.y;
    rowR.textContent = state.r;

    if (response.ok) {
        /** @type {{result: string, time: string, now: string}} */
        const res = await response.json();

        results.result = res.result;
        results.time = res.now;
        results.execTime = res.time;

        rowResult.textContent = res.result;
        rowTime.textContent = new Date(res.now).toLocaleString();
        rowExecTime.textContent = res.time;
    } else if (response.status === 400) {
        const res = await response.json();
        rowTime.textContent = new Date(res.now).toLocaleString();
        rowExecTime.textContent = "N/A";
        rowResult.textContent = `error: ${res.reason}`;
    } else {
        rowTime.textContent = "N/A";
        rowExecTime.textContent = "N/A";
        rowResult.textContent = "error";
    }

    const prevResults = JSON.parse(localStorage.getItem("results") || "[]");
    localStorage.setItem("results", JSON.stringify([...prevResults, results]));

    rowX.innerText = results.x.toString();
    rowY.innerText = results.y.toString();
    rowR.innerText = results.r.toString();
    rowTime.innerText = results.time;
    rowExecTime.innerText = results.execTime;
    rowResult.innerText = results.result;
}

const prevResults = JSON.parse(localStorage.getItem("results") || "[]");

prevResults.forEach(result => {
    const table = document.getElementById("result-table");

    const newRow = table.insertRow(-1);

    const rowX = newRow.insertCell(0);
    const rowY = newRow.insertCell(1);
    const rowR = newRow.insertCell(2);
    const rowTime = newRow.insertCell(3);
    const rowExecTime = newRow.insertCell(4);
    const rowResult = newRow.insertCell(5);

    rowX.innerText = result.x.toString();
    rowY.innerText = result.y.toString();
    rowR.innerText = result.r.toString();
    rowTime.innerText = result.time.toString();
    rowExecTime.innerText = result.execTime;
    rowResult.innerText = result.result;
});


