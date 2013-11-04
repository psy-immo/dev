/**
 * timer.js, (c) 2013, Immanuel Albrecht; Dresden University of Technology,
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

var timerIdCounter = 0;
var timerArray = [];
var timerNames = {};

/**
 * 
 * @param name   name of the timer object
 * @param type   0 .. count down
 *                1 .. count up
 * @param value  initial timer value, in seconds
 */

function Timer(name, type, value) {
	
	this.id = timerIdCounter++;
	
	if (name)
		this.name = name;
	else 
		this.name = "timer"+this.id;
	
	/**
	 * NOTE: although the value parameter is in seconds,
	 * the value member will be in milliseconds, since this
	 * is the native interval measure for Date objects
	 */
	
	if (value)
		this.value = (parseInt(value)*1000);
	else
		this.value = 0;
	
	if (this.type)
		this.type = type;
	else
		this.type = 0; //default to count down
	
	/**
	 * refering date
	 */
	
	this.reference = new Date();
	
	/**
	 * this function is called on each myStorage.StoreIn run
	 */
	this.Tick = function() {
		var now = new Date();
		
		var delta = now - this.reference;
		
		if (this.type) {
			this.value += delta;
		} else {
			if (this.value > 0) {
				this.value -= delta;
				
				if (this.value <= 0) {
					myLogger.Log("Timer " + this.name +" elapsed.");
					this.value = 0;
				}				
			}			
		}
		
		this.reference = now;
	};
	
	/**
	 * return the current contents of the input field as string
	 */
	this.GetValue = function() {
		/**
		 * update the value
		 */
		this.Tick();
		/**
		 * return it
		 */
		return this.value;
	};

	/**
	 * restore the input field from string
	 */

	this.SetValue = function(contents) {
		this.value = parseInt(contents);
		this.reference = new Date();
	};
	
	
	timerArray.push(this);
	timerNames[this.name] = this;
	
	myStorage.RegisterField(this, "timerArray[" + this.id + "]");
	myStorage.RequestTicks(this);

	return this;
}
