/**
 * storage.js, (c) 2011, Immanuel Albrecht; Dresden University of Technology,
 * Professur für die Psychologie des Lernen und Lehrens
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * creates a new Storage object that takes care of storing and reloading
 * information
 */

function Storage() {
	this.dataFields = [];
	this.dataFieldNames = [];

	/**
	 * registers a data field with a unique name, data field objects should
	 * provide:
	 * 
	 * .GetValue() which returns a string representation of the current state data
	 * 
	 * .SetValue(v) which restores a state given by a previously returned state
	 * data string
	 */

	this.RegisterField = function(datafield, name) {
		/**
		 * try to fix name collisions
		 */

		var collision = true;
		var append_nbr = 0;
		var good_name = name;

		while (collision == true) {
			collision = false;

			for ( var int = 0; int < this.dataFieldNames.length; int++) {
				if (this.dataFieldNames[int] == good_name) {
					collision = true;
					append_nbr = 1 + append_nbr;
					good_name = name + "(" + append_nbr + ")";
					break;
				}
			}
		}

		this.dataFields.push(datafield);
		this.dataFieldNames.push(good_name);
	};

	/**
	 * stores all data in the storage space using name to avoid collisions,
	 * storage can be sessionStorage or localStorage
	 */

	this.StoreIn = function(storage, name) {
		if (typeof myHover == "object") {
			if (myHover.CrashDown) {
				myHover.CrashDown();
			}
		}

		for ( var int = 0; int < this.dataFieldNames.length; int++) {
			var obj = this.dataFields[int];

			if (obj.GetValue) {
				var v = obj.GetValue();
				var keyname = "myStorage"+name + this.dataFieldNames[int];
				
				storage.setItem(keyname, v);
				
			}
			
		}
	};
	
	/**
	 * restores all data in the storage space using name to avoid collisions,
	 * storage can be sessionStorage or localStorage
	 */

	this.RestoreFrom = function(storage, name) {
		if (typeof myHover == "object") {
			if (myHover.CrashDown) {
				myHover.CrashDown();
			}
		}

		for ( var int = 0; int < this.dataFieldNames.length; int++) {
			var obj = this.dataFields[int];

			if (obj.SetValue) {
				var keyname = "myStorage"+name + this.dataFieldNames[int];
				var v = storage.getItem(keyname);
				
				if (v !== null) {
					obj.SetValue(v);
				}
			}
			
		}
	};	
	
	/**
	 * set up automatic storage and restoring of contents
	 */
	this.SetupAutoRestore = function(storage, name) {
		myStorageLocation = storage;
		myStorageName = name;
		
		myStorageAncientUnloader = window.onunload;
		
		window.onunload = function() {
			myStorage.StoreIn(myStorageLocation, myStorageName);
			
			if (myStorageAncientUnloader) {
				myStorageAncientUnloader();
			}
		};
		
		myStorage.RestoreFrom(myStorageLocation, myStorageName);		
	};
}

myStorage = new Storage();