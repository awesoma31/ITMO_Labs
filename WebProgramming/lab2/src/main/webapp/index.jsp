<%@ page contentType="text/html;charset=UTF-8" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <title>Web lab1</title>

  <link rel="stylesheet" href="styles/reset.css">
  <link rel="stylesheet" href="styles/main.css">
  <script defer src="index.js"></script>
</head>
<body>

  <div class="background-top"> </div>
  <div class="content">
    <nav class="navbar">
      <div id="info">
        Alexander Churakov P3231, var. 669
      </div>
      <a href="https://github.com/awesoma31" target="_blank" id="github">github</a>
    </nav>

    <main class="container">
      <section>
        <div id="error" hidden></div>
      <form action="${pageContext.request.contextPath}/controller" method="get" id="data-form">
        <fieldset id="xs">
          <legend>Select X:</legend>
          <label><input type="checkbox" name="x" onclick="return checkX();" value="-2">-2</label>
          <label><input type="checkbox" name="x" onclick="return checkX();" value="-1.5">-1.5</label>
          <label><input type="checkbox" name="x" onclick="return checkX();" value="-1">-1</label>
          <label><input type="checkbox" name="x" onclick="return checkX();" value="-0.5">-0.5</label>
          <label><input type="checkbox" name="x" onclick="return checkX();" value="0">0</label>
          <label><input type="checkbox" name="x" onclick="return checkX();" value="0.5">0.5</label>
          <label><input type="checkbox" name="x" onclick="return checkX();" value="1">1</label>
          <label><input type="checkbox" name="x" onclick="return checkX();" value="1.5">1.5</label>
          <label><input type="checkbox" name="x" onclick="return checkX();" value="2">2</label>
          <label><input type="checkbox" name="x" onclick="return checkX();" id="graphCheckbox"><span id="graphValue"></span></label>
        </fieldset>

        <fieldset id="ys">
          <legend>Select Y:</legend>
          <label for="y">Enter Y:</label>
          <input type="number" id="y" name="y" required>
        </fieldset>


        <fieldset id="rs">
          <legend>Select R:</legend>
            <label><input type="radio" name="r" value="1">1</label>
            <label><input type="radio" name="r" value="1.5">1.5</label>
            <label><input type="radio" name="r" value="2">2</label>
            <label><input type="radio" name="r" value="2.5">2.5</label>
            <label><input type="radio" name="r" value="3">3</label>
        </fieldset>

        <button type="submit">Submit</button>
      </form>
      </section>

      <section>
        <canvas id="coordinatePlane" width="250" height="250" style="border:1px solid #000000;"></canvas>
        <p id="message"></p>
      </section>

    </main>
  </div>
  <footer id="copyright">pgLangInspired, 2024</footer>
  <div class="background-bot"></div>
</body>
</html>