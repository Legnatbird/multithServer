'use strict';

let $ = (e) => document.querySelector(e)

function displayMessage(message) {
    $(".message").textContent = message
}

$(".number").value = Math.trunc(Math.random() * 20) + 1

$(".check").addEventListener("click", () => {
    const guess = Number($(".guess").value)
    const number = $(".number")
    $(".guess").click()
    if (!guess) displayMessage("No number")
    if (guess !== number.value && (guess)) {
        (guess > number.value) ? displayMessage("Too high!"): displayMessage("Too low!")
        $(".score").textContent--
    } else if (guess) {
        number.textContent = number.value
        displayMessage("Correct number!!")
        $(".check").disabled = true;
        $("body").style.backgroundColor = "#60b347";
        ($(".highscore").textContent > $(".score").textContent) ? $(".highscore").textContent: $(".highscore").textContent = $(".score").textContent
    }
    if ($(".score").textContent == 0) {
        number.textContent = number.value
        $("body").style.backgroundColor = "#9d3d3d"
        $(".score").textContent = 1
        displayMessage("You lost! XD")
        $(".check").disabled = true;
    }
})
$(".again").addEventListener("click", () => {
    $("body").style.backgroundColor = "#222"
    $(".guess").value = null
    $(".number").textContent = "?"
    $(".number").value = Math.trunc(Math.random() * 20) + 1
    displayMessage("Start guessing...")
    $(".score").textContent = 20
    $(".check").disabled = false;
})