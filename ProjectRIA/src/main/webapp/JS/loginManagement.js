
(function() { 

  document.getElementById("login_button").addEventListener('click', (e) => {
    var form = e.target.closest("form");
    if (form.checkValidity()) {
      makeCall("POST", 'CheckCredentials', e.target.closest("form"),
        function(req) {
          if (req.readyState == XMLHttpRequest.DONE) {
            switch (req.status) {
              case 200:
                var user = JSON.parse(req.responseText);
                sessionStorage.setItem('name', user.name);    
                sessionStorage.setItem('surname', user.surname);
                sessionStorage.setItem('username', user.username);  
                window.location.href = "Home.html";
                break;
              case 400: // bad request
                document.getElementById("login_error").textContent = req.responseText;
                break;
              case 401: // unauthorized
                  document.getElementById("login_error").textContent = req.responseText;
                  break;
              case 500: // server error
            	document.getElementById("login_error").textContent = req.responseText;
                break;
            }
          }
        }
      );
    } else {
    	 form.reportValidity();
    }
  });

})();