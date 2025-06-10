{
	
    let userMessage,accountDetail,addressBook,transaction,accountList,pageOrchestrator = new PageOrchestrator();
	 
	 
	 window.addEventListener("load", () => {
	    if (sessionStorage.getItem("username") == null) {
	      window.location.href = "index.html";
	    } else {
	      pageOrchestrator.start(); // initialize the components
	      pageOrchestrator.refresh();
	    } // display initial content
	  }, false);
	
	
	function UserMessage(p_id , name,surname){
		this.p_id = p_id;
		this.name = name;
		this.surname = surname;
		this.show= function(){
			this.p_id.textContent =	"Welcome "+ name +" "+surname;
		};
		
	}
	
	function AccountList(account_table,account_body){
		this.account_table = account_table;
		this.account_body = account_body;

	 this.show = function(next) {
	      var self = this;
	      makeCall("POST", "GetAccounts", null,
	        function(req) {
	          if (req.readyState == 4) {
	            if (req.status == 200) {
	              var accountsToShow = JSON.parse(req.responseText);
	              self.update(accountsToShow); 
	              if (next) next();
	            
	          } 
              }
	        }
	      );
	    };
	    
		this.update = function(accounts) {
	      var row, destcell, datecell, linkcell, anchor,select,option;
	      this.account_body.innerHTML = ""; 
	      var self = this;
	      select = document.getElementById("id_account");
	      select.innerHTML="";
	      accounts.forEach(function(account) {
	        row = document.createElement("tr");
	        destcell = document.createElement("td");
	        destcell.textContent = account.id;
	        row.appendChild(destcell);
	        datecell = document.createElement("td");
	        datecell.textContent = account.balance+"\u20AC";
	        row.appendChild(datecell);
	        linkcell = document.createElement("td");
	        anchor = document.createElement("a");
	        linkcell.appendChild(anchor);
	        linkText = document.createTextNode("Click Here");
	        anchor.appendChild(linkText);
	        anchor.setAttribute('accountid', account.id); 
	        anchor.addEventListener("click", (e) => {
	          transaction.hideTransaction();
	          accountDetail.show(e.target.getAttribute("accountid")); 
	        }, false);
	        anchor.href = "#";
	        row.appendChild(linkcell);
	        self.account_body.appendChild(row);
	        option = document.createElement("option");
	        option.setAttribute("value",account.id);
	        option.appendChild(document.createTextNode(account.id));
	       	select.appendChild(option);
	       	
	      });

	      this.account_table.style.visibility = "visible";
	    }
	      this.autoclick = function(accountId) {
	      var e = new Event("click");
	      var selector = "a[accountid='" + accountId + "']";
	      var anchorToClick =  
	        (accountId) ? document.querySelector(selector) : this.account_body.querySelectorAll("a")[0];
	      if (anchorToClick) anchorToClick.dispatchEvent(e);
	      transaction.showTransaction();
	  }
	    
	    
	}
	
	function AccountDetail(alert,transaction_table,transaction_body,transaction_title,id_account_source) {
	    this.alert = alert;
	    this.transaction_table = transaction_table;
	    this.transaction_body = transaction_body; 
	    this.transaction_title = transaction_title;
	    this.id_account_source = id_account_source;
	    this.accountsUsed = undefined;
	    
		
	    this.show = function(accountid) {		
	      var self = this;
	      makeCall("GET", "GetAccountDetails?accountid=" +accountid, null,
	        function(req) {
	          if (req.readyState == 4) {
	            if (req.status == 200) {
	              var obj = JSON.parse(req.responseText);
				  self.transaction_title.textContent="Transactions table of account number " + accountid;
	              self.accountsUsed = obj.accounts; 
	              self.lastAccount = accountid;
	              self.update(obj.transactions,accountid); 
	            } else if (req.status == 403) {
                          	window.location.href = req.getResponseHeader("Location");
                  			window.sessionStorage.removeItem('username');
							window.sessionStorage.removeItem('name');    
                			window.sessionStorage.removeItem('surname');
                  }
                  else {
	              	self.alert.textContent = req.responseText;
					self.transaction_title.textContent="Transactions table of account number " + lastAccount;
	            }
	          transaction.hideAddMoneyLabel();
	          }
	        }
	      );
	    };

	    this.update = function(accountTransactions,accountid) {
	      var row, cell_other,idcell,detailscell,date_p,amount_p,description_p,sign;
	      this.transaction_body.innerHTML = ""; 
	      this.accountid = accountid;
	      var self = this;
	      var username="";
	      if(accountTransactions!= null){
				this.alert.textContent="";
		      	accountTransactions.forEach(function(transaction) { 
		        row = document.createElement("tr");
		        
		        cell_other = document.createElement("td");
		        
		        if(transaction.sourceAccount != accountid){
						for(const a of accountDetail.accountsUsed ){
						if(a.id == transaction.sourceAccount){
							username = a.username;
						}
					}
		        	cell_other.textContent = "From "+username+"("+transaction.sourceAccount+")";
		        	sign="+";
		        }
		        else{
					for(const a of accountDetail.accountsUsed ){
						if(a.id == transaction.destinationAccount){
							username = a.username;
						}
					}
					sign="-";
		        	cell_other.textContent = "To " +username +"("+transaction.destinationAccount+")";
		        }
		        
		        row.appendChild(cell_other);
		        
		        detailscell = document.createElement("td");
		        date_p = document.createElement("p");
		        date_p.textContent = "Date: " +transaction.date;
		        detailscell.appendChild(date_p);
		        amount_p = document.createElement("p");
		        
		        
		        amount_p.textContent = "Amount: "+sign+""+transaction.amount +"\u20AC";
		        if(transaction.sourceAccount != accountid){
		        	amount_p.style.color ="green";
		        }
		        else{
		        	amount_p.style.color ="red";
		        }
		        
		        detailscell.appendChild(amount_p);
		        description_p = document.createElement("p");
		        description_p.textContent = "Description: "+ transaction.description;
		        detailscell.appendChild(description_p);
		        row.appendChild(detailscell);
		        
		        idcell = document.createElement("td");
		        idcell.textContent = transaction.id;
		        row.appendChild(idcell);
		        
		        
		        self.transaction_body.appendChild(row);
		      });
	     	this.transaction_table.style.visibility = "visible";
			this.transaction_table.getElementsByTagName("thead")[0].style.visibility = "visible";
			this.transaction_title.style.visibility = "visible";
	    }
	    else{
			this.transaction_table.style.visibility = "hidden";
			this.transaction_title.style.visibility = "hidden";
			this.transaction_table.getElementsByTagName("thead")[0].style.visibility = "hidden";
			
			this.alert.textContent="Not found any transactions in the account number "+accountid;
		}
		this.id_account_source.textContent=accountid;
	    }
	}
	
	
	function Transaction(id_account_source,id_user,id_account_destination,description,amount,transaction_error,button,tab,div_transaction,img_correct,img_error,button_add_money,amount_deposit,account_deposit,add_money_error){
		this.id_account_source = id_account_source; 
		this.id_user = id_user;
		this.id_account_destination = id_account_destination;
		this.description = description;
		this.amount =amount;
		this.transaction_error =transaction_error;
		this.button = button;
		this.div_transaction = div_transaction; 
		this.obj = undefined;
		this.amount_source=-1;
		this.img_correct = img_correct;
		this.img_error = img_error;
		this.img_correct.style.visibility = "hidden";
		this.img_error.style.visibility = "hidden";
		this.button_add_money=button_add_money;
		this.amount_deposit = amount_deposit; 
		this.account_deposit = account_deposit;
		this.add_money_error = add_money_error;
		this.id_user.addEventListener("focus", e => {
            addressBook.autocompleteDestinationUsername(e.target.value);
        });
        this.id_user.addEventListener("keyup", e => {
            addressBook.autocompleteDestinationUsername(e.target.value);  
        });
        this.id_account_destination.addEventListener("focus", e => {
            addressBook.autocompleteDestinationAccount(this.id_user.value, e.target.value, this.id_account_source.value);
        });
        this.id_account_destination.addEventListener("keyup", e => {
            addressBook.autocompleteDestinationAccount(this.id_user.value, e.target.value, this.id_account_source.value);
        });
		
		
		this.initialize = function (){
		/**Initialize add money button */
		button_add_money.addEventListener('click', (e) => {
			transaction.hideTransaction();
			var self = this; 
			var form = e.target.closest("form");
    		if (form.checkValidity()) {
				if(amount_deposit.value<=0){
					form.reset();
					this.add_money_error.textContent = "Ammount cannot be 0 or negative!";
					return;
				}
				else{
					var find = false;
					if(!(tab === undefined)){
					/**Find the account id */
						var n = tab.rows.length;
						for(i = 0;i<n;i++){			
							cell = tab.rows.item(i).cells;
							const selectedValue = [].filter
                				.call(this.account_deposit.options, option => option.selected)
                				.map(option => option.text);
							if(cell.item(0).innerHTML == selectedValue){
								find = true;
								break;
							}
						}	
					}
					if(!find){
						this.add_money_error.textContent = "Error account id does not corrispond!";
					}
					else{
		      			makeCall("POST", 'AddMoney', form,
				        function(req) {
				          if (req.readyState == XMLHttpRequest.DONE) {
				            switch (req.status) {
				              case 200:
				              	accountList.show();
				              	self.add_money_error.textContent = "";
				              	break;
				              default: // bad request and internal server error
				                self.add_money_error.textContent = req.responseText;
				                break;  
				            }
				          }
				        }
		      );}}
		    } else {
				form.reportValidity();
		    }
		  });
		
		
	
		this.button.addEventListener('click', (e) => {
   	 		
   	 		var self = this;
   	 		var form = e.target.closest("form");
    		if (form.checkValidity()) {
						if(!(tab === undefined)){
					/**Find the amount of the id */
						var n = tab.rows.length;
						for(i = 0;i<n;i++){
								
							cell = tab.rows.item(i).cells;
							if(cell.item(0).innerHTML == id_account_source.textContent){
								this.amount_source = cell.item(1).innerHTML;
								break;
							}
						}

						
					}		
	
				if(this.id_account_source.textContent == this.id_account_destination.value){
					form.reset();
					this.hideTransaction();
					this.img_error.style.visibility = "visible";
					this.transaction_error.textContent="The destination account can not be the source account!";
					return;
				}
				else{
					if(isNaN(this.amount.value)||this.amount.value<=0){
						form.reset();
						this.hideTransaction();
						this.transaction_error.textContent="Ammount cannot be 0 or negative!";
						this.img_error.style.visibility = "visible";
						return;
					}
					else{
						if(Number(this.amount.value)>Number(this.amount_source)){
							form.reset();
							this.hideTransaction();
							this.transaction_error.textContent="Not enough money in this account!";
							this.img_error.style.visibility = "visible";
							return;
						}
						else{
							
			      			var data = new FormData();
			      			data.append("idUser",id_user.value);
			      			data.append("idAccount",id_account_destination.value);
			      			data.append("description",description.value);
			      			data.append("amount",amount.value);
			      			data.append("idThis",id_account_source.textContent);
			      			form.reset();
			      			makeCall("POST", 'CheckTransaction', data,
					        function(req) {
					          if (req.readyState == XMLHttpRequest.DONE) {
					            switch (req.status) {
					              case 200:
					              	if(req.responseText!=''){
	                        			self.obj = JSON.parse(req.responseText);
										accountList.show(function() {
		        							accountList.autoclick(self.obj.sourceAccount); 
		        						}); 
	        						}
					              	break;
					              default: // bad request and internal server error
					              	self.hideTransaction();
					              	self.img_error.style.visibility = "visible";
					                self.transaction_error.textContent = req.responseText;
					                break;  
				            }
				          }
				        }
				      );
			      	}
			      }
		      }
		      
		    } else {
				form.reportValidity();
		    }
		   }
		   
		   );
		};
		
		this.hideAddMoneyLabel=function(){
			this.add_money_error.textContent = "";	
		}
		
		
		
		this.showTransaction= function(){
			if(this.obj!=undefined){
				var sourceUser, sourceAccount ,destinationUser,destinationId,description,amount,moneyBeforeTransactionS,moneyAfterTransactionS,moneyBeforeTransactionD,moneyAfterTransactionD;
				
					
				this.transaction_error.textContent="Transaction Completed!";
				this.div_transaction.textContent = "";
				
				
				sourceUser = document.createElement("p");
				sourceAccount = document.createElement("p");
				destinationUser = document.createElement("p");
				destinationId = document.createElement("p");
				description = document.createElement("p");
				amount = document.createElement("p");
				moneyBeforeTransactionS = document.createElement("p");
				moneyAfterTransactionS = document.createElement("p");
				moneyBeforeTransactionD = document.createElement("p");
				moneyAfterTransactionD= document.createElement("p");
				
				sourceUser.textContent = "Money sent from: "+this.obj.sourceUser;
				sourceAccount.textContent = "Account used to send: "+ this.obj.sourceAccount;
				destinationUser.textContent = "Money receive by: "+ this.obj.destinationUser;
				destinationId.textContent = "Account used to receive: " + this.obj.destinationId;
				description.textContent = "Description: " + this.obj.description;
				amount.textContent =  "Amount: "+this.obj.amount;
				moneyBeforeTransactionS.textContent = "Money of "+this.obj.sourceUser+" before transaction: "+this.obj.moneyBeforeTransactionS;
				moneyAfterTransactionS.textContent = "Money of "+this.obj.sourceUser+" after transaction: "+this.obj.moneyAfterTransactionS;
				moneyBeforeTransactionD.textContent = "Money of "+this.obj.destinationUser+" before transaction: "+this.obj.moneyBeforeTransactionD;
				moneyAfterTransactionD.textContent = "Money of "+this.obj.destinationUser+" after transaction: "+this.obj.moneyAfterTransactionD;
				this.div_transaction.appendChild(sourceUser);
				this.div_transaction.appendChild(sourceAccount);
				sourceAccount.setAttribute("sourceAccount",sourceAccount);
				this.div_transaction.appendChild(destinationUser);
				this.div_transaction.appendChild(destinationId);
				this.div_transaction.appendChild(description);
				this.div_transaction.appendChild(amount);
				this.div_transaction.appendChild(moneyBeforeTransactionS);
				this.div_transaction.appendChild(moneyAfterTransactionS);
				this.div_transaction.appendChild(moneyBeforeTransactionD);
				this.div_transaction.appendChild(moneyAfterTransactionD);
				this.img_correct.style.visibility = "visible";
				addressBook.showButton(this.obj.destinationUser,this.obj.destinationId);
			}
		}
		this.hideTransaction=function(){
			this.transaction_error.textContent="";
			this.div_transaction.textContent = "";
			addressBook.hideButton();
			this.img_correct.style.visibility = "hidden";
			this.img_error.style.visibility = "hidden";
		}	
	}




	function AddressBook(contact_message,user_datalist,account_datalist,button,div_button){
		
		this.contact_message = contact_message;
		this.user_datalist = user_datalist;
		this.account_datalist = account_datalist;
		this.button = button;
		this.contacts = [];
		this.div_button = div_button;
		this.div_button.style.visibility = "hidden";
		var self = this;
				
		this.button.addEventListener('click', () => {
			
			var data = new FormData();
			data.append("sourceAccount",transaction.obj.sourceAccount);
			data.append("destinationUser",transaction.obj.destinationUser);
			data.append("destinationId",transaction.obj.destinationId);

			makeCall("POST", 'AddContact', data,
					        function(req) {
					          if (req.readyState == XMLHttpRequest.DONE) {
					            switch (req.status) {
					              case 200:
										transaction.hideTransaction();
                        				self.load();
					              	break;
					              case 400:
					              case 404:	
					              default: 
					              		transaction.hideTransaction();
					                	self.contact_messages=req.responseText;
					                break;  
				            }
				          }
				        }
				      );
			});

		
		
		
			
		
		
		this.load = function(){
            makeCall("POST", "GetAddressBook", null, (req) => {
                switch(req.status){
                    case 200: //ok
                        self.contact_message.textContent = "";
                        if(req.responseText!=''){
                        	self.contacts = JSON.parse(req.responseText);
                        }
                        break;
                    case 400: // bad request
                    case 401: // unauthorized
                    case 500: // server error
                    default: //Error
                        self.contact_message.textContent = "Unable to load your contacts";
                        break;
                }
            });
        };
        
		
        this.autocompleteDestinationUsername = function(text_username){
            this.user_datalist.innerHTML = "";
            this.account_datalist.innerHTML = "";
            //Get dest match
			if(!(this.contacts == null||this.contacts == undefined)){
	            var usernames = Object.keys(this.contacts);
	            if (!usernames.includes(text_username)){ 
	                let similarUsernames = [];
	                usernames.forEach(username => {
	                    if (String(username).startsWith(text_username)) 
	                        similarUsernames.push(username); 
	                });
	                similarUsernames.forEach(similar => {
	                    let option = document.createElement("option");
	                    option.text = similar;
	                    option.value = similar;
	                    this.user_datalist.appendChild(option);
	                });
	            }
	       }
        }
		this.autocompleteDestinationAccount = function(text_username, account_id, current_account){
            this.user_datalist.innerHTML = "";
            this.account_datalist.innerHTML = "";
            if(!(this.contacts == null||this.contacts == undefined)){
	            var usernames = Object.keys(this.contacts);
	            
	            if (usernames.includes(text_username)){ 
	                let accounts = this.contacts[text_username];
	                if (!accounts.includes(account_id)){ 
	                    let similarAccounts = [];
	                    accounts.forEach(account => {
	                        if (String(account).startsWith(account_id) && account != current_account) 
	                            similarAccounts.push(account);
	                    });
	                    similarAccounts.forEach(account => {
	                        let option = document.createElement("option");
	                        option.text = account;
	                        option.value = account;
	                        this.account_datalist.appendChild(option);
	                    });
	                }
	            }
            }
        }
        
        this.showButton = function(username,accountId){
			if(!(this.contacts == null||this.contacts == undefined)){
				var dest = Object.keys(this.contacts);
				if (dest.includes(username)){ 
					let accounts = this.contacts[username];
					if(!accounts.includes(accountId)){
						this.div_button.style.visibility = "visible";
					}
					else{
						this.div_button.style.visibility = "hidden";
					}
				}
				else{
					this.div_button.style.visibility = "visible";
				}
			}
			else{
				this.div_button.style.visibility = "visible";
			}
		}
		this.hideButton=function (){
			this.div_button.style.visibility = "hidden";
		}
        
        
        
        
		
		
	}
	  function PageOrchestrator() {
	    
	    this.start = function() {
		
		userMessage = new UserMessage(
			document.getElementById("user_message"),
			sessionStorage.getItem('name'),
			sessionStorage.getItem('surname')
		);
		
			/** 
			Initialize new account button
			 */
		document.getElementById("new_account_button").addEventListener('click', (e) => {
			transaction.hideTransaction();
   	 		var form = e.target.closest("form");
    		if (form.checkValidity()) {
      			makeCall("POST", 'CreateNewAccount',null,
		        function(req) {
		          if (req.readyState == XMLHttpRequest.DONE) {
		            switch (req.status) {
		              case 200:
		              	accountList.show();
		              	break;
		              case 400: // bad request
		                document.getElementById("new_account_error").textContent = req.responseText;
		                break;
		            }
		          	transaction.hideAddMoneyLabel();
		          }
		        }
		      );
		    } else {
		    	 form.reportValidity();
		    }
		  });
  			addressBook = new AddressBook(
				document.getElementById("contact_message"),
				document.getElementById("user_datalist"),
				document.getElementById("account_datalist"),
				document.getElementById("add_contact"),
				document.getElementById("div_button_contact")
			);
  			
	      	accountList = new AccountList(
	        document.getElementById("account_table"),
	        document.getElementById("account_body")
	        );
	     	accountDetail = new AccountDetail(
			document.getElementById("id_alert"),
			document.getElementById("transaction_table"),
			document.getElementById("transaction_body"),
			document.getElementById("transaction_title"),
			document.getElementById("id_account_source")
			);
			transaction = new Transaction(
			document.getElementById("id_account_source"),
			document.getElementById("id_user"),
			document.getElementById("id_account_destination"),
			document.getElementById("description"),
			document.getElementById("amount"),
			document.getElementById("transaction_error"),
			document.getElementById("send_money_button"),
			document.getElementById("account_table"),
			document.getElementById("transaction_details"),
			document.getElementById("img_correct"),
			document.getElementById("img_error"),
			document.getElementById("add_money_button"),
			document.getElementById("number"),
			document.getElementById("id_account"),
			document.getElementById("add_money_error")
			);  
			document.querySelector("a[href='Logout']").addEventListener('click', () => {
	        window.sessionStorage.removeItem('username');
	        window.sessionStorage.removeItem('name');
	        window.sessionStorage.removeItem('surname');
	      })
			
			
	    };
		
		
	    this.refresh = function(currentAccount) {
			userMessage.show();
			addressBook.load();
	      	transaction.initialize();
	      	accountList.show(function() {
	        	accountList.autoclick(currentAccount); 
	        });			
	    };
	  }
	  
};