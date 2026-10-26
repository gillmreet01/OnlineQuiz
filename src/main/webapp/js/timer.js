/*
 * 10-second countdown for one question (the ONLY JavaScript in the project).
 *
 * The server tells us how many milliseconds are left (data-millis on #timer),
 * so refreshing the page does not give the student extra time.
 * When the time reaches 0 we submit the form automatically; the server then
 * records the selected option (or "no answer") and sends the next question.
 */
(function () {
  var timerBox = document.getElementById("timer");
  var timerLabel = document.getElementById("timerLabel");
  var timerFill = document.getElementById("timerFill");
  var form = document.getElementById("quizForm");
  var nextBtn = document.getElementById("nextBtn");
  if (!timerBox || !form) { return; }

  var totalMillis = parseInt(timerBox.getAttribute("data-total"), 10);
  var millisLeft = parseInt(timerBox.getAttribute("data-millis"), 10);
  var deadline = Date.now() + millisLeft;   // the moment the time is up
  var submitted = false;                    // stops us from submitting twice

  function submitOnce() {
    if (submitted) { return; }
    submitted = true;
    nextBtn.disabled = true;
    form.submit();
  }

  // Student clicked Next
  form.addEventListener("submit", function () {
    submitted = true;
    nextBtn.disabled = true;
  });

  // Animate the thin bar from its current width down to 0 over the remaining time
  timerFill.style.width = (millisLeft / totalMillis * 100) + "%";
  void timerFill.offsetWidth;               // forces the browser to apply the width above first
  timerFill.style.transition = "width " + millisLeft + "ms linear";
  timerFill.style.width = "0%";

  // Update the number 4 times a second (smooth, and still accurate)
  function tick() {
    var left = deadline - Date.now();
    if (left <= 0) {
      timerLabel.textContent = "0s";
      submitOnce();
      return;
    }
    var seconds = Math.ceil(left / 1000);
    timerLabel.textContent = seconds + "s";
    if (seconds <= 3) { timerBox.classList.add("danger"); }
  }
  tick();
  setInterval(tick, 250);
})();
