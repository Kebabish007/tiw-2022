
(function() { 

	var registration_button = document.getElementById("registration_button");
	var password = registration_button.closest("form").querySelector('input[name="password"]');
	var repeat_password = registration_button.closest("form").querySelector('input[name="passwordRepeat"]');
	var email = registration_button.closest("form").querySelector('input[name="email"]');
	var message_registration = document.getElementById("registration_error");
	var mail_format = /^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$/;

  
	  registration_button.addEventListener('click', (e) => {
	    var form = e.target.closest("form");
		if (form.checkValidity()) {
	    	if(password.value!=repeat_password.value){
				message_registration.textContent = "Password and repeat password are not equal";
			}
	        else{
				if(email.value.match(mail_format)){
		      makeCall("POST", 'CheckRegistration', e.target.closest("form"),
		        function(req) {
		          if (req.readyState == XMLHttpRequest.DONE) {
		            switch (req.status) {
		              case 200:
		                window.location.href = "index.html";
		                break;
		              case 400: // bad request
		              case 500: // server error
		              default:
		                message_registration.textContent = req.responseText;
						break
		            }
		          }
		        }
		      );
		      }
		      else{
					message_registration.textContent = "Not an invalid mail!";
				}
			}
		    } else {
		    	 form.reportValidity();
		    }
	  });

})();