
	function makeCall(method, url, formElement, cback, reset = true) {
	    var req = new XMLHttpRequest(); // visible by closure
	    req.onreadystatechange = function() {
	      cback(req)
	    }; // closure
	    
	    req.open(method, url,true);
	    if (formElement == null) {
	        req.send(); 
	    } else if (formElement instanceof FormData){
	        req.send(formElement); 
	    } else {
	        req.send(new FormData(formElement)); 
	    }
	    
	    if (formElement !== null &&!(formElement instanceof FormData)&& reset === true) {
	      formElement.reset();
	    }
}
