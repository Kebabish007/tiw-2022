package it.polimi.tiw.projectRIA.beans;

import java.util.HashMap;
import java.util.Set;

public class AddressBook{

	
	private HashMap<String,Set<Integer>> contacts;
	
	public AddressBook() {
		contacts = new HashMap<String,Set<Integer>>();
	}
	
	public AddressBook(HashMap<String,Set<Integer>> contacts) {
		this.contacts = contacts;
	}
	
	public void setContacts(HashMap<String,Set<Integer>> contacts)
	{
		this.contacts = contacts;
	}
	
	public HashMap<String,Set<Integer>> getContacts() {
		return contacts;
	}
}
