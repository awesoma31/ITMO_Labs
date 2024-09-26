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

const canvas = document.getElementById('coordinatePlane');
const ctx = canvas.getContext('2d');

function validateXs() {
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

document.getElementById("data-form").addEventListener("submit", submit);

/**
 * Sends the data to the server.
 * @param ev {SubmitEvent}
 */
function submit(ev) {
    ev.preventDefault();

    try {
        const data = new FormData(ev.target);
        const values = Object.fromEntries(data);
        validateFormInput(values);
    } catch (e) {
        alert(e.message);
        return;
    }

    this.submit(ev);
}
/** @param values FormValues
 * @throws InvalidValueException If any of the values are not valid
 */
function validateFormInput(values) {
    event.preventDefault();

    // state.x = values.x;
    // state.y = values.y;
    // state.r = values.r;

    if (state.x === undefined) {
        console.log("x undefined")
        throw new InvalidValueException("please select x");
    }

    if (isNaN(state.y)) {
        console.log("y isNaN")
        throw new InvalidValueException("invalid y value");
    }

    const y = parseInt(state.y);
    if (y < -5 || y > 5) {
        throw new InvalidValueException("y is out of bounds (allowed range -5..5)")
    }

    if (isNaN(state.r)) {
        console.log("invalid r invalid")
        throw new InvalidValueException("invalid r value")
    }
}
